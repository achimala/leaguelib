package com.gvaneyck.rtmp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an object for later decoding
 * 
 * @author Gabriel Van Eyck
 */
public class ClassDefinition
{
	public String type;
	public boolean externalizable = false;
	public boolean dynamic = false;
	public List<String> members = new ArrayList<String>();
}
