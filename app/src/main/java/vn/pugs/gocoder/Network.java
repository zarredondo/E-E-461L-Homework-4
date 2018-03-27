package vn.pugs.gocoder;

import android.os.AsyncTask;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zarredondo on 3/27/2018.
 */

/* extends AsyncTask<String, Void */

public class Network {


    public static final String urlString = "http://maps.googleapis.com/maps/api/geocode/json?";

    private String streamData;

    public String connectNetwork(String address) {
        try {
            URL geolocationURL = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyB1DPTsxxN-A-xuFyZ-68XZVkecC3UakbE");
            new NetworkTask().execute(geolocationURL);
        } catch (NullPointerException e) {
            System.out.println(e);
        } catch (MalformedURLException e) {
            System.out.println(e);
        }
        return streamData;
    }

    public class NetworkTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL geolocationURL = urls[0];
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
            }
            catch (IOException e) {
                System.out.println(e);
            }
            return outputStream;
        }

        protected void onPostExecute(String stream) {
            streamData = stream;
        }

    }
}