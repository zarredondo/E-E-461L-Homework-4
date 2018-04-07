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
import java.util.List;

public class MainActivity extends AppCompatActivity implements Network.NetworkTask{

    private static final String apiKey = "AIzaSyB1DPTsxxN-A-xuFyZ-68XZVkecC3UakbE";
    private static final String urlGeocodeString = "https://maps.googleapis.com/maps/api/geocode/json?address=";

    private URL geolocationOriginURL;
    private URL geolocationDestinationURL;

    private EditText originStreetAddressText;
    private EditText originCityAddressText;
    private EditText originStateAddressText;

    private EditText destinationStreetAddressText;
    private EditText destinationCityAddressText;
    private EditText destinationStateAddressText;

    private Button addressInputButton;

    private double latitude;
    private double longitude;
    private String place_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        originStreetAddressText = (EditText) findViewById(R.id.origin_street_address_text);
        originCityAddressText = (EditText) findViewById(R.id.origin_city_address_text);
        originStateAddressText = (EditText) findViewById(R.id.origin_state_address_text);

        destinationStreetAddressText = (EditText) findViewById(R.id.destination_street_address_text);
        destinationCityAddressText = (EditText) findViewById(R.id.destination_city_address_text);
        destinationStateAddressText = (EditText) findViewById(R.id.destination_state_address_text);

        addressInputButton = (Button) findViewById(R.id.address_input_btn);
        addressInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildOriginURL();
                if (!destinationStreetAddressText.getText().toString().isEmpty()) {
                    buildDirectionsURL();
                    new Network(MainActivity.this).execute(geolocationOriginURL, geolocationDestinationURL);
                }
                else {
                    new Network(MainActivity.this).execute(geolocationOriginURL);
                }
            }
        });
    }

    @Override
    public void onResponseReceived(List<String> streamData) {
        try {
            int counter = 0;
            Intent locationIntent = new Intent(getApplicationContext(), MapLocation.class);
            for (String data : streamData) {
                JSONObject json = new JSONObject(data);
                JSONArray results = json.getJSONArray("results");
                JSONObject resultsArray = results.getJSONObject(0);
                place_id = resultsArray.getString("place_id");
                JSONObject geometry = resultsArray.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                latitude = location.getDouble("lat");
                longitude = location.getDouble("lng");
                locationIntent.putExtra("place_id" + counter, place_id);
                locationIntent.putExtra("latitude" + counter, latitude);
                locationIntent.putExtra("longitude" + counter, longitude);
                counter = counter + 1;
            }
            startActivity(locationIntent);
        }
        catch(org.json.JSONException e){
            e.printStackTrace();
        }
    }

    public void buildOriginURL() {
        String finalURL = urlGeocodeString;

        String streetAddressString = originStreetAddressText.getText().toString();
        streetAddressString = streetAddressString.replace(" ", "+");

        String cityAddressString = originCityAddressText.getText().toString();
        cityAddressString = cityAddressString.replace(" ", "+");

        String stateAddressString = originStateAddressText.getText().toString();

        finalURL += streetAddressString + ",+" + cityAddressString + ",+" + stateAddressString + "&key=" + apiKey;
        try {
            geolocationOriginURL = new URL(finalURL);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void buildDirectionsURL() {
        String finalURL = urlGeocodeString;

        String streetAddressString = destinationStreetAddressText.getText().toString();
        streetAddressString = streetAddressString.replace(" ", "+");

        String cityAddressString = destinationCityAddressText.getText().toString();
        cityAddressString = cityAddressString.replace(" ", "+");

        String stateAddressString = destinationStateAddressText.getText().toString();

        finalURL += streetAddressString + ",+" + cityAddressString + ",+" + stateAddressString + "&key=" + apiKey;
        try {
            geolocationDestinationURL = new URL(finalURL);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
