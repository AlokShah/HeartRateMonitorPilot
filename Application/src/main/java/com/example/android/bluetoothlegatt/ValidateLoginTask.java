package com.example.android.bluetoothlegatt;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonStreamerEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


/**
 * Created by alokshah on 2/12/18.
 */

public class ValidateLoginTask extends AsyncTask<String, Void, String>{

    public Interface delegate = null;
    StringBuffer result = new StringBuffer();
    private String response = "";

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {

        try
        {
            return postData(strings[0],strings[1],strings[2]);

        }
        catch(Exception ex)
        {
            return "Error";
        }

    }

    @Override
    protected void onPostExecute(String result) {
        //super.onPostExecute(result);
        delegate.processFinish(response);
    }

    private String postData(String urlPath,String Name, String ID) throws IOException, JSONException {
        JSONObject dataToSend = new JSONObject();
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {

            dataToSend.put("LName", Name);
            dataToSend.put("ID", ID);


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlPath);
            httppost.addHeader( "Content-Type", "application/json" );
            httppost.setEntity(new StringEntity(dataToSend.toString()));
            HttpResponse res = httpclient.execute(httppost);
            response = EntityUtils.toString(res.getEntity());

           // String responseStr = EntityUtils.toString(response.);


        }
        catch(Exception ex)
        {
            Log.i("ERROR", ex.toString());
        }
        finally
        {
            if(bufferedReader != null)
            {
                bufferedReader.close();
            }

            if(bufferedWriter != null)
            {
                bufferedWriter.close();
            }
        }
        return result.toString();
    }
}


