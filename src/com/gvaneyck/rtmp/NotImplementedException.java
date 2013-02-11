package com.gvaneyck.rtmp;
import java.io.IOException;

/**
 * A basic exception used within AMF3Encoder and AMF3Decoder for notifying of
 * unimplemented functionality
 * 
 * @author Gabriel Van Eyck
 */
public class NotImplementedException extends IOException
{
	private static final long serialVersionUID = -1806306151286578816L;

	public NotImplementedException(String message)
	{
		super(message);
	}
}
