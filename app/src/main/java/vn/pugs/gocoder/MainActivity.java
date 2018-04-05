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

    private static final String apiKey = "AIzaSyB1DPTsxxN-A-xuFyZ-68XZVkecC3UakbE";
    private static final String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private URL geolocationURL;

    private EditText streetAddressText;
    private EditText cityAddressText;
    private EditText stateAddressText;
    private Button addressInputButton;

    private double latitude;
    private double longitude;
    private String zip_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        streetAddressText = (EditText) findViewById(R.id.street_address_text);
        cityAddressText = (EditText) findViewById(R.id.city_address_text);
        stateAddressText = (EditText) findViewById(R.id.state_address_text);
        addressInputButton = (Button) findViewById(R.id.address_input_btn);

        addressInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildURL();
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
            JSONArray addressComponents = resultsArray.getJSONArray("address_components");
            for (int i = 0; i < addressComponents.length(); i++) {
                if ((addressComponents.getJSONObject(i).getJSONArray("types").getString(0)).equals("postal_code")) {
                    JSONObject postal = addressComponents.getJSONObject(i);
                    zip_code = postal.getString("short_name");
                }
            }
            JSONObject geometry = resultsArray.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            latitude = location.getDouble("lat");
            longitude = location.getDouble("lng");
            Intent locationIntent = new Intent(getApplicationContext(), MapLocation.class);
            locationIntent.putExtra("zip_code", zip_code);
            locationIntent.putExtra("latitude", latitude);
            locationIntent.putExtra("longitude", longitude);
            startActivity(locationIntent);
        }
        catch (org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    public void buildURL() {
        String finalURL = urlString;

        String streetAddressString = streetAddressText.getText().toString();
        streetAddressString = streetAddressString.replace(" ", "+");

        String cityAddressString = cityAddressText.getText().toString();
        cityAddressString = cityAddressString.replace(" ", "+");

        String stateAddressString = stateAddressText.getText().toString();

        finalURL += streetAddressString + ",+" + cityAddressString + ",+" + stateAddressString + "&key=" + apiKey;
        try {
            geolocationURL = new URL(finalURL);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
