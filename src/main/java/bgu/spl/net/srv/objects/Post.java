package bgu.spl.net.srv.objects;

import java.util.List;

public class Post {
    public String content;
    public List<String> tags;
    public String sender;

    public Post(String content, List<String> tags,String sender){
        this.content = content;
        this.tags = tags;
        this.sender = sender;
    }
}
