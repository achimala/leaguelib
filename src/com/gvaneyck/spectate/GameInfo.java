package com.gvaneyck.spectate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gvaneyck.rtmp.TypedObject;

/**
 * Stores info for a LoL game
 * 
 * @author Gvaneyck
 */
public class GameInfo implements Comparable
{
	public int delay;
	public String type;
	public String targetSummoner;
	public Map<String, Player> players = new HashMap<String, Player>();
	public List<Player> teamOne = new ArrayList<Player>();
	public List<Player> teamTwo = new ArrayList<Player>();
	
	public GameInfo(String target, TypedObject gameData)
	{
		targetSummoner = target;
		
		TypedObject body = gameData.getTO("data").getTO("body");
		delay = body.getInt("reconnectDelay") - 5;
		
		TypedObject game = body.getTO("game");
		type = game.getString("queueTypeName");
		
		//Object[] bannedChamps = game.getArray("bannedChampions");
		
		Object[] team1 = game.getArray("teamOne");
		Object[] team2 = game.getArray("teamTwo");
		for (Object o : team1)
		{
			TypedObject to = (TypedObject)o;
			Player p = new Player();
			p.summoner = to.getString("summonerName");
			players.put(to.getString("summonerInternalName"), p);
			teamOne.add(p);
		}
		for (Object o : team2)
		{
			TypedObject to = (TypedObject)o;
			Player p = new Player();
			p.summoner = to.getString("summonerName");
			players.put(to.getString("summonerInternalName"), p);
			teamTwo.add(p);
		}
		
		Object[] selections = game.getArray("playerChampionSelections");
		for (Object o : selections)
		{
			TypedObject to = (TypedObject)o;
			Player p = players.get(to.getString("summonerInternalName"));
			p.champion = to.getInt("championId").toString();
			p.spell1 = to.getInt("spell1Id").toString();
			p.spell2 = to.getInt("spell2Id").toString();
		}
		
//		System.out.println(delay);
//		System.out.println(type);
//		System.out.println(targetSummoner);
//		for (Player p : players.values())
//			System.out.println(p.summoner + " " + p.champion + " " + p.spell1 + " " + p.spell2);
	}
	
	public boolean equals(Object o)
	{
		if (o instanceof GameInfo)
		{
			GameInfo temp = (GameInfo)o;
			return targetSummoner.equals(temp.targetSummoner);
		}
		return false;
	}

	public int compareTo(Object o)
	{
		if (o instanceof GameInfo)
		{
			GameInfo temp = (GameInfo)o;
			return targetSummoner.compareTo(temp.targetSummoner);
		}
		return 0;
	}
	
	public String toString()
	{
		if (delay > 0)
			return targetSummoner + " (" + delay + "s)";
		else
			return targetSummoner;
	}
}
