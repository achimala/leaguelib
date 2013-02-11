package com.gvaneyck.rtmp;
/**
 * <p>
 * Encodes and decodes to and from Base64 notation.
 * </p>
 * <p>
 * Homepage: <a href="http://iharder.net/base64">http://iharder.net/base64</a>.
 * </p>
 * 
 * <p>
 * I am placing this code in the Public Domain. Do with it as you will. This
 * software comes with no guarantees or warranties but with plenty of
 * well-wishing instead! Please visit <a
 * href="http://iharder.net/base64">http://iharder.net/base64</a> periodically
 * to check for updates or to contribute improvements.
 * </p>
 * 
 * Stripped down version of the code that only encodes byte arrays (GVE)
 * 
 * @author Robert Harder
 * @author rob@iharder.net
 * @version 2.2.1
 */
public class Base64
{
	/** The equals sign (=) as a byte. */
	private final static byte EQUALS_SIGN = (byte)'=';

	/** Preferred encoding. */
	private final static String PREFERRED_ENCODING = "UTF-8";

	private final static byte[] _STANDARD_ALPHABET = { (byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F', (byte)'G', (byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L', (byte)'M', (byte)'N', (byte)'O', (byte)'P', (byte)'Q', (byte)'R', (byte)'S', (byte)'T', (byte)'U', (byte)'V', (byte)'W', (byte)'X', (byte)'Y', (byte)'Z', (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g', (byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l', (byte)'m', (byte)'n', (byte)'o', (byte)'p', (byte)'q', (byte)'r', (byte)'s', (byte)'t', (byte)'u', (byte)'v', (byte)'w', (byte)'x', (byte)'y', (byte)'z', (byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', (byte)'6', (byte)'7', (byte)'8', (byte)'9', (byte)'+', (byte)'/' };

	private Base64()
	{
	}

	public static String encodeBytes(byte[] source)
	{
		int off = 0;
		int len = source.length;

		int len43 = len * 4 / 3;
		byte[] outBuff = new byte[(len43) // Main 4:3
				+ ((len % 3) > 0 ? 4 : 0)]; // Account for padding
		int d = 0;
		int e = 0;
		int len2 = len - 2;
		for (; d < len2; d += 3, e += 4)
			encode3to4(source, d + off, 3, outBuff, e);

		if (d < len)
		{
			encode3to4(source, d + off, len - d, outBuff, e);
			e += 4;
		}

		// Return value according to relevant encoding.
		try
		{
			return new String(outBuff, 0, e, PREFERRED_ENCODING);
		}
		catch (java.io.UnsupportedEncodingException uue)
		{
			return new String(outBuff, 0, e);
		}
	}

	private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset)
	{
		byte[] ALPHABET = _STANDARD_ALPHABET;

		// 1 2 3
		// 01234567890123456789012345678901 Bit position
		// --------000000001111111122222222 Array position from threeBytes
		// --------| || || || | Six bit groups to index ALPHABET
		// >>18 >>12 >> 6 >> 0 Right shift necessary
		// 0x3f 0x3f 0x3f Additional AND

		// Create buffer with zero-padding if there are only one or two
		// significant bytes passed in the array.
		// We have to shift left 24 in order to flush out the 1's that appear
		// when Java treats a value as negative that is cast from a byte to an
		// int.
		int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0) | (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0) | (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);

		switch (numSigBytes)
		{
		case 3:
			destination[destOffset] = ALPHABET[(inBuff >>> 18)];
			destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
			destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
			destination[destOffset + 3] = ALPHABET[(inBuff) & 0x3f];
			return destination;

		case 2:
			destination[destOffset] = ALPHABET[(inBuff >>> 18)];
			destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
			destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
			destination[destOffset + 3] = EQUALS_SIGN;
			return destination;

		case 1:
			destination[destOffset] = ALPHABET[(inBuff >>> 18)];
			destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
			destination[destOffset + 2] = EQUALS_SIGN;
			destination[destOffset + 3] = EQUALS_SIGN;
			return destination;

		default:
			return destination;
		}
	}
}
