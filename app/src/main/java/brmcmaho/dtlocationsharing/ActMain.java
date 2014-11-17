package brmcmaho.dtlocationsharing;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;

import org.actimo.activity.core.FeatureActivity;

import butterknife.OnClick;


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

        src = new DTSource(this, getGmailAccount());
        src.connect();

        sink = new DTSink(this);
        //sink.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        src.disconnect();
        sink.disconnect();
    }

    @OnClick(R.id.update_map_button)
    public void updateMap() {
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





    //get gmail account to use as user name
    private String getGmailAccount(){
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        String gmail = null;

        for(Account account: list)
        {
            if(account.type.equalsIgnoreCase("com.google"))
            {
                gmail = account.name;
                break;
            }
        }
        return gmail;
    }






}
