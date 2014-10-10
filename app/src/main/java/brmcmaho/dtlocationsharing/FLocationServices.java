package brmcmaho.dtlocationsharing;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;


public class FLocationServices extends FGooglePlayServices implements LocationListener{

    private static final String TAG = "FLocationServices";


    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }


    public void getLastLocation(){

        //get the last known location (may not be fresh) and post it to the bus
        postLocationUpdateEvent(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
    }

    // no argument request for default LocationRequest
    public void requestLocationUpdates(){

        //create default location request and set default values
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        requestLocationUpdates(locationRequest);
    }


    public void requestLocationUpdates(LocationRequest locationRequest){
        //subscribe to location updates, with this class as the location listener
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    public void removeLocationUpdates(){
        //unsubscribe to location updates for this class' listener
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void getAddress(){
        new GetAddressTask(getActivity()).execute(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
    }

    

    //location listeneer
    @Override
    public void onLocationChanged(Location location) {
        postLocationUpdateEvent(location);
    }




    //post a location update event to the bus
    private void postLocationUpdateEvent(Location location){
        EventBus.getDefault().post(new LocationUpdateEvent(location));
    }

    //post a location update event to the bus
    private void postAddressEvent(String address){
        EventBus.getDefault().post(new AddressEvent(address));
    }




    protected class GetAddressTask extends AsyncTask<Location, Void, String> {

        Context mContext;

        // Constructor called by the system to instantiate the task
        public GetAddressTask(Context context) {
            super();

            // Set a Context for the background task
            mContext = context;
        }

        /**
         * Get a geocoding service instance, pass latitude and longitude to it, format the returned
         * address, and return the address to the UI thread.
         */
        @Override
        protected String doInBackground(Location... params) {

            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

            //get location from params
            Location location = params[0];

            List<Address> addresses = null;


            // Try to get an address for the current location.
            try {
                //get one
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1 );

            // Catch network or other I/O problems.
            } catch (IOException exception1) {

                Log.e(TAG, getActivity().getString(R.string.IO_Exception_getFromLocation));
                exception1.printStackTrace();
                return (getActivity().getString(R.string.IO_Exception_getFromLocation));

             // Catch incorrect latitude or longitude values
            } catch (IllegalArgumentException exception2) {

                // Construct a message containing the invalid arguments
                String errorString = getActivity().getString(
                        R.string.illegal_argument_exception,
                        location.getLatitude(),
                        location.getLongitude()
                );

                Log.e(TAG, errorString);
                exception2.printStackTrace();
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {

                // Get the first address
                Address address = addresses.get(0);

                //format the address
                String addressText = getActivity().getString(R.string.address_output_string,
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",  // If there's a street address, add it
                        address.getLocality(), // add locality, usually a city
                        address.getCountryName() //add the country name
                );

                return addressText;

            } else {
                return "No address found";
            }
        }

        /**
         * A method that's called once doInBackground() completes.  This method runs on the UI thread.
         */
        @Override
        protected void onPostExecute(String address) {

            postAddressEvent(address);
        }
    }





}


class AddressEvent {

    Date when = Calendar.getInstance().getTime();
    private String address;

    public AddressEvent(String address) {this.address = address;}

    public String getAddress() { return address; }
    public Date getTimestamp() { return when; }
}

 class LocationUpdateEvent {

    Date when = Calendar.getInstance().getTime();
    private Location location;

    public LocationUpdateEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() { return location; }
    public Date getTimestamp() { return when; }
}