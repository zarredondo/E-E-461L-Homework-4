package vn.pugs.gocoder;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String zip_code;

    private double userLatitude;
    private double userLongitude;
    private boolean userLocationButtonStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        zip_code = getIntent().getStringExtra("zip_code");
        latitude = getIntent().getDoubleExtra("latitude", 37.0);
        longitude = getIntent().getDoubleExtra("longitude", 122.0);
        userLatitude = getIntent().getDoubleExtra("userLatitude", 37.0);
        userLongitude = getIntent().getDoubleExtra("userLongitude", 122.0);
        userLocationButtonStatus = getIntent().getBooleanExtra("userLocationButtonStatus",false);
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
        LatLng userLocation;
        if (userLocationButtonStatus) {
            userLocation = new LatLng(userLatitude, userLongitude);
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Marker in my current location"));
        }

        LatLng inputLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(inputLocation).title("Marker in my designated location"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(inputLocation));
    }
}
