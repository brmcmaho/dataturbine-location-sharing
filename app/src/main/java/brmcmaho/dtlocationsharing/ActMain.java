package brmcmaho.dtlocationsharing;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.rbnb.sapi.SAPIException;

import org.actimo.activity.core.FeatureActivity;

import butterknife.OnClick;
import edu.ucsd.rbnb.simple.MIME;
import edu.ucsd.rbnb.simple.SimpleSink;
import edu.ucsd.rbnb.simple.SimpleSource;


public class ActMain extends FeatureActivity  {

    private FLocationServices mLocationServices;

    private DTSource src;
    private DTSink sink;

    @Override
    protected void initializeFeatures() {

        mLocationServices = new FLocationServices() ;

        addFeature(mLocationServices);
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_main);
       // ButterKnife.inject(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.upper_container, new FragControls())
                    .add(R.id.lower_container, new FragMap())
                    .commit();
        }

        src = new DTSource(this);
        src.connect();

        sink = new DTSink(this);
        sink.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        src.disconnect();
        sink.disconnect();
    }

    @OnClick(R.id.get_location_button)
    public void getLocation() {
        mLocationServices.getLastLocation();
        sink.fetchData();
    }

    @OnClick(R.id.get_address_button)
    public void getAddress() {
        mLocationServices.getAddress();
    }

    @OnClick(R.id.start_updates_button)
    public void startUpdates() {
        mLocationServices.requestLocationUpdates();
    }

    @OnClick(R.id.stop_updates_button)
    public void stopUpdates() {
        mLocationServices.removeLocationUpdates();
    }













}
