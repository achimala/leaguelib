package com.gvaneyck.rtmp;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Simple JSON parser
 * Assumes the JSON is properly structured
 * 
 * @author Gabriel Van Eyck
 */
public class JSON
{
	/**
	 * Parses JSON from a string
	 * 
	 * @param json The JSON to parse
	 * @return The parsed object
	 */
	public static Object parse(String json)
	{
		if (json == null)
			return null;
		
		LinkedList<Character> buff = new LinkedList<Character>();
		for (int i = 0; i < json.length(); i++)
			buff.add(json.charAt(i));
		return parse(buff);
	}

	/**
	 * Parses JSON from a linked list
	 * 
	 * @param json The JSON to parse
	 * @return The parsed object
	 */
	private static Object parse(LinkedList<Character> json)
	{
		char c = json.removeFirst();
		while (c == ' ')
			c = json.removeFirst();
		
		switch (c)
		{
		case '{':
			return parseObject(json);

		case '[':
			return parseArray(json);

		case '"':
			return parseString(json);
			
		default:
			json.addFirst(c);
			return parseOther(json);
		}
	}
	
	/**
	 * Parses a JSON object
	 * Assumes the opening curly brace has been consumed
	 * 
	 * @param json The JSON to parse
	 * @return The parsed object as a map
	 */
	private static TypedObject parseObject(LinkedList<Character> json)
	{
		TypedObject ret = new TypedObject(null);

		char c = json.removeFirst();
		if (c == '}') // Check for empty object
			return ret;
		json.addFirst(c);
		
		do
		{
			json.removeFirst(); // Read quote
			String key = parseString(json);
			json.removeFirst(); // Read colon
			Object val = parse(json);
			ret.put(key, val);
			
			c = json.removeFirst();
		}
		while (c != '}');
		
		return ret;
	}
	
	/**
	 * Parses a JSON array
	 * Assumes the opening square bracket has been consumed
	 * 
	 * @param json The JSON to parse
	 * @return The parsed array
	 */
	private static Object[] parseArray(LinkedList<Character> json)
	{
		char c = json.removeFirst();
		if (c == ']') // Check for empty array
			return new Object[0];
		json.addFirst(c);
		
		ArrayList<Object> temp = new ArrayList<Object>();
		
		do
		{
			temp.add(parse(json));
			c = json.removeFirst();
		}
		while (c != ']');
		
		Object[] ret = new Object[temp.size()];
		for (int i = 0; i < temp.size(); i++)
			ret[i] = temp.get(i);
		
		return ret;
	}

	/**
	 * Parses a JSON string
	 * Assumes the opening double quote has been consumed
	 * 
	 * @param json The JSON to parse
	 * @return The parsed string
	 */
	private static String parseString(LinkedList<Character> json)
	{
		boolean postBackslash = false;
		StringBuilder buff = new StringBuilder();
		char c;

		while ((c = json.removeFirst()) != '"' || postBackslash)
		{
			if (!postBackslash)
			{
				if (c == '\\')
					postBackslash = true;
				else
					buff.append(c);
			}
			else
			{
				switch (c)
				{
				case '"':
					buff.append('"');
					break;

				case '\\':
					buff.append('\\');
					break;

				case '/':
					buff.append('/');
					break;

				case 'b':
					buff.append('\b');
					break;

				case 'f':
					buff.append('\f');
					break;

				case 'n':
					buff.append('\n');
					break;

				case 'r':
					buff.append('\r');
					break;

				case 't':
					buff.append('\t');
					break;

				case 'u':
					StringBuilder temp = new StringBuilder();
					for (int i = 0; i < 4; i++)
						temp.append(json.removeFirst());
					buff.append((char)Integer.parseInt(temp.toString(), 16));
					break;
				}

				postBackslash = false;
			}
		}

		return buff.toString();
	}

	/**
	 * Parses JSON numbers, booleans, and null
	 * 
	 * @param json The JSON to parse
	 * @return The parsed object
	 */
	private static Object parseOther(LinkedList<Character> json)
	{
		char c = json.removeFirst();
		switch (c)
		{
		case 't':
			for (int i = 0; i < 3; i++)
				json.removeFirst();
			return true;

		case 'f':
			for (int i = 0; i < 4; i++)
				json.removeFirst();
			return false;

		case 'n':
			for (int i = 0; i < 3; i++)
				json.removeFirst();
			return null;

		default:
			StringBuilder buff = new StringBuilder();
			boolean isInt = true;
			while (c == '-' || c >= '0' && c <= '9' || c == '.')
			{
				if (c == '.')
					isInt = false;
				buff.append(c);
				c = json.removeFirst();
			}
			json.addFirst(c);

			try
			{
				if (isInt)
					return Integer.parseInt(buff.toString());
			}
			catch (NumberFormatException e) { }

			return Double.parseDouble(buff.toString());
		}
	}
}
