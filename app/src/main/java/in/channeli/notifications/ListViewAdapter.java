package in.channeli.notifications;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manohar on 9/1/16.
 */
public class ListViewAdapter extends ArrayAdapter<Notification> {

    private Context context;
    private ArrayList<Notification> notifications;
    private int resource;

    public ListViewAdapter(Context context, int resource, ArrayList<Notification> notifications) {
        super(context, resource, notifications);
        this.context = context;
        this.resource = resource;
        this.notifications = notifications;
    }
    public View getView(int position, View ConvertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View list_view = inflater.inflate(resource, null);
        TextView heading = (TextView) list_view.findViewById(R.id.Heading);
        heading.setText(notifications.get(position).app);
        TextView dateTime = (TextView) list_view.findViewById(R.id.DateTime);
        dateTime.setText(notifications.get(position).datetime);
        TextView description = (TextView) list_view.findViewById(R.id.Description);
        description.setText(Html.fromHtml(notifications.get(position).content));

        return list_view;
    }
}
