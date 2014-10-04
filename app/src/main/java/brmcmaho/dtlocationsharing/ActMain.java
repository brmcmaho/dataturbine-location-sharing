package brmcmaho.dtlocationsharing;

import android.location.Location;
import android.os.Bundle;
import android.view.View;

import org.actimo.activity.core.FeatureActivity;


public class ActMain extends FeatureActivity  {

    private FragMain mViewFrag;
    private FLocation mFLocation;

    @Override
    protected void initializeFeatures() {

        mViewFrag = new FragMain();







        mFLocation = new FLocation(new FLocationListener() {


            @Override
            public void stateMessage(int msg) {
                mViewFrag.stateMessage(msg);
            }

            @Override
            public void statusMessage(int msg) {
                mViewFrag.statusMessage(msg);

            }

            @Override
            public void locationUpdate(Location location) {


                mViewFrag.locationUpdate(location);
            }

            @Override
            public void addressMessage(String address) {
                mViewFrag.addressMessage(address);
            }


        });

        addFeature(mFLocation);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.main_container, mViewFrag)
                    .commit();
        }
    }


    public void getLocation(View v) {
        mFLocation.getLocation();
    }


    public void getAddress(View v) {
        mFLocation.getAddress();
    }

    public void startUpdates(View v) {
        mFLocation.startUpdates();
    }

    public void stopUpdates(View v) {
        mFLocation.stopUpdates();
    }


}
