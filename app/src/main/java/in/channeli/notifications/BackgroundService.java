package in.channeli.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by manohar on 9/1/16.
 */
public class BackgroundService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BackgroundService(String name) {
        super(name);
    }

    Timer timer = new Timer();

    public BackgroundService(){
        super("polling");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final HttpGet httpGet = new HttpGet(intent.getStringExtra("url"));
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME,0);
        httpGet.setHeader("Cookie","csrftoken="+settings.getString("csrftoken",""));
        httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpGet.setHeader("Cookie","CHANNELI_SESSID="+settings.getString("CHANNELI_SESSID",""));

        Log.e("url",intent.getStringExtra("url"));
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {

                               InputStream isr = null;
                               String result="";
                               try{
                                   HttpClient httpClient = new DefaultHttpClient();
                                   HttpResponse response = httpClient.execute(httpGet);
                                   HttpEntity entity = response.getEntity();
                                   isr = entity.getContent();
                               }
                               catch(Exception e){
                                   e.printStackTrace();
                               }
//convert response to string
                               try{
                                   BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
                                   StringBuilder sb = new StringBuilder();
                                   String line = null;
                                   while((line = reader.readLine()) != null){
                                       sb.append(line+"\n");
                                   }
                                   isr.close();
                                   result = sb.toString();
                               }
                               catch(Exception e){
                                   Log.e("log_tag", "Error converting result " + e.toString());
                               }
                               final ResultReceiver resultReceiver = intent.getParcelableExtra("receiver");
                               Bundle bundle = new Bundle();
                               bundle.putString("result", result);
                               resultReceiver.send(1, bundle);
                               //Log.e("result",result);
                           }
                       },100,30000);
    }
}
