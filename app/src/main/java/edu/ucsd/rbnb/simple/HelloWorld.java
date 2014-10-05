//package edu.ucsd.rbnb.simple;
//
//import com.rbnb.sapi.ChannelMap;
//import com.rbnb.sapi.SAPIException;
//
//import edu.ucsd.rbnb.simple.MIME;
//import edu.ucsd.rbnb.simple.SimpleRequestSink;
//import edu.ucsd.rbnb.simple.SimpleSource;
//import edu.ucsd.rbnb.simple.SimpleSourceListener;
//import edu.ucsd.rbnb.simple.SimpleSubscribeSink;
//
//public class HelloWorld {
//
//	public static void main(String[] args) throws SAPIException {
//		//example4();
//	}
//
//	public static void example1() throws SAPIException{
//		SimpleSource src = new SimpleSource("MySource");
//		src.addChannel("count");
//
//		src.put("count", 42);
//
//		src.flush();
//	}
//
//	public static void example2() throws SAPIException{
//		double time = System.currentTimeMillis()/1000;
//		SimpleSource src = new SimpleSource("MySource2");
//		src.addChannel("temp");
//		src.addChannel("gps", MIME.GPS);
//		src.put("temp", 32.1, time);
//		src.put("gps", new double[]{53.0,-12.0}, time);
//		src.flush();
//
//
//	}
//
//	public static void example3() throws SAPIException{
//		SimpleSource src = new SimpleSource("MySource3");
//		src.setConnectionHandling(false);
//		src.setArchiveSize(400);
//		src.setCacheSize(10);
//		src.addChannel("temp");
//
//		src.connect();
//
//		src.put("temp", 32.1);
//		src.flush();
//		src.put("temp", 30.7);
//		src.flush();
//
//		src.close();
//	}
//	public static void example4() throws SAPIException{
//		SimpleSource src = new SimpleSource("MySource4");
//		src.addChannel("count");
//		src.addListener(new SimpleSourceListener() {
//
//			@Override
//			public void onPut(String chName, Object[] data, double timestamp) {
//				System.out.println(chName +": "+data[0]+" @ "+timestamp);
//			}
//
//			@Override
//			public void onFlush() {
//				System.out.println("Sending Data");
//			}
//
//			@Override
//			public void onAddChannel(String channelName, String mimeType) {
//				System.out.println("Ready Channel: "+channelName +" as"+
//			mimeType);
//			}
//
//			@Override
//			public void onConnect() {
//				System.out.println("Connecting to Source");
//			}
//
//			@Override
//			public void onDisconect() {
//				System.out.println("Disconnecting");
//			}
//		});
//
//		src.connect();
//		src.put("count", 31);
//		src.flush();
//
//		src.put("count", 40);
//		src.flush();
//		src.close();
//	}
//
//	public static void example5() throws SAPIException{
//		SimpleSubscribeSink sink = new SimpleSubscribeSink("Sample");
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
//	}
//
//	public static void example6() throws SAPIException{
//		SimpleRequestSink sink = new SimpleRequestSink("Sink");
//
//		double time = 1111111111;
//		sink.monitorChannel("test/tmp");
//
//		ChannelMap map = sink.request(time);
//		//map.g
//		System.out.println(map.GetDataAsFloat32(0)[0]);
//
//		sink.close();
//	}
//
//}