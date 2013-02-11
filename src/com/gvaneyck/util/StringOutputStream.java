package com.gvaneyck.util;
import java.io.IOException;
import java.io.OutputStream;

/**
 * OutputSteam for a String
 * 
 * @author Gabriel Van Eyck
 */
public class StringOutputStream extends OutputStream {
	
	StringBuffer sb;
	
	public StringOutputStream(StringBuffer sb)
	{
		this.sb = sb;
	}
	
	public void close() throws IOException
	{
		sb = new StringBuffer();
	}
	
	public void flush() throws IOException	{ }
	
	public void write(byte[] b) throws IOException
	{
		sb.append(new String(b));
	}
	
	public void write(byte b) throws IOException
	{
		sb.append((char)b);
	}

	public void write(int i) throws IOException
	{
		sb.append((char)i);
	}
	
	public String getData()
	{
		return sb.toString();
	}
}
