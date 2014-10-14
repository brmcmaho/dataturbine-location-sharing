package brmcmaho.dtlocationsharing;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.rbnb.sapi.SAPIException;

import de.greenrobot.event.EventBus;
import edu.ucsd.rbnb.simple.MIME;
import edu.ucsd.rbnb.simple.SimpleSource;

/**
 * Created by Brian on 2014-10-10.
 */
public class DTSource {

    private static final String NAME = "LocationTesterSource";
    private static final String SERVER_IP = "76.176.187.138";
    private static final int PORT = 3333;



    private final Context mContext;
    SimpleSource src;


    public DTSource(Context context){
        mContext = context;
    }

    public void connect(){
        //run task to connect to DT server in the background
        new ConnectToServerTask(mContext).execute();
        EventBus.getDefault().register(this);
    }

    public void disconnect(){
        EventBus.getDefault().unregister(this);
        src.close();
    }



    public void sendLocation(Location location){
        //run task to send location to server in the background
        new SendLocationTask(mContext).execute(location);
    }



    //event handling
    public void onEvent(LocationUpdateEvent event){
        sendLocation(event.getLocation());
    }






    protected class ConnectToServerTask extends AsyncTask<Void, Void, String> {



        Context localContext;
        public ConnectToServerTask(Context context) {
            super();
            localContext = context;
        }


        @Override
        protected String doInBackground(Void... params) {

            try {
                src = new SimpleSource(NAME, SERVER_IP, PORT);
                src.setConnectionHandling(false); //prevent source from auto connecting on flush
                src.setArchiveSize(400);
                src.setCacheSize(10);
                src.addChannel("gps", MIME.GPS);
                src.connect();
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
