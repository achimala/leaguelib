/*
 *  This file is part of LeagueLib.
 *  LeagueLib is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  LeagueLib is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with LeagueLib.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.achimala.leaguelib.models;

import com.gvaneyck.rtmp.TypedObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Arrays;

public class LeagueGame implements PlayerList {
    private class ObserverCredentials {
        private String _serverIP, _encryptionKey;
        private int _serverPort=-1;
        
        public ObserverCredentials(TypedObject obj) {
            _serverIP = obj.getString("observerServerIp");
            _serverPort = obj.getInt("observerServerPort");
            _encryptionKey = obj.getString("observerEncryptionKey");
        }
        
        public void setServerIPAddress(String ip) {
            _serverIP = ip;
        }
        
        public void setServerPort(int port) {
            _serverPort = port;
        }
        
        public void setEncryptionKey(String key) {
            _encryptionKey = key;
        }
        
        public String getServerIPAddress() {
            return _serverIP;
        }
        
        public int getServerPort() {
            return _serverPort;
        }
        
        public String getEncryptionKey() {
            return _encryptionKey;
        }
    }
    
    // TODO: eventually put this in its own enum?
    private String _gameType, _gameMode;
    private LeagueMatchmakingQueue _queue = null;
    private int _id=-1;
    private List<LeagueSummoner> _playerTeam, _enemyTeam;
    private TeamType _playerTeamType = TeamType.BLUE;
    private TeamType _enemyTeamType = TeamType.PURPLE;
    private Map<String, LeagueChampion> _playerChampionSelections;
    private Map<String, LeagueSummoner> _summoners;
    private Map<TeamType, List<LeagueChampion>> _bannedChampions;
    private ObserverCredentials _observerCredentials;
    
    public LeagueGame() {
    }
    
    private void fillListWithPlayers(List<LeagueSummoner> list, Object[] team, LeagueSummoner primarySummoner) {
        for(Object o : team) {
            TypedObject to = (TypedObject)o;
            LeagueSummoner sum = new LeagueSummoner(to, primarySummoner.getServer(), true);
            if(sum.isEqual(primarySummoner))
                sum = primarySummoner;
            _summoners.put(sum.getInternalName(), sum);
            list.add(sum);
        }
    }
    
    public LeagueGame(TypedObject obj, LeagueSummoner primarySummoner) {
        _observerCredentials = new ObserverCredentials(obj.getTO("playerCredentials"));
        obj = obj.getTO("game");
        _id = obj.getInt("id");
        _gameType = obj.getString("gameType");
        _gameMode = obj.getString("gameMode");
        _queue = LeagueMatchmakingQueue.valueOf(obj.getString("queueTypeName"));
        _playerTeam = new ArrayList<LeagueSummoner>();
        _enemyTeam = new ArrayList<LeagueSummoner>();
        _summoners = new HashMap<String, LeagueSummoner>();
        fillListWithPlayers(_playerTeam, obj.getArray("teamOne"), primarySummoner);
        fillListWithPlayers(_enemyTeam, obj.getArray("teamTwo"), primarySummoner);
        if(!_playerTeam.contains(primarySummoner))
            swapTeams();
        
        _playerChampionSelections = new HashMap<String, LeagueChampion>();
        for(Object o : obj.getArray("playerChampionSelections")) {
            TypedObject to = (TypedObject)o;
            _playerChampionSelections.put(to.getString("summonerInternalName"), 
					  LeagueChampion.getChampionWithId(to.getInt("championId")));
        }
	_bannedChampions = new HashMap<TeamType, List<LeagueChampion>>();
	for(TeamType t : TeamType.values()) {
	    _bannedChampions.put(t, new ArrayList<LeagueChampion>());
	}
	Object[] sortedBans = obj.getArray("bannedChampions");
	Arrays.sort(sortedBans, new Comparator<Object>() {
		public int compare(Object o1, Object o2) {
		    TypedObject to1 = (TypedObject)o1;
		    TypedObject to2 = (TypedObject)o2;
		    return to1.getInt("pickTurn").compareTo(to2.getInt("pickTurn"));
		}
	    });
	for(Object o : sortedBans) {
	    TypedObject to = (TypedObject)o;
	    TeamType teamType = TeamType.getFromId(to.getInt("teamId"));
	    LeagueChampion champion = LeagueChampion.getChampionWithId(to.getInt("championId"));
	    _bannedChampions.get(teamType).add(champion);
	}
	
    }
    
    public void setGameType(String type) {
        _gameType = type;
    }
    
    public void setGameMode(String mode) {
        _gameMode = mode;
    }
    
    public void setId(int id) {
        _id = id;
    }
    
    public void setQueue(LeagueMatchmakingQueue queue) {
        _queue = queue;
    }
    
    public void swapTeams() {
        List<LeagueSummoner> temp = _playerTeam;
        _playerTeam = _enemyTeam;
        _enemyTeam = temp;
	_playerTeamType = TeamType.PURPLE;
	_enemyTeamType = TeamType.BLUE;
    }
    
    public String getGameType() {
        return _gameType;
    }
    
    public String getGameMode() {
        return _gameMode;
    }
    
    public int getId() {
        return _id;
    }
    
    public LeagueMatchmakingQueue getQueue() {
        return _queue;
    }
    
    public List<LeagueSummoner> getPlayerTeam() {
        return _playerTeam;
    }
    
    public List<LeagueSummoner> getEnemyTeam() {
        return _enemyTeam;
    }
    
    public List<LeagueSummoner> getAllPlayers() {
        List<LeagueSummoner> players = new ArrayList<LeagueSummoner>(_playerTeam);
        players.addAll(_enemyTeam);
        return players;
    }
    
    public LeagueChampion getChampionSelectionForSummoner(LeagueSummoner summoner) {
        return _playerChampionSelections.get(summoner.getInternalName());
    }
    
    public ObserverCredentials getObserverCredentials() {
        return _observerCredentials;
    }

    public TeamType getPlayerTeamType() {
	return _playerTeamType;
    }

    public TeamType getEnemyTeamType() {
	return _enemyTeamType;
    }
    
    public List<LeagueChampion> getBannedChampionsForTeam(TeamType type) {
	return _bannedChampions.get(type);
    }
	    
}