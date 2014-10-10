package brmcmaho.dtlocationsharing;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.greenrobot.event.EventBus;


public class FragMap extends Fragment {


    public MapView mMapView;
    private GoogleMap mMap;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_map, container, false);
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        setUpMapIfNeeded();


        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        setUpMapIfNeeded();

        EventBus.getDefault().register(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = mMapView.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);


            } else {
                Log.e("FragMap", "Failed to set up map fragment");
            }
        }
    }





    public void onEvent(LocationUpdateEvent event){

        //get LatLng representation of location
        LatLng latLng = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());

        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("My location"));
    }









//
//    private void updateMapMarkers() {
//
//        mMap.clear();
//
//
//        // Instantiates a new CircleOptions object and defines the center and radius
//        mMap.addCircle(new CircleOptions()
//                .center(mEvent.getLatLng())
//                .radius(mEvent.getAccuracy())
//                .strokeColor(Color.RED)
//                .fillColor(Color.RED & 0x11FFFFFF)
//                .strokeWidth(1.0f)); // in pixels
//
//        mMap.addMarker(new MarkerOptions()
//                .position(mEvent.getLatLng())
//                .title(mEvent.getMessage()));
//
//
//    }


}
    



