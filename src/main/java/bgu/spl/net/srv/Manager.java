package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;
import bgu.spl.net.srv.objects.MessageNotification;
import bgu.spl.net.srv.objects.PM;
import bgu.spl.net.srv.objects.Post;
import bgu.spl.net.srv.objects.User;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Manager<T> {
    ConnectionImpl<T> connection;
    ConcurrentHashMap<String, Integer> userNamesAndIds;
    List<String> filteredWords;
    ConcurrentHashMap<Integer, User> idsAndUsers;

    public Manager(Connections<T> connection){
        this.connection = (ConnectionImpl)connection;
        userNamesAndIds = new ConcurrentHashMap<>();
        idsAndUsers = new ConcurrentHashMap<>();
        
        //enter words to be filtered
        this.filteredWords = new ArrayList<>();
        filteredWords.add("hello");

    }

    public void setFilteredWords(List<String> filteredWords) {
        this.filteredWords = filteredWords;
    }

    /**
     * Registers a user to the manager.
     * @param connId
     * @param userName
     * @param password
     * @param birthday
     * @return true if successful, false if failed because it is already registered.
     */
    public boolean register(int connId, String userName, String  password, String birthday){
        Integer existingConnId = userNamesAndIds.putIfAbsent(userName,connId);

        if (existingConnId == null){ //if the userName doesn't already exist
            User user = idsAndUsers.putIfAbsent(connId, new User(userName,password,birthday));
            if(user == null){ //if the connId doesn't already have a user
                return true;
            }else{
                userNamesAndIds.remove(userName,connId);
                return false;
            }
        }else{ //user is registered already
            return false;
        }
    }

    /**
     * does login for the user.
     * @param connId
     * @param username
     * @param password
     * @param captcha
     * @return false if not successful - if captcha is 0, the user is not registered or already logged in
     * or if the password is incorrect.
     */
    public boolean login(int connId, String username, String password, byte captcha){
        if(captcha == '0'){
            return false;
        }
        //if the user logged in again, after logout, it has new connId..
        Integer preID= userNamesAndIds.get(username);
        if(preID==null){ //the user isn't registered
            return false;
        }else if(preID!= connId){
            userNamesAndIds.replace(username, connId);
            User user= idsAndUsers.get(preID);
            idsAndUsers.remove(preID);
            idsAndUsers.put(connId,user);
        }

        User user = idsAndUsers.get(connId);
        if(user == null || !user.getPassword().equals(password) || user.loggedIn){
            return false;
        }else{
            user.loggedIn = true;
            //sends unreceived messages
            for(PM PM : user.unReceivedPMs){
                sendNotification(connId, new MessageNotification((byte) '0', (PM.sender +"\0" +PM.message+ " "+ PM.date+ "\0").getBytes(StandardCharsets.UTF_8)));
                //sendNotification(connId, "9 0 " + PM.sender +"\0" +PM.message + "\0;");
            }
            user.unReceivedPMs.clear();
            for (Post post : user.unreceivedPosts){
                sendNotification(connId, new MessageNotification((byte) '1', (post.sender +"\0" +post.content+"\0").getBytes(StandardCharsets.UTF_8)));
                //sendNotification(connId, "9 1 "+post.sender+ "\0" + post.content + "\0;");
            }
            user.unreceivedPosts.clear();
            return true;
        }
    }

    public void sendNotification(int connId, MessageNotification message){
        ConnectionHandler<Serializable> handler = (ConnectionHandler<Serializable>) connection.getHandler(connId);
        handler.send(message);
    }


    /**
     * user logout.
     * @param connId
     * @return false if user is not registered or is not logged in.
     */
    public  boolean logout(int connId){
        User user = idsAndUsers.get(connId);
        if(user == null || !user.loggedIn){
            return  false;
        }
        user.loggedIn = false;
        return true;
    }

    /**
     * adds the userName given to the user's following list.
     * @param connId
     * @param userName
     * @return false if user is not registered, not logged in, or the userName is already on its following list,
     *          or the user is blocked from the user to follow, or the user blocked the user to follow.
     *          or if the user to follow doesn't exist.
     */
    public boolean follow(int connId, String userName){
        User user = idsAndUsers.get(connId);
        Integer id = userNamesAndIds.get(userName);
        if( user == null || !user.loggedIn || user.getFollowing().contains(userName)|| user.blockedFrom.contains(userName)||
        id == null){
            return false;
        }
        User userToFollow = idsAndUsers.get(userNamesAndIds.get(userName));
        if(userToFollow.blockedFrom.contains(user.getUserName())){ //if the user blocked the userToFollow returns false
            return false;
        }
        user.addToFollowing(userName);
        userToFollow.addToFollowers(user.getUserName());
        return true;
    }

    /**
     * removes the userName given from the user's following list.
     * @param connId
     * @param userName
     * @return false if user is not registered, not logged in, or the userName is already not on its following list,
     *          or the user to unfollow doesn't exist.
     */
    public boolean unfollow(int connId, String userName){
        User user = idsAndUsers.get(connId);
        Integer id = userNamesAndIds.get(userName);
        if( user == null || !user.loggedIn || !user.getFollowing().contains(userName) || id == null){
            return false;
        }
        user.removeFromFollowing(userName);
        User user1 = idsAndUsers.get(userNamesAndIds.get(userName));
        user1.removeFromFollowers(user.getUserName());
        return true;
    }

    /**
     * sends the post to the user's followers, or saves it for when they log in.
     * @param connId
     * @param content
     * @return
     */
    public boolean post(int connId, String content){
        User user = idsAndUsers.get(connId);
        if( user == null || !user.loggedIn){
            return false;
        }
        //finds the users that are tagged
        List<String> tags = new ArrayList<>();
        Pattern pattern = Pattern.compile("@\\w+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()){
            tags.add(matcher.group().substring(1));
        }
        Post post = new Post(content, tags, user.getUserName());
        user.addToPosts(post);
        for(String userName : user.getFollowers()){ //sends a notification to the user's followers
            int id = userNamesAndIds.get(userName);
            User user1 = idsAndUsers.get(id);

            if(!user1.loggedIn){ //saves the post for when it logs in
                user1.addToUnreceivedPosts(post);
            }else{ //send  a notification to the user
                sendNotification(id,new MessageNotification((byte) '1',(user.getUserName()+ "\0"+content+"\0").getBytes(StandardCharsets.UTF_8)));
                //sendNotification(id, "9 1 "+user.getUserName()+ "\0"+content+"\0;");
            }
        }

        for(String userName : tags){ //sends a notification to the tagged users
            if(userNamesAndIds.containsKey(userName)){
                int id = userNamesAndIds.get(userName);
                User user1 = idsAndUsers.get(id);
                if(!user1.loggedIn){
                    user1.addToUnreceivedPosts(post);
                }else{ //send  a notification to the user
                    sendNotification(id,new MessageNotification((byte) '1',(user.getUserName()+ "\0"+content+"\0").getBytes(StandardCharsets.UTF_8)));
                    //sendNotification(id, "9 1 "+user.getUserName()+ "\0"+content+"\0;");
                }
            }
        }
        return true;
    }


    /**
     * sends the pm to the user, or saves it for when it logs in.
     * @param connId
     * @param userName
     * @param content
     * @param date
     * @return
     */
    public boolean PM(int connId, String userName, String content, String date){
        User user = idsAndUsers.get(connId);
        if(userNamesAndIds.get(userName) == null){
            return false;
        }
        User recipient = idsAndUsers.get(userNamesAndIds.get(userName));
        if( user == null || !user.loggedIn || recipient == null || !user.followers.contains(userName)){
            return false;
        }

        for (String word: filteredWords){ //filters the message
            content = content.replaceAll("\\b"+word+"\\b", "<filtered>");
        }

        PM PM = new PM(content,date, user.getUserName());
        if(!recipient.loggedIn){ //saves the PM for when the user is logged in
            recipient.addToUnreceivedPMs(PM);
        }else{ //send a notification to the user
            int id = userNamesAndIds.get(userName);
            sendNotification(id,new MessageNotification((byte) '0',(user.getUserName()+ "\0"+content+ " "+ date+"\0").getBytes(StandardCharsets.UTF_8)));
            //sendNotification(id, "9 0 " + user.getUserName() +"\0" +content + "\0;");
        }
        return true;
    }

    /**
     * returns information about all users that are logged in (and haven't blocked the user).
     * @param connId
     * @return
     */
    public List<byte[]> logStat(int connId){
        User user = idsAndUsers.get(connId);
        if(user == null || !user.loggedIn){
            return null;
        }
        List<byte[]> listOfResponse = new ArrayList<>();
        for(User user1 : idsAndUsers.values()){
            if(user1.loggedIn && !user1.getBlockedFrom().contains(user.getUserName())){

                short age = user1.getAge();
                byte[] response = new byte[8];
                shortToBytes(response,0,age);
                shortToBytes(response,2,(short)(user1.posts.size()));
                shortToBytes(response,4,(short)(user1.followers.size()));
                shortToBytes(response,6,(short)(user1.following.size()));
                listOfResponse.add(response);
            }
        }
        return listOfResponse;
    }

    public void shortToBytes(byte[] bytesArr, int from, short num) {
        bytesArr[from] = (byte) ((num >> 8) & 0xFF);
        bytesArr[from + 1] = (byte) (num & 0xFF);
    }

    /**
     * sends data on the users with userName from the given array. ignores blocked users.
     * @param connId
     * @param userNames
     * @return list of byte arrays containing the optional part of the ack response,
     *          null if user is not logged in or not registered.
     */
    public List<byte[]> stat(int connId, String[] userNames){
        User user = idsAndUsers.get(connId);
        if(user == null || !user.loggedIn){
            return null;
        }
        List<byte[]> listOfResponse = new ArrayList<>();
        for( String userName : userNames){
            Integer id = userNamesAndIds.get(userName);
            if(id != null) {
                User user1 = idsAndUsers.get(id);
                if(!user1.blockedFrom.contains(user.getUserName())) { //doesn't send info on blocked users
                    short age = user1.getAge();
                    byte[] response = new byte[8];
                    shortToBytes(response, 0, age);
                    shortToBytes(response, 2, (short) (user1.posts.size()));
                    shortToBytes(response, 4, (short) (user1.followers.size()));
                    shortToBytes(response, 6, (short) (user1.following.size()));
                    listOfResponse.add(response);
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }
        return listOfResponse;
    }

    /**
     * blocks the given user. after executing this method, both user aren't following each other.
     * @param connId
     * @param userName
     * @return
     */
    public boolean block(int connId, String userName){
        User user = idsAndUsers.get(connId);
        int id =userNamesAndIds.get(userName);
        User userToBlock = idsAndUsers.get(id);
        if(user == null || userToBlock == null || user.blockedFrom.contains(userToBlock.getUserName())){
            return false;
        }
        userToBlock.addToBlockedFrom(user.getUserName());
        user.addToBlockedFrom(userToBlock.getUserName());
        user.getFollowers().remove(userToBlock.getUserName());
        user.getFollowing().remove(userToBlock.getUserName());
        userToBlock.getFollowers().remove(user.getUserName());
        userToBlock.getFollowing().remove(user.getUserName());
        return true;
    }



}
