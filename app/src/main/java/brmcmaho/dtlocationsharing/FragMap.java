package brmcmaho.dtlocationsharing;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import de.greenrobot.event.EventBus;


public class FragMap extends Fragment {


    public MapView mMapView;
    private GoogleMap mMap;

    private HashMap<String, Location> userLocations;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_map, container, false);
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        setUpMapIfNeeded();

        //TODO persistence
        userLocations = new HashMap<String, Location>();


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




    //event handling
    public void onEventMainThread(RemoteLocationEvent event){

        HashMap<String, Location> remoteLocationMap = event.getLocationMap();

        boolean locationsChanged = false; //flag for change to user locations

        //for each user in the location map received from DT
        for (String key : remoteLocationMap.keySet()){

            //if the location is for a user we are already tracking
            if(userLocations.containsKey(key)){

                Location remoteLoc = remoteLocationMap.get(key); //new location
                Location userLoc = userLocations.get(key);  //current stored location

                //if the location has actually changed
                if(remoteLoc.getLatitude() != userLoc.getLatitude() ||
                        remoteLoc.getLongitude() != userLoc.getLongitude()){

                    userLocations.put(key, remoteLoc); //update the location
                    locationsChanged = true; //flag the change
                }



             //else add the new user
            }else{
                userLocations.put(key, remoteLocationMap.get(key));
                locationsChanged = true; //flag the change
            }

        }

        //if we have had an update to user locations
        if(locationsChanged){

            mMap.clear(); //clear the map to prevent stacking location points

            //for each user we are tracking
            for(String userName : userLocations.keySet()){

                Location loc = userLocations.get(userName);
                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());

               Log.v("FragMap", "  Updating user location: "+userName+ " to: "+loc.getLatitude()+", "+loc.getLongitude());

                //add a marker
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(userName));

            }

        }else{
            Log.v("FragMap", "Received location update from server, but no positions changed");
        }


    }











}
    



