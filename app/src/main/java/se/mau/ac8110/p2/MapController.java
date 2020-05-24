package se.mau.ac8110.p2;

import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import static android.content.ContentValues.TAG;

public class MapController implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private MemberInfo[] markers;

    MapController(SupportMapFragment mapFragment) {
        mapFragment.getMapAsync(this);
    }

    void setMarkers(MemberInfo[] markers){
        this.markers = markers;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        showMarkers();
    }

    void updateMap(){
        Log.d(TAG, "updating map");
        if(googleMap !=null){
            googleMap.clear();
            showMarkers();
            Log.d(TAG, "showMarkers() running");
        }
    }

    private void showMarkers(){
        if(markers !=null){
            for (MemberInfo marker : markers) {
                LatLng latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(marker.getName() + " on position: longitude: " + latLng.longitude + ", latitude: " + latLng.latitude);
                googleMap.addMarker(markerOptions);
            }
        }
    }
}
