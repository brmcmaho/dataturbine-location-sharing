package brmcmaho.dtlocationsharing;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.SAPIException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import edu.ucsd.rbnb.simple.SimpleSubscribeSink;

/**
 * Created by Brian on 2014-10-10.
 */
public class DTSink {


    private static final String NAME = "LocationTesterSink";
    private static final String SERVER_IP = "76.176.187.138";
    private static final int PORT = 3333;

//
//    SimpleSubscribeSink sink = new SimpleSubscribeSink("Sample");
//		sink.monitorAll();
//
//		sink.subscribeToOldest();
//
//		//Fetch 50 slices  of data
//		for(int r=0; r <50; r++){
//
//			//fetch data
//			ChannelMap map = sink.fetch();
//
//			//Print it
//			for(int i=0; i < map.NumberOfChannels(); i++){
//
//				System.out.println(
//					map.GetName(i) + "\t"
//					+map.GetTimes(i)[0] + "\t"
//					+map.GetDataAsFloat32(i)[0] + "\t"
//				);
//			}
//		}
//		sink.close();


    private final Context mContext;
    private SimpleSubscribeSink sink;


    public DTSink(Context context) {
        mContext = context;
    }

//    public void connect() {
//        //run task to connect to DT server in the background
//        new ConnectToServerTask(mContext).execute();
//
//    }

    public void disconnect() {

        sink.close();
    }

    public void fetchData(){
        new FetchDataTask(mContext).execute();
    }




//    protected class ConnectToServerTask extends AsyncTask<Void, Void, String> {
//
//        Context localContext;
//
//        public ConnectToServerTask(Context context) {
//            super();
//            localContext = context;
//        }
//
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            try {
//                //sink auto connects on creation
//                sink = new SimpleSubscribeSink(NAME, SERVER_IP, PORT);
//                sink.monitorAll();
//                sink.subscribeToNewest();
//
//
//            } catch (SAPIException e) {
//                Log.e("SAPIException", "on connect", e);
//            }
//            return "";
//        }
//
//        @Override
//        protected void onPostExecute(String address) {
//
//        }
//    }

    protected class FetchDataTask extends AsyncTask<Void, Void, HashMap<String, Location>> {



        Context localContext;

        public FetchDataTask(Context context) {
            super();
            localContext = context;

        }


        @Override
        protected HashMap<String, Location> doInBackground(Void... params) {
            HashMap<String, Location> locationMap = new HashMap<String, Location>();

            try {
                //sink auto connects on creation
                sink = new SimpleSubscribeSink(NAME, SERVER_IP, PORT);
                sink.monitorAll();
                sink.subscribeToNewest();


            } catch (SAPIException e) {
                Log.e("SAPIException", "on connect", e);
            }



            try {

                ChannelMap map = sink.fetch();

                for (int i = 0; i < map.NumberOfChannels(); i++) {

                    String name =map.GetName(i);
                    if(name.startsWith("_")){
                        Log.v("DTSink", "Skipping metadata channel: " + map.GetName(i));
                        continue;
                    }

//
//                    for (int d = 0; d < ((map.GetDataAsFloat64(i).length)/2); d = d + 2){
//
//                        double lat = map.GetDataAsFloat64(i)[d];
//                        double lng =map.GetDataAsFloat64(i)[d+1];
//                        double time = map.GetTimes(i)[d];
//
//
//                        Log.i("DTSink", "Received name: "+map.GetName(i)+" lat: "+lat+" long: "+lng+"  time: "+time);
//                    }



                    double lat = map.GetDataAsFloat64(i)[0];
                    double lng =map.GetDataAsFloat64(i)[1];
                    double time = map.GetTimes(i)[0];

                    Log.i("DTSink", "Received location- name: "+map.GetName(i)+" lat: "+lat+" long: "+lng+"  time: "+time);

                    Location loc = new Location("DT");
                    loc.setLatitude(lat);
                    loc.setLongitude(lng);
                    loc.setTime((long)time);

                    locationMap.put(name, loc);

                }

            } catch (SAPIException e) {
                Log.e("SAPIException", "on connect", e);
            }
            return locationMap;
        }

        @Override
        protected void onPostExecute(HashMap<String, Location> locationMap) {
            postRemoteLocationEvent(locationMap);
        }
    }

    private void postRemoteLocationEvent(HashMap<String, Location> locArray) {
        EventBus.getDefault().post(new RemoteLocationEvent(locArray));
    }


}


class RemoteLocationEvent {
    Date when = Calendar.getInstance().getTime();
    private HashMap<String, Location> locationMap;

    public RemoteLocationEvent(HashMap<String, Location> locationMap) {this.locationMap = locationMap;}

    public HashMap<String, Location> getLocationMap() { return locationMap; }

    public Date getTimestamp() { return when; }


}