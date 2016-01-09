package in.channeli.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity {

    public static String UrlOfLogin = "https://channeli.in/login/";
    public static String UrlofNotification = "https://channeli.in/notifications/";
    public static final String PREFS_NAME = "MyPrefsFile";

    Notification notification;
    ArrayList<Notification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME,0);
        SharedPreferences.Editor editor = settings.edit();

        HttpGet httpGet = new HttpGet(UrlofNotification+"fetch?action=first&id=&number=20");
        httpGet.setHeader("Cookie","csrftoken="+settings.getString("csrftoken",""));
        httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpGet.setHeader("Cookie","CHANNELI_SESSID="+settings.getString("CHANNELI_SESSID",""));

        Parsing parsing = new Parsing();
        try {
            String result = new ConnectTaskHttpGet().execute(httpGet).get();
            //Log.e("result",result);
            notifications = parsing.parseNotifications(result);
            editor.putInt("latest_id",notifications.get(0).id);
            editor.commit();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ListViewAdapter(this, R.layout.list_node, notifications));

        DownloadResultReceiver resultReceiver = new DownloadResultReceiver(new Handler());
        resultReceiver.setReceiver(new DownloadResultReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                String result = resultData.getString("result");
                NotificationCompat.Builder mBuilder;

                Parsing parsing1 = new Parsing();
                ArrayList<Notification> notifications1 = parsing1.parseNotifications(result);
                if (notifications1.size() != 0) {
                    if (notifications1.get(notifications1.size() - 1).id >= settings.getInt("latest_id", 0)) {
                        mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.notify)
                                .setContentTitle("notifications")
                                .setContentText("you have more than twenty notifications");
                    } else {
                        mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.notify)
                                .setContentTitle(notifications1.get(0).app)
                                .setContentText(notifications1.get(0).content);
                    }
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                    mNotificationManager.notify(0, mBuilder.build());
                }
            }
        });
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this,
                BackgroundService.class);
        intent.putExtra("receiver", resultReceiver);
        intent.putExtra("url", UrlofNotification+"fetch?action=first&id=&number=20");
        startService(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
