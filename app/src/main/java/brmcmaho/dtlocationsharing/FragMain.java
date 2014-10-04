package brmcmaho.dtlocationsharing;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rbnb.sapi.SAPIException;

import edu.ucsd.rbnb.simple.MIME;
import edu.ucsd.rbnb.simple.SimpleSource;


/**
 * Created by Brian on 2014-09-29.
 */
public class FragMain extends Fragment{


    // Handles to UI widgets
    private TextView mLatLng;
    private TextView mAddress;
    //private ProgressBar mActivityIndicator;
    private TextView mConnectionState;
    private TextView mConnectionStatus;


    private SimpleSource src;
    private Location mLocation; //TODO hack


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_main, container, false);

        new ConnectToServerTask(getActivity()).execute();



        mLatLng = (TextView) view.findViewById(R.id.lat_lng);
        mAddress = (TextView) view.findViewById(R.id.address);
        //mActivityIndicator = (ProgressBar) view.findViewById(R.id.address_progress);
        mConnectionState = (TextView) view.findViewById(R.id.text_connection_state);
        mConnectionStatus = (TextView) view.findViewById(R.id.text_connection_status);


        return view;
    }


    public void stateMessage(int msg) {
        mConnectionState.setText(msg);
    }


    public void statusMessage(int msg) {

        mConnectionStatus.setText(msg);
    }


    public void locationUpdate(Location location) {

       mLocation = location;

        new SendDataTask(getActivity()).execute();

        mLatLng.setText(LocationUtils.getLatLng(getActivity(), location));
    }


    public void addressMessage(String address) {
        mAddress.setText(address);
    }


    protected class ConnectToServerTask extends AsyncTask<Void, Void, String> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;

        // Constructor called by the system to instantiate the task
        public ConnectToServerTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }

        /**
         * Get a geocoding service instance, pass latitude and longitude to it, format the returned
         * address, and return the address to the UI thread.
         */
        @Override
        protected String doInBackground(Void... params) {
            src = new SimpleSource("LocationTester", "76.176.187.138", 3333);
            src.setConnectionHandling(false);

            try {
                src.setArchiveSize(400);
                src.setCacheSize(10);
                src.addChannel("gps", MIME.GPS);
                src.connect();
                Log.e("test","connected?");
            } catch (SAPIException e) {
                Log.e("SAPIException", "on connect", e);
            }
            return "";
        }


        @Override
        protected void onPostExecute(String address) {

        }
    }


    protected class SendDataTask extends AsyncTask<Void, Void, String> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;


        // Constructor called by the system to instantiate the task
        public SendDataTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }

        /**
         * Get a geocoding service instance, pass latitude and longitude to it, format the returned
         * address, and return the address to the UI thread.
         */
        @Override
        protected String doInBackground(Void... params) {
            double time = System.currentTimeMillis()/1000;
            try {
                src.put("gps", new double[]{mLocation.getLatitude(), mLocation.getLongitude()}, time);
                src.flush();
                Log.i("DT", "Putting data: "+mLocation.getLatitude()+", " +mLocation.getLongitude());
            } catch (SAPIException e) {
                Log.e("SAPIException", "on data", e);
            }
            return "";
        }


        @Override
        protected void onPostExecute(String address) {

        }
    }


}
