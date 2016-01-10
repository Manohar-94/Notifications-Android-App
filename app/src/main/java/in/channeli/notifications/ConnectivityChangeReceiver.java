package in.channeli.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.v4.app.NotificationCompat;

import org.apache.http.client.methods.HttpGet;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/*
 Created by manohar on 10/1/16.
 */
public class ConnectivityChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String result = "";
        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
        if (networkInfo != null && networkInfo.isConnected()) {

            HttpGet httpGet = new HttpGet(MainActivity.UrlofNotification +"fetch?action=first&id=&number=20");

            httpGet.setHeader("Cookie", "csrftoken=" + settings.getString("csrftoken", ""));
            httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpGet.setHeader("Cookie", "CHANNELI_SESSID=" + settings.getString("CHANNELI_SESSID", ""));
            try {
                result = new ConnectTaskHttpGet().execute(httpGet).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            //Log.e("url", intent.getStringExtra("url"));
            /*InputStream isr = null;

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                isr = entity.getContent();
            } catch (Exception e) {
                e.printStackTrace();
            }
//convert response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                isr.close();
                result = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }*/
                    /*final ResultReceiver resultReceiver = intent.getParcelableExtra("receiver");
                    Bundle bundle = new Bundle();
                    bundle.putString("result", result);
                    resultReceiver.send(1, bundle);*/
            //Log.e("result",result);
            Parsing parsing1 = new Parsing();
            NotificationCompat.Builder mBuilder;
            ArrayList<Notification> notifications1 = parsing1.parseNotifications(result);
            int position = 0;
            if (notifications1.size() != 0) {
                int latest_id = settings.getInt("latest_id", 0);
                for (int i = 0; i < notifications1.size(); i++) {
                    if (notifications1.get(i).id <= latest_id) {
                        position = i;
                        break;
                    }
                }
                if (position != 0) {
                    if(position == 1){
                        mBuilder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.notify)
                                .setContentTitle(notifications1.get(0).app)
                                .setContentText(notifications1.get(0).content);
                    }
                    else if (position < notifications1.size()) {
                        mBuilder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.notify)
                                .setContentTitle("notifications")
                                .setContentText("you have " + position + " new notifications");
                    } else{
                        mBuilder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.notify)
                                .setContentTitle("notifications")
                                .setContentText("you have more than "+notifications1.size()+" new notifications");
                    }
                    Intent resultintent = new Intent(context, MainActivity.class);
                    TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
                    taskStackBuilder.addParentStack(MainActivity.class);
                    taskStackBuilder.addNextIntent(resultintent);
                    PendingIntent resultPendingIntent =
                            taskStackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    mBuilder.setAutoCancel(true);
                    NotificationManager mNotificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                    mNotificationManager.notify(0, mBuilder.build());
                }

            }

        }

    }
}