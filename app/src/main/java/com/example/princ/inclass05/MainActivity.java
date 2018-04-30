package com.example.princ.inclass05;

/*
  Author : Sujanth Babu Guntupalli
*/

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GetImageTask.IBitmap{
    String[] keywords,urls;
    ArrayList<String> urlList=new ArrayList<>();
    Button go;
    ImageButton pButton,nButton;
    TextView viewKeyword;
    ImageView iv;
    AlertDialog.Builder builder;
    ProgressDialog pDialog;
    Bitmap bitmap;
    int imageId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isConnected()) {
            new GetKeywordsTask().execute("http://dev.theappsdr.com/apis/photos/keywords.php");
        }else{
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        }
        go = (Button) findViewById(R.id.goButton);
        viewKeyword = (TextView) findViewById(R.id.tvKeyword);
        iv=(ImageView) findViewById(R.id.ivImage);
        pButton=(ImageButton) findViewById(R.id.pButton);
        nButton=(ImageButton) findViewById(R.id.nButton);
        pButton.setEnabled(false);
        nButton.setEnabled(false);

        builder = new AlertDialog.Builder(this);
        pDialog=new ProgressDialog(this);
        pDialog.setMessage("Loading Dictionary");
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    Toast.makeText(MainActivity.this, "Is Connected", Toast.LENGTH_SHORT).show();
                    builder.setTitle("Choose a Keyword")
                            .setItems(keywords, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    if(isConnected()) {
                                        viewKeyword.setText(keywords[item]);
                                        urlList.clear();
                                        imageId=0;
                                        new GetURLsTask().execute("http://dev.theappsdr.com/apis/photos/index.php?keyword=" + keywords[item]);
                                    }else{
                                        Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).create().show();
                } else {
                    Toast.makeText(MainActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (urlList != null && !urlList.isEmpty()) {
                        if (imageId > 0) {
                            imageId -= 1;
                            if(isConnected()) {
                                new GetImageTask(MainActivity.this,MainActivity.this).execute(urlList.get(imageId));
                            }else{
                                Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                            }
                        } else if (imageId == 0) {
                            imageId = urlList.size() - 1;
                            if(isConnected()) {
                                new GetImageTask(MainActivity.this,MainActivity.this).execute(urlList.get(imageId));
                            }else{
                                Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No Images to dispaly", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){

                }
            }
        });

       nButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (urlList != null && !urlList.isEmpty()) {
                        if (imageId < urlList.size() - 1) {
                            imageId += 1;
                            if(isConnected()) {
                                new GetImageTask(MainActivity.this,MainActivity.this).execute(urlList.get(imageId));
                            }else{
                                Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                            }
                        } else if (imageId == urlList.size() - 1) {
                            imageId = 0;
                            if(isConnected()) {
                                new GetImageTask(MainActivity.this,MainActivity.this).execute(urlList.get(imageId));
                            }else{
                                Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No Images to Display", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){

                }
            }
        });

    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    @Override
    public void handleBitmap(Bitmap bitmap) {
            this.bitmap=bitmap;
            iv.setImageBitmap(bitmap);
            pButton.setClickable(true);
            nButton.setClickable(true);
    }

    private class GetKeywordsTask extends AsyncTask<String, Void, String> {

        String result;
        HttpURLConnection con;
        BufferedReader reader;

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(strings[0]);
                con = (HttpURLConnection) url.openConnection();
                con.connect();
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                }
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            if (s != null || !s.isEmpty()) {
                keywords = s.split(";");

            }
            for(String kw:keywords){
                Log.d("demo", "onPostExecute: "+kw);
            }
        }
    }

    private class GetURLsTask extends AsyncTask<String, Void, Void> {

        String result;
        HttpURLConnection con;
        BufferedReader reader;

        @Override
        protected void onPreExecute() {
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            for(int i=0;i<100;i++){
                for(int j=0;j<100000;j++){

                }
            }
            try {
                URL url = new URL(strings[0]);
                con = (HttpURLConnection) url.openConnection();
                con.connect();
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                }
                String line = "";
                while ((line = reader.readLine()) != null) {
                    //sb.append(line);
                    urlList.add(line);
                }
                //result = sb.toString();
                Log.d("demo", "doInBackground: "+urlList.size());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (urlList!= null && !urlList.isEmpty()) {
                /*urls = urlList.toArray(new String[urlList.size()]);
                for(String s:urls){
                    Log.d("demo", "onPostExecute2: "+s);
                }*/
                if(urlList.size()>1){
                    pDialog.dismiss();
                    pButton.setEnabled(true);
                    nButton.setEnabled(true);
                    if(isConnected()) {
                        new GetImageTask(MainActivity.this,MainActivity.this).execute(urlList.get(imageId));
                    }else{
                        Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    pDialog.dismiss();
                    if(isConnected()) {
                        new GetImageTask(MainActivity.this,MainActivity.this).execute(urlList.get(imageId));
                    }else{
                        Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    }
                    pButton.setEnabled(false);
                    nButton.setEnabled(false);
                }
            }else{
                pDialog.dismiss();
                pButton.setEnabled(false);
                nButton.setEnabled(false);
                iv.setImageBitmap(null);
                Toast.makeText(MainActivity.this, "No Images Found", Toast.LENGTH_SHORT).show();
            }


        }
    }
}
