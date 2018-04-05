package vn.pugs.gocoder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements Network.NetworkTask{

    public static final String urlString = "http://maps.googleapis.com/maps/api/geocode/json?";
    URL geolocationURL;

    EditText addressInput;
    Button addressInputButton;

    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addressInput = (EditText) findViewById(R.id.address_input);
        addressInputButton = (Button) findViewById(R.id.address_input_btn);
        addressInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    geolocationURL = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyB1DPTsxxN-A-xuFyZ-68XZVkecC3UakbE");
                } catch (NullPointerException e) {
                    System.out.println(e);
                } catch (MalformedURLException e) {
                    System.out.println(e);
                }
                new Network(MainActivity.this).execute(geolocationURL);
            }
        });
    }

    @Override
    public void onResponseReceived(String data) {
        try {
            JSONObject json = new JSONObject(data);
            JSONArray results = json.getJSONArray("results");
            JSONObject resultsArray = results.getJSONObject(0);
            JSONObject geometry = resultsArray.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            latitude = location.getDouble("lat");
            longitude = location.getDouble("lng");
            Intent locationIntent = new Intent(getApplicationContext(), MapLocation.class);
            locationIntent.putExtra("latitude", latitude);
            locationIntent.putExtra("longitude", longitude);
            startActivity(locationIntent);
        }
        catch (org.json.JSONException e) {
            e.printStackTrace();
        }
    }
}
