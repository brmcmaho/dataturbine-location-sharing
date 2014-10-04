package edu.ucsd.rbnb.simple;

import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.SAPIException;

public class DataFrame {

	private ChannelMap map;
	public DataFrame(ChannelMap map){
		this.map = map;
	}
	
	public short getAsShort(String channel) throws SAPIException{
		return map.GetDataAsInt16(getIndex(channel))[0];
	}
	public int getAsInt(String channel) throws SAPIException{
		return map.GetDataAsInt32(getIndex(channel))[0];
	}
	public long getAsLong(String channel) throws SAPIException{
		return map.GetDataAsInt64(getIndex(channel))[0];
	}
	public float getAsFloat(String channel) throws SAPIException{
		return map.GetDataAsFloat32(getIndex(channel))[0];
	}
	public double getAsDouble(String channel) throws SAPIException{
		return map.GetDataAsFloat64(getIndex(channel))[0];
	}
	public String getAsString(String channel) throws SAPIException{
		return map.GetDataAsString(getIndex(channel))[0];
	}
	
	public byte[] getAsByteArray(String channel) throws SAPIException{
		return map.GetDataAsInt8(getIndex(channel));
	}
	public short[] getAsShortArray(String channel) throws SAPIException{
		return map.GetDataAsInt16(getIndex(channel));
	}
	public int[] getAsIntArray(String channel) throws SAPIException{
		return map.GetDataAsInt32(getIndex(channel));
	}
	public long[] getAsLongArray(String channel) throws SAPIException{
		return map.GetDataAsInt64(getIndex(channel));
	}
	public float[] getAsFloatArray(String channel) throws SAPIException{
		return map.GetDataAsFloat32(getIndex(channel));
	}
	public double[] getAsDoubleArray(String channel) throws SAPIException{
		return map.GetDataAsFloat64(getIndex(channel));
	}
	public String[] getAsStringArray(String channel) throws SAPIException{
		return map.GetDataAsString(getIndex(channel));
	}
	
	private int  getIndex(String channel) throws SAPIException{
		int index = map.GetIndex(channel);
		if(index == -1)
			throw new SAPIException("Channel '"+channel+"' not set");
		return index;
	}
}
