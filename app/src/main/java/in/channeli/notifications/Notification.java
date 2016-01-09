package in.channeli.notifications;

/**
 * Created by manohar on 9/1/16.
 */
public class Notification {
    String url;
    String app;
    String datetime;
    String content;
    int id;
    String viewed;

    public Notification(String url,String app,String datetime, String content, int id, String viewed){
        this.id = id;
        this.viewed = viewed;
        this.app = app;
        this.url = url;
        this.datetime = datetime;
        this.content = content;
    }
}
