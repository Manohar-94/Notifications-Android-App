package in.channeli.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import in.channeli.notifications.ConnectTaskHttpGet;
import in.channeli.notifications.ConnectTaskHttpPost;

/*
 Created by manohar on 4/2/15.
 */
public class LoginPage extends Activity{
    //public static final String PREFS_NAME = "MyPrefsFile";
    public String result;
    public int check;
    public String username="", password="", session_key="", msg="", enrollment_no="", name, info;
    public View view;
    public String[] cookie_list_new;
    HttpPost httpPost;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    LayoutInflater inflater;

    EditText usertext,passtext;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        inflater = getLayoutInflater();
        view = inflater.inflate(R.layout.login_page,null);

        Log.e("login_page","inside login page");

        usertext = (EditText) findViewById(R.id.username);
        passtext = (EditText) findViewById(R.id.password);

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                try{
                    processData();
                    //hideKeyboard();
                }
                catch(Exception e){
                    e.printStackTrace();
                    Log.e("log_tag", "error in processData");
                }
            }
        });

    }

    /*public void hideKeyboard(){
        if(view != null) {
            View view = this.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }*/
    public void processData() throws UnsupportedEncodingException {

        username = usertext.getText().toString();
        password = passtext.getText().toString();
        //httpClient = new DefaultHttpClient();
        //httpPost = new HttpPost(MainActivity.UrlOfLogin+"channeli_login/");
        try{
            HttpGet httpGet = new HttpGet(MainActivity.UrlOfLogin);
            String CSRFTOKEN = new CookiesHttpGet().execute(httpGet).get();

            Log.e("csrftoken", CSRFTOKEN);

            httpPost = new HttpPost(MainActivity.UrlOfLogin);
            List<NameValuePair> namevaluepair = new ArrayList<NameValuePair>(2);
            namevaluepair.add(new BasicNameValuePair("username",username));
            namevaluepair.add(new BasicNameValuePair("password",password));
            namevaluepair.add(new BasicNameValuePair("csrfmiddlewaretoken",CSRFTOKEN));
            namevaluepair.add(new BasicNameValuePair("remember_me","on"));
            httpPost.setEntity(new UrlEncodedFormEntity(namevaluepair));
            httpPost.setHeader("Referer", "https://channeli.in/");
            //httpPost.setHeader("X-CSRFToken", cookie_list[0]);
            httpPost.setHeader("Cookie","csrftoken="+CSRFTOKEN);
            httpPost.setHeader("Accept", "application/xml");
            //httpPost.setHeader("Content-type", "application/json");
            //httpPost.setHeader("Content-Type", "multipart/form-data");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            BasicClientCookie csrf_cookie = new BasicClientCookie("csrftoken", CSRFTOKEN);
            csrf_cookie.setDomain(".channeli.in");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            CookieStore cookieStore = httpClient.getCookieStore();
            cookieStore.addCookie(csrf_cookie);

//  Create local HTTP context - to store cookies
            HttpContext localContext = new BasicHttpContext();
//  Bind custom cookie store to the local context
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            //result = new ConnectTaskHttpPost().execute(httpPost).get();
            cookie_list_new = new CookiesHttpPost().execute(httpPost).get();
            Log.e("csrf", cookie_list_new[0]);
            Log.e("sessid", cookie_list_new[1]);

        }
        catch(Exception e){
            e.printStackTrace();

        }
        settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        editor = settings.edit();
        //if()
            editor.putString("csrftoken",cookie_list_new[0]);
            editor.putString("CHANNELI_SESSID",cookie_list_new[1]);
            editor.commit();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        //}
        //else{
            if(!isOnline()){
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Sorry! Could not connect. Check the internet connection!", Toast.LENGTH_SHORT);
                toast.show();
            }
            /*else if(msg.contains("NO")){
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Wrong username or password!", Toast.LENGTH_SHORT);
                toast.show();
            }*/
            /*else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Sorry! Could not login! Try again later!", Toast.LENGTH_SHORT);
                toast.show();
            }*/
            finish();
            //TODO close the app
        }
    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }


    public void onBackPressed(){
        super.onBackPressed();
        //System.exit(0);

        //TODO close the app
    }
}
