package com.gvaneyck.rtmp;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Encodes AMF3 data and packets for RTMP
 * 
 * @author Gabriel Van Eyck
 */
public class AMF3Encoder
{
	/** RNG used for generating MessageIDs */
	private static Random rand = new Random();

	/** Used for generating timestamps in headers */
	private long startTime = System.currentTimeMillis();

	/**
	 * Adds headers to provided data
	 * 
	 * @param data
	 * @return The data with headers added
	 */
	public byte[] addHeaders(byte[] data)
	{
		List<Byte> result = new ArrayList<Byte>();

		// Header byte
		result.add((byte)0x03);

		// Timestamp
		long timediff = System.currentTimeMillis() - startTime;
		result.add((byte)((timediff & 0xFF0000) >> 16));
		result.add((byte)((timediff & 0x00FF00) >> 8));
		result.add((byte)(timediff & 0x0000FF));

		// Body size
		result.add((byte)((data.length & 0xFF0000) >> 16));
		result.add((byte)((data.length & 0x00FF00) >> 8));
		result.add((byte)(data.length & 0x0000FF));

		// Content type
		result.add((byte)0x11);

		// Source ID
		result.add((byte)0x00);
		result.add((byte)0x00);
		result.add((byte)0x00);
		result.add((byte)0x00);

		// Add body
		for (int i = 0; i < data.length; i++)
		{
			result.add(data[i]);
			if (i % 128 == 127 && i != data.length - 1)
				result.add((byte)0xC3);
		}

		byte[] ret = new byte[result.size()];
		for (int i = 0; i < ret.length; i++)
			ret[i] = result.get(i);

		return ret;
	}

	/**
	 * Encodes the given parameters as a connect packet
	 * 
	 * @param params The connection parameters
	 * @return The connection packet
	 * @throws NotImplementedException
	 * @throws EncodingException
	 */
	public byte[] encodeConnect(Map<String, Object> params) throws EncodingException, NotImplementedException
	{
		List<Byte> result = new ArrayList<Byte>();

		writeStringAMF0(result, "connect");
		writeIntAMF0(result, 1); // invokeId

		// Write params
		result.add((byte)0x11); // AMF3 object
		result.add((byte)0x09); // Array
		writeAssociativeArray(result, params);

		// Write service call args
		result.add((byte)0x01);
		result.add((byte)0x00); // false
		writeStringAMF0(result, "nil"); // "nil"
		writeStringAMF0(result, ""); // ""

		// Set up CommandMessage
		TypedObject cm = new TypedObject("flex.messaging.messages.CommandMessage");
		cm.put("messageRefType", null);
		cm.put("operation", 5);
		cm.put("correlationId", "");
		cm.put("clientId", null);
		cm.put("destination", "");
		cm.put("messageId", randomUID());
		cm.put("timestamp", 0d);
		cm.put("timeToLive", 0d);
		cm.put("body", new TypedObject(null));
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("DSMessagingVersion", 1d);
		headers.put("DSId", "my-rtmps");
		cm.put("headers", headers);

		// Write CommandMessage
		result.add((byte)0x11); // AMF3 object
		encode(result, cm);

		byte[] ret = new byte[result.size()];
		for (int i = 0; i < ret.length; i++)
			ret[i] = result.get(i);

		ret = addHeaders(ret);
		ret[7] = (byte)0x14; // Change message type

		return ret;
	}

	/**
	 * Encodes the given data as a connect packet
	 * 
	 * @param id The invoke ID
	 * @param params The data to invoke
	 * @return The invoke packet
	 * @throws NotImplementedException
	 * @throws EncodingException
	 */
	public byte[] encodeInvoke(int id, Object data) throws EncodingException, NotImplementedException
	{
		List<Byte> result = new ArrayList<Byte>();

		result.add((byte)0x00); // version
		result.add((byte)0x05); // type?
		writeIntAMF0(result, id); // invoke ID
		result.add((byte)0x05); // ???

		result.add((byte)0x11); // AMF3 object
		encode(result, data);

		byte[] ret = new byte[result.size()];
		for (int i = 0; i < ret.length; i++)
			ret[i] = result.get(i);

		ret = addHeaders(ret);

		return ret;
	}

	/**
	 * Encodes an object as AMF3
	 * 
	 * @param obj The object to encode
	 * @return The encoded object
	 * @throws NotImplementedException
	 * @throws EncodingException
	 */
	public byte[] encode(Object obj) throws EncodingException, NotImplementedException
	{
		List<Byte> result = new ArrayList<Byte>();
		encode(result, obj);

		byte[] ret = new byte[result.size()];
		for (int i = 0; i < ret.length; i++)
			ret[i] = result.get(i);

		return ret;
	}

	/**
	 * Encodes an object as AMF3 to the given buffer
	 * 
	 * @param ret The buffer to encode to
	 * @param obj The object to encode
	 * @throws EncodingException
	 * @throws NotImplementedException
	 */
	@SuppressWarnings("unchecked")
	public void encode(List<Byte> ret, Object obj) throws EncodingException, NotImplementedException
	{
		if (obj == null)
		{
			ret.add((byte)0x01);
		}
		else if (obj instanceof Boolean)
		{
			boolean val = (Boolean)obj;
			if (val)
				ret.add((byte)0x03);
			else
				ret.add((byte)0x02);
		}
		else if (obj instanceof Integer)
		{
			ret.add((byte)0x04);
			writeInt(ret, (Integer)obj);
		}
		else if (obj instanceof Double)
		{
			ret.add((byte)0x05);
			writeDouble(ret, (Double)obj);
		}
		else if (obj instanceof String)
		{
			ret.add((byte)0x06);
			writeString(ret, (String)obj);
		}
		else if (obj instanceof Date)
		{
			ret.add((byte)0x08);
			writeDate(ret, (Date)obj);
		}
		// Must precede Object[] check
		else if (obj instanceof Byte[])
		{
			ret.add((byte)0x0C);
			writeByteArray(ret, (byte[])obj);
		}
		else if (obj instanceof Object[])
		{
			ret.add((byte)0x09);
			writeArray(ret, (Object[])obj);
		}
		// Must precede Map check
		else if (obj instanceof TypedObject)
		{
			ret.add((byte)0x0A);
			writeObject(ret, (TypedObject)obj);
		}
		else if (obj instanceof Map)
		{
			ret.add((byte)0x09);
			writeAssociativeArray(ret, (Map<String, Object>)obj);
		}
		else
		{
			throw new EncodingException("Unexpected object type: " + obj.getClass().getName());
		}
	}

	/**
	 * Encodes an integer as AMF3 to the given buffer
	 * 
	 * @param ret The buffer to encode to
	 * @param val The integer to encode
	 */
	private void writeInt(List<Byte> ret, int val)
	{
		if (val < 0 || val >= 0x200000)
		{
			ret.add((byte)(((val >> 22) & 0x7f) | 0x80));
			ret.add((byte)(((val >> 15) & 0x7f) | 0x80));
			ret.add((byte)(((val >> 8) & 0x7f) | 0x80));
			ret.add((byte)(val & 0xff));
		}
		else
		{
			if (val >= 0x4000)
			{
				ret.add((byte)(((val >> 14) & 0x7f) | 0x80));
			}
			if (val >= 0x80)
			{
				ret.add((byte)(((val >> 7) & 0x7f) | 0x80));
			}
			ret.add((byte)(val & 0x7f));
		}
	}

	/**
	 * Encodes a double as AMF3 to the given buffer
	 * 
	 * @param ret The buffer to encode to
	 * @param val The double to encode
	 */
	private void writeDouble(List<Byte> ret, double val)
	{
		if (Double.isNaN(val))
		{
			ret.add((byte)0x7F);
			ret.add((byte)0xFF);
			ret.add((byte)0xFF);
			ret.add((byte)0xFF);
			ret.add((byte)0xE0);
			ret.add((byte)0x00);
			ret.add((byte)0x00);
			ret.add((byte)0x00);
		}
		else
		{
			byte[] temp = new byte[8];
			ByteBuffer.wrap(temp).putDouble(val);
			for (byte b : temp)
				ret.add(b);
		}
	}

	/**
	 * Encodes a string as AMF3 to the given buffer
	 * 
	 * @param ret The buffer to encode to
	 * @param val The string to encode
	 * @throws EncodingException
	 */
	private void writeString(List<Byte> ret, String val) throws EncodingException
	{
		byte[] temp = null;
		try
		{
			temp = val.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new EncodingException("Unable to encode string as UTF-8: " + val);
		}

		writeInt(ret, (temp.length << 1) | 1);

		for (byte b : temp)
			ret.add(b);
	}

	/**
	 * Encodes a date as AMF3 to the given buffer
	 * 
	 * @param ret The buffer to encode to
	 * @param val The date to encode
	 */
	private void writeDate(List<Byte> ret, Date val)
	{
		ret.add((byte)0x01);
		writeDouble(ret, (double)val.getTime());
	}

	/**
	 * Encodes an array as AMF3 to the given buffer
	 * 
	 * @param ret The buffer to encode to
	 * @param val The array to encode
	 * @throws EncodingException
	 * @throws NotImplementedException
	 */
	private void writeArray(List<Byte> ret, Object[] val) throws EncodingException, NotImplementedException
	{
		writeInt(ret, (val.length << 1) | 1);
		ret.add((byte)0x01);
		for (Object obj : val)
			encode(ret, obj);
	}

	/**
	 * Encodes an associative array as AMF3 to the given buffer
	 * 
	 * @param ret The buffer to encode to
	 * @param val The associative array to encode
	 * @throws EncodingException
	 * @throws NotImplementedException
	 */
	private void writeAssociativeArray(List<Byte> ret, Map<String, Object> val) throws EncodingException, NotImplementedException
	{
		ret.add((byte)0x01);
		for (String key : val.keySet())
		{
			writeString(ret, key);
			encode(ret, val.get(key));
		}
		ret.add((byte)0x01);
	}

	/**
	 * Encodes an object as AMF3 to the given buffer
	 * 
	 * @param ret The buffer to encode to
	 * @param val The object to encode
	 * @throws EncodingException
	 * @throws NotImplementedException
	 */
	private void writeObject(List<Byte> ret, TypedObject val) throws EncodingException, NotImplementedException
	{
		if (val.type == null || val.type.equals(""))
		{
			ret.add((byte)0x0B); // Dynamic class

			ret.add((byte)0x01); // No class name
			for (String key : val.keySet())
			{
				writeString(ret, key);
				encode(ret, val.get(key));
			}
			ret.add((byte)0x01); // End of dynamic
		}
		else if (val.type.equals("flex.messaging.io.ArrayCollection"))
		{
			ret.add((byte)0x07); // Externalizable
			writeString(ret, val.type);

			encode(ret, val.get("array"));
		}
		else
		{
			writeInt(ret, (val.size() << 4) | 3); // Inline + member count
			writeString(ret, val.type);

			List<String> keyOrder = new ArrayList<String>();
			for (String key : val.keySet())
			{
				writeString(ret, key);
				keyOrder.add(key);
			}

			for (String key : keyOrder)
				encode(ret, val.get(key));
		}
	}

	/**
	 * Not implemented
	 * 
	 * @param ret
	 * @param val
	 * @throws NotImplementedException
	 */
	private void writeByteArray(List<Byte> ret, byte[] val) throws NotImplementedException
	{
		throw new NotImplementedException("Encoding byte arrays is not implemented");
	}

	/**
	 * Encodes an integer as AMF0 to the given buffer
	 * 
	 * @param ret The buffer to encode to
	 * @param val The integer to encode
	 */
	private void writeIntAMF0(List<Byte> ret, int val)
	{
		ret.add((byte)0x00);

		byte[] temp = new byte[8];
		ByteBuffer.wrap(temp).putDouble((double)val);
		for (byte b : temp)
			ret.add(b);
	}

	/**
	 * Encodes a string as AMF0 to the given buffer
	 * 
	 * @param ret The buffer to encode to
	 * @param val The string to encode
	 * @throws EncodingException
	 */
	private void writeStringAMF0(List<Byte> ret, String val) throws EncodingException
	{
		byte[] temp = null;
		try
		{
			temp = val.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new EncodingException("Unable to encode string as UTF-8: " + val);
		}

		ret.add((byte)0x02);

		ret.add((byte)((temp.length & 0xFF00) >> 8));
		ret.add((byte)(temp.length & 0x00FF));

		for (byte b : temp)
			ret.add(b);
	}

	/**
	 * Generates a random UID, used for messageIDs
	 * 
	 * @return A random UID
	 */
	public static String randomUID()
	{
		byte[] bytes = new byte[16];
		rand.nextBytes(bytes);

		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)
		{
			if (i == 4 || i == 6 || i == 8 || i == 10)
				ret.append('-');
			ret.append(String.format("%02X", bytes[i]));
		}

		return ret.toString();
	}
}
