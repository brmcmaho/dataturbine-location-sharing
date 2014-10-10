package brmcmaho.dtlocationsharing;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.rbnb.sapi.SAPIException;

import org.actimo.activity.core.FeatureActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.ucsd.rbnb.simple.MIME;
import edu.ucsd.rbnb.simple.SimpleSource;


public class ActMain extends FeatureActivity  {

    private FragMain mViewFrag;
    private FLocationServices mLocationServices;

    private SimpleSource src;
    private Context mContext;

    @Override
    protected void initializeFeatures() {

        mViewFrag = new FragMain();
        mContext = this;


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
                    .add(R.id.main_container, mViewFrag)
                    .commit();
        }

        new ConnectToServerTask(this).execute();
    }


    @OnClick(R.id.get_location_button)
    public void getLocation() {
        mLocationServices.getLastLocation();
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






    protected class ConnectToServerTask extends AsyncTask<Void, Void, String> {

        Context localContext;
        public ConnectToServerTask(Context context) {
            super();
            localContext = context;
        }


        @Override
        protected String doInBackground(Void... params) {
            src = new SimpleSource("LocationTester", "76.176.187.138", 3333);
            src.setConnectionHandling(false);

            try {
                src.setArchiveSize(400);
                src.setCacheSize(10);
                src.addChannel("gps", MIME.GPS);
                src.connect();
                Log.e("test", "connected?");
            } catch (SAPIException e) {
                Log.e("SAPIException", "on connect", e);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String address) {

        }
    }





    protected class SendLocationTask extends AsyncTask<Location, Void, String> {

        Context localContext;

        public SendLocationTask(Context context) {
            super();
            localContext = context;
        }

        @Override
        protected String doInBackground(Location... params) {
            double time = System.currentTimeMillis()/1000;
            try {
                src.put("gps", new double[]{params[0].getLatitude(), params[0].getLongitude()}, time);
                src.flush();
                Log.i("DT", "Putting data: "+params[0].getLatitude()+", " +params[0].getLongitude());
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
