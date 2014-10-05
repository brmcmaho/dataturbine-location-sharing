package brmcmaho.dtlocationsharing;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class FragMain extends Fragment{


    // Handles to UI widgets
    private TextView mLatLng;
    private TextView mAddress;
    //private ProgressBar mActivityIndicator;
    private TextView mConnectionState;
    private TextView mConnectionStatus;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_main, container, false);

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

        mLatLng.setText(LocationUtils.getLatLng(getActivity(), location));
    }


    public void addressMessage(String address) {
        mAddress.setText(address);
    }





}
