package com.gvaneyck.spectate;
import java.io.IOException;
import java.io.InputStream;

class StreamGobbler extends Thread
{
	private InputStream in;
	private StringBuilder buffer = new StringBuilder();
	
	public StreamGobbler(InputStream in)
	{
		this.in = in;
		this.start();
	}
	
	public void run()
	{
		try
		{
			int c;
			
			while ((c = in.read()) != -1)
				buffer.append((char)c);
		}
		catch (IOException e)
		{
			// Ignored
		}
	}
	
	public String getData()
	{
		return buffer.toString();
	}
}
