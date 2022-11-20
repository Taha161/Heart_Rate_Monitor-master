package com.example.tejag.heart_rate_monitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
   // public static int i=0;
    Boolean testvar= false;
    private TextView sm,tem,humd;
    public void auto(){
        final long period = 1000;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // do your task here
                new JSONTask().execute("https://api.thingspeak.com/channels/1913677/feeds.json?api_key=CA02H1KE8EH98A2K");
            }
        }, 0, period);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sm = (TextView)findViewById(R.id.soil);
        tem = (TextView)findViewById(R.id.temp);
        humd = (TextView)findViewById(R.id.hum);
        auto();

    }
    class JSONTask extends AsyncTask<String,String,String>{
        @Override
        protected  String doInBackground(String... params ){
            StringBuffer buffer=new StringBuffer();
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            URL url= null;
            try {
                url = new URL(params[0]);

                connection=(HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream stream=connection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(stream));
                String line="";
                buffer=new StringBuffer();
                while ((line=reader.readLine())!=null){
                    buffer.append(line);
                }
                String finalJson=buffer.toString();
                JSONObject parentobject=new JSONObject(finalJson);
                JSONArray parentarray=parentobject.getJSONArray("feeds");
                int i=parentarray.length()-1;

                JSONObject finalobject=  parentarray.getJSONObject(i);
                String val1=finalobject.getString("field1");
                String val2=finalobject.getString("field2");
                String val3=finalobject.getString("field3");
                String val = val1+","+val2+","+val3;
                return val;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
//            String[] x=result.split("[,]");
            String [] twoStringArray= result.split(",", 3); //the main lin
            sm.setText("Soil Moisture: "+twoStringArray[0]);
            tem.setText("Temperature: "+twoStringArray[1]);
            humd.setText("Humidity: "+twoStringArray[2]);
            String checkvar = twoStringArray[0];

            if(twoStringArray[0] != checkvar)
            {
                testvar=false;
            }
            else
            {
                testvar=true;
            }
                if (Integer.parseInt(twoStringArray[0]) <= 20) {
                    //Toast.makeText(getApplicationContext(),"Hello World",Toast.LENGTH_SHORT).show()
                        addNotification();

                }
           }



        }




    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.plant) //set icon for notification
                        .setContentTitle("Alert..!!!") //set title of notification
                        .setContentText("Low Soil Moisture! Plant needs Water")//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_HIGH)//set priority of notification
                        .setDefaults(Notification.DEFAULT_SOUND)//set priority of notification
        .setOnlyAlertOnce(testvar);


        Intent notificationIntent = new Intent(this, NotificationView.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        //  notificationIntent.putExtra("message", "This is a notification message");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
