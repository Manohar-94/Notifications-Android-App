package in.channeli.notifications;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by manohar on 9/1/16.
 */
public class Parsing {

    public ArrayList<Notification> parseNotifications(String result){
        ArrayList<Notification> notifications = new ArrayList<>();
        Notification notification;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject json;
            JSONArray jsonArray = jsonObject.getJSONArray("notifications");
            for(int i=0; i<jsonArray.length(); i++){
                json = jsonArray.getJSONObject(i);
                notification = new Notification(json.getString("url"), json.getString("app"),
                        json.getString("datetime"), json.getString("content"), json.getInt("id"),
                        json.getString("viewed"));
                notifications.add(notification);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return notifications;
    }
}
