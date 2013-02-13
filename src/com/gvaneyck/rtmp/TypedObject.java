package com.gvaneyck.rtmp;
import java.util.Date;
import java.util.HashMap;

/**
 * A map that has a type, used to represent an object
 * 
 * @author Gabriel Van Eyck
 */
public class TypedObject extends HashMap<String, Object>
{
	private static final long serialVersionUID = 1244827787088018807L;

	public String type;
	
	/**
	 * Creates a typed object that is simply a map (null type)
	 */
	public TypedObject()
	{
		this.type = null;
	}

	/**
	 * Initializes the type of the object, null type implies a dynamic class
	 * (used for headers and some other things)
	 * 
	 * @param type The type of the object
	 */
	public TypedObject(String type)
	{
		this.type = type;
	}

	/**
	 * Creates a flex.messaging.io.ArrayCollection in the structure that the
	 * encoder expects
	 * 
	 * @param data The data for the ArrayCollection
	 * @return
	 */
	public static TypedObject makeArrayCollection(Object[] data)
	{
		TypedObject ret = new TypedObject("flex.messaging.io.ArrayCollection");
		ret.put("array", data);
		return ret;
	}

	/**
	 * Convenience for going through object hierarchy
	 * 
	 * @param key The key of the TypedObject
	 * @return The TypedObject
	 */
	public TypedObject getTO(String key)
	{
		return (TypedObject)get(key);
	}
	
    /**
     * Extracts the exception reason from an error message
     * (e.g. com.riotgames.platform.game.GameNotFoundException)
     */
    public String getExceptionMessage() {
        TypedObject cause = getTO("data").getTO("rootCause");
        if(cause == null)
            return null;
        return cause.type;
    }
    
	/**
	 * Convenience for retrieving Strings
	 * 
	 * @param key The key of the String
	 * @return The String
	 */
	public String getString(String key)
	{
		return (String)get(key);
	}

	/**
	 * Convenience for retrieving integers
	 * 
	 * @param key The key of the integer
	 * @return The integer
	 */
	public Integer getInt(String key)
	{
		Object val = get(key);
		if (val == null)
			return null;
		else if (val instanceof Integer)
			return (Integer)val;
		else 
			return ((Double)val).intValue();
	}

	/**
	 * Convenience for retrieving doubles
	 * 
	 * @param key The key of the double
	 * @return The double
	 */
	public Double getDouble(String key)
	{
		Object val = get(key);
		if (val == null)
			return null;
		else if (val instanceof Double)
			return (Double)val;
		else 
			return ((Integer)val).doubleValue();
	}
	
	/**
	 * Convenience for retrieving booleans
	 * 
	 * @param key The key of the boolean
	 * @return The boolean
	 */
	public Boolean getBool(String key)
	{
		return (Boolean)get(key);
	}

	/**
	 * Convenience for retrieving object arrays
	 * Also handles flex.messaging.io.ArrayCollection
	 * 
	 * @param key The key of the object array
	 * @return The object array
	 */
	public Object[] getArray(String key)
	{
		if (get(key) instanceof TypedObject && getTO(key).type.equals("flex.messaging.io.ArrayCollection"))
			return (Object[])getTO(key).get("array");
		else
			return (Object[])get(key);
	}
	
	/**
	 * Convenience for retrieving Date objects
	 * 
	 * @param key The key of the Date object
	 * @return The Date object
	 */
	public Date getDate(String key)
	{
		return (Date)get(key);
	}

	public String toString()
	{
		if (type == null)
			return super.toString();
		else if (type.equals("flex.messaging.io.ArrayCollection"))
		{
			StringBuilder sb = new StringBuilder();
			Object[] data = (Object[])get("array");
			sb.append("ArrayCollection:[");
			for (int i = 0; i < data.length; i++)
			{
				sb.append(data[i]);
				if (i < data.length - 1)
					sb.append(", ");
			}
			sb.append(']');
			return sb.toString();
		}
		else
			return type + ":" + super.toString();
	}
}
