package brmcmaho.dtlocationsharing;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class FragControls extends Fragment {


    // Handles to UI widgets
    @InjectView(R.id.lat_lng)
    TextView mLatLng;
    @InjectView(R.id.address)
    TextView mAddress;
    @InjectView(R.id.text_connection_status)
    TextView mConnectionStatus;

    /*Button click listeners defined in ActMain*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_controls, container, false);

        EventBus.getDefault().register(this);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
        ButterKnife.reset(this); //required for fragments
    }


    /*OnClick methods for buttons*/
    @OnClick(R.id.update_map_button)
    public void getLocation() {
        ((ActMain) getActivity()).updateMap();
    }

    @OnClick(R.id.get_address_button)
    public void getAddress() {
        ((ActMain) getActivity()).getAddress();
    }

    @OnClick(R.id.start_updates_button)
    public void startUpdates() {
        ((ActMain) getActivity()).startUpdates();
    }

    @OnClick(R.id.stop_updates_button)
    public void stopUpdates() {
        ((ActMain) getActivity()).stopUpdates();
    }







    /*Event handling*/
    public void onEvent(LocationUpdateEvent event) {

        String locString = event.getLocation().getLatitude() + ", " + event.getLocation().getLongitude();
        mLatLng.setText(locString);
    }

    public void onEvent(AddressEvent event) {
        mAddress.setText(event.getAddress());
    }

    public void onEvent(GoogleApiClientStatusEvent event){
        mConnectionStatus.setText(event.getMessage());
    }








}
