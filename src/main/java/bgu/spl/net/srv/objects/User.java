package bgu.spl.net.srv.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class User {
    private String userName;
    private String password;
    private String birthday;
    public List<String> followers;
    public List<String> following;
    public List<Post> posts;
    private List<PM> privateMessages;
    public List<String> blockedFrom; //users that blocked the user
    public List<Post> unreceivedPosts;
    public List<PM> unReceivedPMs;
    public boolean loggedIn = false;

    public User(String userName, String password, String birthday){
        this.userName = userName;
        this.password = password;
        this.birthday = birthday;
        followers = new ArrayList<>();
        following = new ArrayList<>();
        posts = new ArrayList<>();
        privateMessages = new ArrayList<>();
        blockedFrom = new ArrayList<>();
        unreceivedPosts = new ArrayList<>();
        unReceivedPMs = new ArrayList<>();
    }
    public short getAge() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date today = new Date();
        try {
            Date birthdayDate = sdf.parse(birthday);
            long diffInMillies = Math.abs(birthdayDate.getTime() - today.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies,TimeUnit.MILLISECONDS);
            return (short) (diff / 365);
        } catch (ParseException e) {
            return (short)(Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(birthday.substring(6)));
        }


    }
    public void addToFollowers(String user){
        followers.add(user);
    }
    public void addToFollowing(String user){
        following.add(user);
    }
    public void removeFromFollowing(String user){following.remove(user);}
    public void removeFromFollowers(String user){followers.remove(user);}
    public void addToPosts(Post post){
        posts.add(post);
    }
    public void addToPMs(PM pm){
        privateMessages.add(pm);
    }
    public void addToBlockedFrom(String user){
        blockedFrom.add(user);
    }
    public void addToUnreceivedPosts(Post post){
        unreceivedPosts.add(post);
    }
    public void addToUnreceivedPMs(PM pm){
        unReceivedPMs.add(pm);
    }
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
    public String getBirthday() {return birthday; }

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public List<PM> getPrivateMessages() {
        return privateMessages;
    }

    public List<String> getBlockedFrom() {
        return blockedFrom;
    }

    public List<Post> getUnreceivedPosts() {
        return unreceivedPosts;
    }

    public List<PM> getUnReceivedPMs() {
        return unReceivedPMs;
    }
}
