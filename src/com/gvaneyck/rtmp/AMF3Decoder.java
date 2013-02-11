package com.gvaneyck.rtmp;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Decodes AMF3 data and packets from RTMP
 * 
 * @author Gabriel Van Eyck
 */
public class AMF3Decoder
{
	/** Stores the data to be consumed while decoding */
	private byte[] dataBuffer;
	private int dataPos;

	/** Lists of references and class definitions seen so far */
	private List<String> stringReferences = new ArrayList<String>();
	private List<Object> objectReferences = new ArrayList<Object>();
	private List<ClassDefinition> classDefinitions = new ArrayList<ClassDefinition>();

	/**
	 * Resets all the reference lists
	 */
	public void reset()
	{
		stringReferences.clear();
		objectReferences.clear();
		classDefinitions.clear();
	}

	/**
	 * Decodes the result of a connect call
	 * 
	 * @param data The connect result
	 * @return The decoded object
	 * @throws EncodingException
	 * @throws NotImplementedException
	 */
	public TypedObject decodeConnect(byte[] data) throws NotImplementedException, EncodingException
	{
		reset();

		dataBuffer = data;
		dataPos = 0;

		TypedObject result = new TypedObject("Invoke");
		result.put("result", decodeAMF0());
		result.put("invokeId", decodeAMF0());
		result.put("serviceCall", decodeAMF0());
		result.put("data", decodeAMF0());

		if (dataPos != dataBuffer.length)
			throw new EncodingException("Did not consume entire buffer: " + dataPos + " of " + dataBuffer.length);

		return result;
	}

	/**
	 * Decodes the result of a invoke call
	 * 
	 * @param data The invoke result
	 * @return The decoded object
	 * @throws EncodingException
	 * @throws NotImplementedException
	 */
	public TypedObject decodeInvoke(byte[] data) throws NotImplementedException, EncodingException
	{
		reset();

		dataBuffer = data;
		dataPos = 0;

		TypedObject result = new TypedObject("Invoke");
		if (dataBuffer[0] == 0x00)
		{
			dataPos++;
			result.put("version", 0x00);
		}
		result.put("result", decodeAMF0());
		result.put("invokeId", decodeAMF0());
		result.put("serviceCall", decodeAMF0());
		result.put("data", decodeAMF0());

		if (dataPos != dataBuffer.length)
			throw new EncodingException("Did not consume entire buffer: " + dataPos + " of " + dataBuffer.length);

		return result;
	}

	/**
	 * Decodes data according to AMF3
	 * 
	 * @param data The data to decode
	 * @return The decoded object
	 * @throws NotImplementedException
	 * @throws EncodingException
	 */
	public Object decode(byte[] data) throws EncodingException, NotImplementedException
	{
		dataBuffer = data;
		dataPos = 0;

		Object result = decode();

		if (dataPos != dataBuffer.length)
			throw new EncodingException("Did not consume entire buffer: " + dataPos + " of " + dataBuffer.length);

		return result;
	}

	/**
	 * Decodes AMF3 data in the buffer
	 * 
	 * @return The decoded object
	 * @throws EncodingException
	 * @throws NotImplementedException
	 */
	private Object decode() throws EncodingException, NotImplementedException
	{
		byte type = readByte();
		switch (type)
		{
		case 0x00:
			throw new EncodingException("Undefined data type");

		case 0x01:
			return null;

		case 0x02:
			return false;

		case 0x03:
			return true;

		case 0x04:
			return readInt();

		case 0x05:
			return readDouble();

		case 0x06:
			return readString();

		case 0x07:
			return readXML();

		case 0x08:
			return readDate();

		case 0x09:
			return readArray();

		case 0x0A:
			return readObject();

		case 0x0B:
			return readXMLString();

		case 0x0C:
			return readByteArray();
		}

		throw new EncodingException("Unexpected AMF3 data type: " + type);
	}

	/**
	 * Removes a single byte from the data buffer
	 * 
	 * @return The next byte in the data buffer
	 */
	private byte readByte()
	{
		byte ret = dataBuffer[dataPos];
		dataPos++;
		return ret;
	}

	/**
	 * Removes a single byte from the data buffer as an unsigned integer
	 * 
	 * @return The next byte in the data buffer as an unsigned integer
	 */
	private int readByteAsInt()
	{
		int ret = readByte();
		if (ret < 0)
			ret += 256;
		return ret;
	}

	/**
	 * Removes the next 'length' bytes from the data buffer
	 * 
	 * @param length The number of bytes to retrieve
	 * @return The next 'length' bytes in the data buffer
	 */
	private byte[] readBytes(int length)
	{
		byte[] ret = new byte[length];
		for (int i = 0; i < length; i++)
		{
			ret[i] = dataBuffer[dataPos];
			dataPos++;
		}
		return ret;
	}

	/**
	 * Decodes an AMF3 integer
	 * 
	 * @return The decoded integer
	 * @author FluorineFX
	 */
	private int readInt()
	{
		int ret = readByteAsInt();
		int tmp;

		if (ret < 128)
		{
			return ret;
		}
		else
		{
			ret = (ret & 0x7f) << 7;
			tmp = readByteAsInt();
			if (tmp < 128)
			{
				ret = ret | tmp;
			}
			else
			{
				ret = (ret | tmp & 0x7f) << 7;
				tmp = readByteAsInt();
				if (tmp < 128)
				{
					ret = ret | tmp;
				}
				else
				{
					ret = (ret | tmp & 0x7f) << 8;
					tmp = readByteAsInt();
					ret = ret | tmp;
				}
			}
		}

		// Sign extend
		int mask = 1 << 28;
		int r = -(ret & mask) | ret;
		return r;
	}

	/**
	 * Decodes an AMF3 double
	 * 
	 * @return The decoded double
	 */
	private double readDouble()
	{
		long value = 0;
		for (int i = 0; i < 8; i++)
			value = (value << 8) + readByteAsInt();

		return Double.longBitsToDouble(value);
	}

	/**
	 * Decodes an AMF3 string
	 * 
	 * @return The decoded string
	 * @throws EncodingException
	 */
	private String readString() throws EncodingException
	{
		int handle = readInt();
		boolean inline = ((handle & 1) != 0);
		handle = handle >> 1;

		if (inline)
		{
			if (handle == 0)
				return "";

			byte[] data = readBytes(handle);

			String str;
			try
			{
				str = new String(data, "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				throw new EncodingException("Error parsing AMF3 string from " + data);
			}

			stringReferences.add(str);

			return str;
		}
		else
		{
			return stringReferences.get(handle);
		}
	}

	/**
	 * Not implemented
	 * 
	 * @return
	 * @throws NotImplementedException
	 */
	private String readXML() throws NotImplementedException
	{
		throw new NotImplementedException("Reading of XML is not implemented");
	}

	/**
	 * Decodes an AMF3 date
	 * 
	 * @return The decoded date
	 */
	private Date readDate()
	{
		int handle = readInt();
		boolean inline = ((handle & 1) != 0);
		handle = handle >> 1;

		if (inline)
		{
			long ms = (long)readDouble();
			Date d = new Date(ms);

			objectReferences.add(d);

			return d;
		}
		else
		{
			return (Date)objectReferences.get(handle);
		}
	}

	/**
	 * Decodes an AMF3 (non-associative) array
	 * 
	 * @return The decoded array
	 * @throws EncodingException
	 * @throws NotImplementedException
	 */
	private Object[] readArray() throws EncodingException, NotImplementedException
	{
		int handle = readInt();
		boolean inline = ((handle & 1) != 0);
		handle = handle >> 1;

		if (inline)
		{
			String key = readString();
			if (key != null && !key.equals(""))
				throw new NotImplementedException("Associative arrays are not supported");

			Object[] ret = new Object[handle];
			objectReferences.add(ret);

			for (int i = 0; i < handle; i++)
				ret[i] = decode();

			return ret;
		}
		else
		{
			return (Object[])objectReferences.get(handle);
		}
	}

	/**
	 * Decodes an AMF3 object
	 * 
	 * @return The decoded object
	 * @throws EncodingException
	 * @throws NotImplementedException
	 */
	private Object readObject() throws EncodingException, NotImplementedException
	{
		int handle = readInt();
		boolean inline = ((handle & 1) != 0);
		handle = handle >> 1;

		if (inline)
		{
			boolean inlineDefine = ((handle & 1) != 0);
			handle = handle >> 1;

			ClassDefinition cd;
			if (inlineDefine)
			{
				cd = new ClassDefinition();
				cd.type = readString();

				cd.externalizable = ((handle & 1) != 0);
				handle = handle >> 1;
				cd.dynamic = ((handle & 1) != 0);
				handle = handle >> 1;

				for (int i = 0; i < handle; i++)
					cd.members.add(readString());

				classDefinitions.add(cd);
			}
			else
			{
				cd = classDefinitions.get(handle);
			}

			TypedObject ret = new TypedObject(cd.type);

			// Need to add reference here due to circular references
			objectReferences.add(ret);

			if (cd.externalizable)
			{
				if (cd.type.equals("DSK"))
					ret = readDSK();
				else if (cd.type.equals("DSA"))
					ret = readDSA();
				else if (cd.type.equals("flex.messaging.io.ArrayCollection"))
				{
					Object obj = decode();
					ret = TypedObject.makeArrayCollection((Object[])obj);
				}
				else if (cd.type.equals("com.riotgames.platform.systemstate.ClientSystemStatesNotification") || cd.type.equals("com.riotgames.platform.broadcast.BroadcastNotification"))
				{
					int size = 0;
					for (int i = 0; i < 4; i++)
						size = size * 256 + readByteAsInt();

					String json;
					try { json = new String(readBytes(size), "UTF-8"); } catch (UnsupportedEncodingException e) { throw new EncodingException(e.toString()); }
					ret = (TypedObject)JSON.parse(json);
					ret.type = cd.type;
				}
				else
				{
					for (int i = dataPos; i < dataBuffer.length; i++)
						System.out.print(String.format("%02X", dataBuffer[i]));
					System.out.println();
					throw new NotImplementedException("Externalizable not handled for " + cd.type);
				}
			}
			else
			{
				for (int i = 0; i < cd.members.size(); i++)
				{
					String key = cd.members.get(i);
					Object value = decode();
					ret.put(key, value);
				}

				if (cd.dynamic)
				{
					String key;
					while ((key = readString()).length() != 0)
					{
						Object value = decode();
						ret.put(key, value);
					}
				}
			}

			return ret;
		}
		else
		{
			return objectReferences.get(handle);
		}
	}

	/**
	 * Not implemented
	 * 
	 * @return
	 * @throws NotImplementedException
	 */
	private String readXMLString() throws NotImplementedException
	{
		throw new NotImplementedException("Reading of XML strings is not implemented");
	}

	/**
	 * Decodes an AMF3 byte array
	 * 
	 * @return The decoded byte array
	 */
	private byte[] readByteArray()
	{
		int handle = readInt();
		boolean inline = ((handle & 1) != 0);
		handle = handle >> 1;

		if (inline)
		{
			byte[] ret = readBytes(handle);
			objectReferences.add(ret);
			return ret;
		}
		else
		{
			return (byte[])objectReferences.get(handle);
		}
	}

	/**
	 * Decodes a DSA
	 * 
	 * @return The decoded DSA
	 * @throws NotImplementedException
	 * @throws EncodingException
	 */
	private TypedObject readDSA() throws EncodingException, NotImplementedException
	{
		TypedObject ret = new TypedObject("DSA");

		int flag;
		List<Integer> flags = readFlags();
		for (int i = 0; i < flags.size(); i++)
		{
			flag = flags.get(i);
			int bits = 0;
			if (i == 0)
			{
				if ((flag & 0x01) != 0)
					ret.put("body", decode());
				if ((flag & 0x02) != 0)
					ret.put("clientId", decode());
				if ((flag & 0x04) != 0)
					ret.put("destination", decode());
				if ((flag & 0x08) != 0)
					ret.put("headers", decode());
				if ((flag & 0x10) != 0)
					ret.put("messageId", decode());
				if ((flag & 0x20) != 0)
					ret.put("timeStamp", decode());
				if ((flag & 0x40) != 0)
					ret.put("timeToLive", decode());
				bits = 7;
			}
			else if (i == 1)
			{
				if ((flag & 0x01) != 0)
				{
					readByte();
					byte[] temp = readByteArray();
					ret.put("clientIdBytes", temp);
					ret.put("clientId", byteArrayToID(temp));
				}
				if ((flag & 0x02) != 0)
				{
					readByte();
					byte[] temp = readByteArray();
					ret.put("messageIdBytes", temp);
					ret.put("messageId", byteArrayToID(temp));
				}
				bits = 2;
			}

			readRemaining(flag, bits);
		}

		flags = readFlags();
		for (int i = 0; i < flags.size(); i++)
		{
			flag = flags.get(i);
			int bits = 0;

			if (i == 0)
			{
				if ((flag & 0x01) != 0)
					ret.put("correlationId", decode());
				if ((flag & 0x02) != 0)
				{
					readByte();
					byte[] temp = readByteArray();
					ret.put("correlationIdBytes", temp);
					ret.put("correlationId", byteArrayToID(temp));
				}
				bits = 2;
			}

			readRemaining(flag, bits);
		}
		
		return ret;
	}

	/**
	 * Decodes a DSK
	 * 
	 * @return The decoded DSK
	 * @throws NotImplementedException
	 * @throws EncodingException
	 */
	private TypedObject readDSK() throws EncodingException, NotImplementedException
	{
		// DSK is just a DSA + extra set of flags/objects
		TypedObject ret = readDSA();
		ret.type = "DSK";
		
		List<Integer> flags = readFlags();
		for (int i = 0; i < flags.size(); i++)
			readRemaining(flags.get(i), 0);

		return ret;
	}

	private List<Integer> readFlags()
	{
		List<Integer> flags = new ArrayList<Integer>();
		int flag;
		do
		{
			flag = readByteAsInt();
			flags.add(flag);
		} while ((flag & 0x80) != 0);

		return flags;
	}
	
	private void readRemaining(int flag, int bits) throws EncodingException, NotImplementedException
	{
		// For forwards compatibility, read in any other flagged objects to
		// preserve the integrity of the input stream...
		if ((flag >> bits) != 0)
		{
			for (int o = bits; o < 6; o++)
			{
				if (((flag >> o) & 1) != 0)
					decode();
			}
		}
	}

	/**
	 * Converts an array of bytes into an ID string
	 * 
	 * @return The ID string
	 */
	private String byteArrayToID(byte[] data)
	{
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < data.length; i++)
		{
			if (i == 4 || i == 6 || i == 8 || i == 10)
				ret.append('-');
			ret.append(String.format("%02x", data[i]));
		}

		return ret.toString();
	}

	/**
	 * Decodes the next AMF0 object from the buffer
	 * 
	 * @return The decoded object
	 * @throws NotImplementedException
	 * @throws EncodingException
	 */
	private Object decodeAMF0() throws NotImplementedException, EncodingException
	{
		int type = readByte();
		switch (type)
		{
		case 0x00:
			return readIntAMF0();

		case 0x02:
			return readStringAMF0();

		case 0x03:
			return readObjectAMF0();

		case 0x05:
			return null;

		case 0x11: // AMF3
			return decode();
		}

		throw new NotImplementedException("AMF0 type not supported: " + type);
	}

	/**
	 * Decodes an AMF0 string
	 * 
	 * @return The decoded string
	 * @throws EncodingException
	 */
	private String readStringAMF0() throws EncodingException
	{
		int length = (readByteAsInt() << 8) + readByteAsInt();
		if (length == 0)
			return "";

		byte[] data = readBytes(length);

		// UTF-8 applicable?
		String str;
		try
		{
			str = new String(data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new EncodingException("Error parsing AMF0 string from " + data);
		}

		return str;
	}

	/**
	 * Decodes an AMF0 integer
	 * 
	 * @return The decoded integer
	 */
	private int readIntAMF0()
	{
		return (int)readDouble();
	}

	/**
	 * Decodes an AMF0 object
	 * 
	 * @return The decoded object
	 * @throws EncodingException
	 * @throws NotImplementedException 
	 */
	private TypedObject readObjectAMF0() throws EncodingException, NotImplementedException
	{
		TypedObject body = new TypedObject("Body");
		String key;
		while (!(key = readStringAMF0()).equals(""))
		{
			byte b = readByte();
			if (b == 0x00)
				body.put(key, readDouble());
			else if (b == 0x02)
				body.put(key, readStringAMF0());
			else if (b == 0x05)
				body.put(key, null);
			else
				throw new NotImplementedException("AMF0 type not supported: " + b);
		}
		readByte(); // Skip object end marker

		return body;
	}
}
