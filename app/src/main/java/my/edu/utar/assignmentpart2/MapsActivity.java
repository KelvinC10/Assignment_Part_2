package my.edu.utar.assignmentpart2;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String placeName = getIntent().getStringExtra("placeName");
        String city = getIntent().getStringExtra("city");
        double lat = getIntent().getDoubleExtra("lat", 4.5975);
        double lng = getIntent().getDoubleExtra("lng", 101.0901);

        LatLng place = new LatLng(lat, lng);

        mMap.addMarker(new MarkerOptions()
                .position(place)
                .title(placeName)
                .snippet(city));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15f));
    }
}