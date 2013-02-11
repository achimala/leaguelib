package com.gvaneyck.rtmp;
import java.io.IOException;

/**
 * A basic exception used within AMF3Encoder and AMF3Decoder to notify of
 * parsing problems.
 * 
 * @author Gabriel Van Eyck
 */
public class EncodingException extends IOException
{
	private static final long serialVersionUID = 1476074395589836889L;

	public EncodingException(String message)
	{
		super(message);
	}
}
