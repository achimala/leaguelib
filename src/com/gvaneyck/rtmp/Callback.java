package com.gvaneyck.rtmp;
/**
 * Provides callback functionality
 * 
 * @author Gabriel Van Eyck
 */
public interface Callback
{
	/**
	 * The function to call after the result has been read
	 * 
	 * @param result The result for this callback
	 */
	public void callback(TypedObject result);
}
