package vn.pugs.gocoder;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapLocation extends FragmentActivity implements OnMapReadyCallback, Network.NetworkTask {

    private static final String apiKey = "AIzaSyB1DPTsxxN-A-xuFyZ-68XZVkecC3UakbE";
    private static final String urlDirectionsString = "https://maps.googleapis.com/maps/api/directions/json?";
    private static URL destinationURL;

    private GoogleMap mMap;
    private double originLatitude;
    private double originLongitude;
    private String origin_place_id;

    private double destLatitude;
    private double destLongitude;
    private String dest_place_id;

    private double userLatitude;
    private double userLongitude;
    private boolean userLocationButtonStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        originLatitude = getIntent().getDoubleExtra("latitude" + 0, 37.0);
        originLongitude = getIntent().getDoubleExtra("longitude" + 0, 122.0);
        origin_place_id = getIntent().getStringExtra("place_id" + 0);
        destLatitude = getIntent().getDoubleExtra("latitude" + 1, -9000);
        destLongitude = getIntent().getDoubleExtra("longitude" + 1, -9000);
        dest_place_id = getIntent().getStringExtra("place_id" + 1);
        userLatitude = getIntent().getDoubleExtra("userLatitude", 37.0);
        userLongitude = getIntent().getDoubleExtra("userLongitude", 122.0);
        userLocationButtonStatus = getIntent().getBooleanExtra("userLocationButtonStatus",false);
        if (destLatitude != -9000) {
            buildURL();
            new Network(MapLocation.this).execute(destinationURL);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng myLocation = new LatLng(originLatitude, originLongitude);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("Marker in my location"));
        if (destLatitude != -9000) {
            LatLng destinationLocation = new LatLng(destLatitude, destLongitude);
            mMap.addMarker(new MarkerOptions().position(destinationLocation).title("Marker in my designated location"));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        LatLng userLocation;
        if (userLocationButtonStatus) {
            userLocation = new LatLng(userLatitude, userLongitude);
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Marker in my current location"));
        }
    }

    @Override
    public void onResponseReceived(List<String> streamData) {
        try {
            for (String data : streamData) {
                JSONObject json = new JSONObject(data);
                JSONArray routes = json.getJSONArray("routes");
                for(int i = 0; i < routes.length(); i++) {
                    JSONObject route = routes.getJSONObject(i);
                    JSONObject overview_polyline = route.getJSONObject("overview_polyline");
                    List<LatLng> points = PolyUtil.decode(overview_polyline.getString("points"));
                    mMap.addPolyline(new PolylineOptions().addAll(points));
                }
            }
        }
        catch(org.json.JSONException e){
            e.printStackTrace();
        }
    }

    public void buildURL() {
        String finalURL = urlDirectionsString;
        finalURL += "origin=" + originLatitude + "," + originLongitude + "&destination=place_id:" + dest_place_id + "&key=" + apiKey;
        try {
            destinationURL = new URL(finalURL);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
