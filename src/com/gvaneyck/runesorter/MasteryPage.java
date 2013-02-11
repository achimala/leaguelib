package com.gvaneyck.runesorter;

import com.gvaneyck.rtmp.TypedObject;

/**
 * Stores mastery page info
 * 
 * @author Gvaneyck
 */
public class MasteryPage implements Comparable
{
	public Integer pageId;
	public String name;
	public Boolean current;
	public Object[] talents;

	public MasteryPage(TypedObject page)
	{
		name = page.getString("name");
		pageId = page.getInt("pageId");
		current = page.getBool("current");
		talents = page.getArray("talentEntries");
	}
	
	public void swap(MasteryPage target)
	{
		String tempName = target.name;
		target.name = name;
		name = tempName;

		Object[] tempTalents = target.talents;
		target.talents = talents;
		talents = tempTalents;

		Boolean tempCurrent = target.current;
		target.current = current;
		current = tempCurrent;
	}
	
	public TypedObject getSavePage(int summId)
	{
		TypedObject ret = new TypedObject("com.riotgames.platform.summoner.masterybook.MasteryBookPageDTO");
		
		Object[] saveTalents = new Object[talents.length];
		for (int i = 0; i < talents.length; i++)
		{
			TypedObject temp = (TypedObject)talents[i];
			TypedObject slot = new TypedObject("com.riotgames.platform.summoner.masterybook.TalentEntry");
			slot.put("talentId", temp.get("talentId"));
			slot.put("rank", temp.get("rank"));
			slot.put("futureData", null);
			slot.put("dataVersion", null);
			saveTalents[i] = slot;
		}
		ret.put("talentEntries", TypedObject.makeArrayCollection(saveTalents));
		
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
		if (o instanceof MasteryPage)
		{
			MasteryPage temp = (MasteryPage)o;
			return pageId.compareTo(temp.pageId);
		}
		return 0;
	}
	
	public String toString()
	{
		return name;
	}
}
