package com.example.princ.inclass05;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetImageTask extends AsyncTask<String, Void, Bitmap> {

    IBitmap ibm;

    ProgressDialog pDlg;
    private Context context;

    public GetImageTask(IBitmap ibm,Context ctx) {
        this.ibm = ibm;
        this.context=ctx;
        pDlg = new ProgressDialog(context);
        pDlg.setMessage("Loading Photo");
        pDlg.setCancelable(false);
        pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }


    @Override
    protected void onPreExecute() {
        pDlg.show();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        HttpURLConnection con = null;
        Bitmap bitmap = null;
        try {
            //Log.d("demo", "got: "+strings[0]);
            URL url = new URL(strings[0]);
            con = (HttpURLConnection) url.openConnection();
            con.connect();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                bitmap = BitmapFactory.decodeStream(con.getInputStream());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap btmp) {
        if (btmp != null) {
            ibm.handleBitmap(btmp);
            pDlg.dismiss();
        } else {
            Log.d("demo", "onPostExecute: bitmap null");
        }
    }

    public static interface IBitmap {
        public void handleBitmap(Bitmap bitmap);
    }
}
