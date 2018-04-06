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

public class MainActivity extends AppCompatActivity implements Network.NetworkTask {
    LocationManager locationManager;
    LocationListener locationListener;

    private static final String apiKey = "AIzaSyB1DPTsxxN-A-xuFyZ-68XZVkecC3UakbE";
    private static final String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private URL geolocationURL;

    private EditText streetAddressText;
    private EditText cityAddressText;
    private EditText stateAddressText;
    private Button addressInputButton;
    private ToggleButton userLocationButton;

    private double latitude;
    private double longitude;
    private String zip_code;

    private double userLatitude;
    private double userLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        streetAddressText = (EditText) findViewById(R.id.street_address_text);
        cityAddressText = (EditText) findViewById(R.id.city_address_text);
        stateAddressText = (EditText) findViewById(R.id.state_address_text);
        addressInputButton = (Button) findViewById(R.id.address_input_btn);
        userLocationButton = (ToggleButton) findViewById(R.id.user_location_btn);

        addressInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildURL();
                new Network(MainActivity.this).execute(geolocationURL);
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
            locationIntent.putExtra("userLatitude", userLatitude);
            locationIntent.putExtra("userLongitude", userLongitude);

            if (userLocationButton.isChecked()) {
                locationIntent.putExtra("userLocationButtonStatus", true);
            } else {
                locationIntent.putExtra("userLocationButtonStatus", false);
            }

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
