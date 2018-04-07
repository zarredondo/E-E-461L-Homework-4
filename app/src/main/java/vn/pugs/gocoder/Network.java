package vn.pugs.gocoder;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zarredondo on 3/27/2018.
 */



public class Network extends AsyncTask<URL, Void, List<String>> {
    public NetworkTask networkTask = null;

    public interface NetworkTask {
        void onResponseReceived(List<String> data);
    }

    public Network(NetworkTask networkTask) {
        this.networkTask = networkTask;
    }

    protected List<String> doInBackground(URL... urls) {
        List<String> result = new ArrayList<>();
        for(URL url : urls) {
            URL geolocationURL = url;
            String outputStream = new String();
            try {
                HttpURLConnection connection = (HttpURLConnection) geolocationURL.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                outputStream = sb.toString();
            } catch (IOException e) {
                System.out.println(e);
            }
            result.add(outputStream);
        }
        return result;
    }

    protected void onPostExecute(List<String> data) {
        networkTask.onResponseReceived(data);
    }
}