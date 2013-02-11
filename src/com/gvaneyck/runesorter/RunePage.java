package com.gvaneyck.runesorter;

import com.gvaneyck.rtmp.TypedObject;

/**
 * Stores rune page info
 * 
 * @author Gvaneyck
 */
public class RunePage implements Comparable
{
	public Integer pageId;
	public String name;
	public Boolean current;
	public TypedObject page;

	public RunePage(TypedObject page)
	{
		name = page.getString("name");
		pageId = page.getInt("pageId");
		current = page.getBool("current");
		this.page = page;
	}
	
	public void swap(RunePage target)
	{
		String tempName = target.name;
		target.name = name;
		name = tempName;

		TypedObject tempPage = target.page;
		target.page = page;
		page = tempPage;

		Boolean tempCurrent = target.current;
		target.current = current;
		current = tempCurrent;
	}
	
	public TypedObject getSavePage(int summId)
	{
		TypedObject ret = new TypedObject("com.riotgames.platform.summoner.spellbook.SpellBookPageDTO");
		
		Object[] slots = page.getArray("slotEntries");
		Object[] saveSlots = new Object[slots.length];
		for (int i = 0; i < slots.length; i++)
		{
			TypedObject temp = (TypedObject)slots[i];
			TypedObject slot = new TypedObject("com.riotgames.platform.summoner.spellbook.SlotEntry");
			slot.put("runeSlotId", temp.get("runeSlotId"));
			slot.put("runeId", temp.get("runeId"));
			slot.put("futureData", null);
			slot.put("dataVersion", null);
			saveSlots[i] = slot;
		}
		ret.put("slotEntries", TypedObject.makeArrayCollection(saveSlots));
		
		ret.put("pageId", pageId);
		ret.put("name", name);
		ret.put("current", current);
		ret.put("summonerId", summId);
		ret.put("createDate", null);
		ret.put("futureData", null);
		ret.put("dataVersion", null);
		
		return ret;
	}
	
	public int compareTo(Object o)
	{
		if (o instanceof RunePage)
		{
			RunePage temp = (RunePage)o;
			return pageId.compareTo(temp.pageId);
		}
		return 0;
	}
	
	public String toString()
	{
		return name;
	}
}
