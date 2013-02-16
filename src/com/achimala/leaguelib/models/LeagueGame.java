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

public class LeagueGame {
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
    private Map<Integer, LeagueChampion> _playerChampionSelections;
    private Map<String, LeagueSummoner> _summoners;
    private ObserverCredentials _observerCredentials;
    
    public LeagueGame() {
    }
    
    private void fillListWithPlayers(List<LeagueSummoner> list, Object[] team, LeagueSummoner primarySummoner) {
        for(Object o : team) {
            TypedObject to = (TypedObject)o;
            LeagueSummoner sum = new LeagueSummoner(to, true);
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
        
        _playerChampionSelections = new HashMap<Integer, LeagueChampion>();
        for(Object o : obj.getArray("playerChampionSelections")) {
            TypedObject to = (TypedObject)o;
            LeagueSummoner sum = _summoners.get(to.getString("summonerInternalName"));
            _playerChampionSelections.put(sum.getId(), LeagueChampion.getChampionWithId(to.getInt("championId")));
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
        return _playerChampionSelections.get(summoner.getId());
    }
    
    public ObserverCredentials getObserverCredentials() {
        return _observerCredentials;
    }
}