package vn.pugs.gocoder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Network.NetworkTask {
    LocationManager locationManager;
    LocationListener locationListener;

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
    private ToggleButton userLocationButton;

    private double latitude;
    private double longitude;
    private String place_id;

    private double userLatitude;
    private double userLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLocationButton = (ToggleButton) findViewById(R.id.user_location_btn);

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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.INTERNET
            }, 10);
            return;
        } else {
            configureButton();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configureButton();
                } return;
        }
    }

    private void configureButton() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
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

            locationIntent.putExtra("userLatitude", userLatitude);
            locationIntent.putExtra("userLongitude", userLongitude);

            if (userLocationButton.isChecked()) {
                locationIntent.putExtra("userLocationButtonStatus", true);
            } else {
                locationIntent.putExtra("userLocationButtonStatus", false);
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
