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

package com.achimala.leaguelib.connection;

import com.gvaneyck.rtmp.LoLRTMPSClient;
import com.achimala.util.Callback;
import com.gvaneyck.rtmp.TypedObject;
import com.achimala.leaguelib.services.*;
import com.achimala.leaguelib.errors.*;
import java.io.IOException;

public class LeagueConnection {
    private static final String NA_VERSION = "3.01.something";
    private static final String SECRET = "g9wvEPh5SQ";
    //holds user,pass,version for each client
    //eventually replace this by reading in list from .config
    private static final String[][] CLIENT_LIST = {{NA_VERSION, "lolteam0", SECRET}, {NA_VERSION, "lolteam2", SECRET}, {NA_VERSION, "lolteam3", SECRET}};
    private LeagueServer _server=null;
    private LoLRTMPSClient _rtmpClients[] = new LoLRTMPSClient[CLIENT_LIST.length];
    private SummonerService _summonerService=null;
    private LeaguesService _leaguesService=null;
    private PlayerStatsService _playerStatsService=null;
    private GameService _gameService=null;
    private LeagueException[] _connExceptions = new LeagueException[_rtmpClients.length];
    private int taskCounter = 0;
    
    public LeagueConnection(LeagueServer server) {
        _server = server;
    }

    public void login() {
        for(int i = 0; i < CLIENT_LIST.length; i++) {
            if(_rtmpClients[i] != null) {
                if(_rtmpClients[i].isConnected())
                    _rtmpClients[i].close();
            }
            //order of arfuments: servercode, version, user, pass
            _rtmpClients[i] = new LoLRTMPSClient(_server.getServerCode(), CLIENT_LIST[i][0], CLIENT_LIST[i][1], CLIENT_LIST[i][2]);
        }
    }
    
    
    /*public void setCredentials(String username, String password, String clientVersion) {
        if(_rtmpClients != null) {
            if(_rtmpClients.isConnected())
                _rtmpClients.close();
        }
        _rtmpClients = new LoLRTMPSClient(_server.getServerCode(), clientVersion, username, password);
    }*/

    public void connectAll() {
        for(int i = 0; i < _rtmpClients.length; i++) {
            try {
                connect(_rtmpClients[i]);
            }
            catch(LeagueException e) {
                _connExceptions[i] = e;
                continue;
            }
        }
    }

    public LeagueException[] getConnectionExceptions() {
        return _connExceptions;
    }

    public void connect(LoLRTMPSClient client) throws LeagueException {
        if(client == null)
            throw new LeagueException(LeagueErrorCode.AUTHENTICATION_ERROR, "Missing authentication credentials for connection to server " + _server);
        try {
            if(client.isConnected()) {
                if(client.isLoggedIn())
                    return;
                else
                    client.login();
            } else
                client.connectAndLogin();
        } catch(IOException ex) {
            throw new LeagueException(LeagueErrorCode.NETWORK_ERROR, ex.getMessage());
        }
    }
    
    /*public void connect() throws LeagueException {
        if(_rtmpClients == null)
            throw new LeagueException(LeagueErrorCode.AUTHENTICATION_ERROR, "Missing authentication credentials for connection to server " + _server);
        try {
            if(_rtmpClients.isConnected()) {
                if(_rtmpClients.isLoggedIn())
                    return;
                else
                    _rtmpClients.login();
            } else
                _rtmpClients.connectAndLogin();
        } catch(IOException ex) {
            throw new LeagueException(LeagueErrorCode.NETWORK_ERROR, ex.getMessage());
        }
    }*/
    
    /*public boolean isConnected() {
        return (_rtmpClient == null || _rtmpClient.isLoggedIn());
    }*/

    public boolean isAnyoneConnected() {
        for(LoLRTMPSClient c : _rtmpClients) {
            if(isConnected(c))
                return true;
        }
        return false;
    }

    public boolean isEveryoneConnected() {
        for(LoLRTMPSClient c : _rtmpClients) {
            if(!isConnected(c))
                return false;
        }
        return true;
    }

    //don't like this because requires isntance of lolrtmpsclient from outside class
    public boolean isConnected(LoLRTMPSClient client) {
        return (client == null || client.isLoggedIn());
    }

    public boolean isConnected(int clientIndex) {
        return isConnected(_rtmpClients[clientIndex]);
    }
    
    public String toString() {
        String s = "";
        for(LoLRTMPSClient c : _rtmpClients)
            s += String.format("<LeagueConnection:%s (%s)>\n", _server.getServerCode(), isConnected(c) ? "Online" : "Offline");
        return s;
    }

    public TypedObject invoke(String service, String method, Object arguments) throws LeagueException {
        try {
            LoLRTMPSClient c = getNextClient();
            System.out.println(c.getUser() + " performing " + method + " in " + service);
            return c.getResult(c.invoke(service, method, arguments));
        }
        catch(IOException e) {
            throw new LeagueException(LeagueErrorCode.NETWORK_ERROR, e.getMessage());
        }
    }

    public void invokeWithCallback(String service, String method, Object arguments, final Callback<TypedObject> callback, com.gvaneyck.rtmp.Callback cb) {
        try {
            LoLRTMPSClient c = getNextClient();
            System.out.println(c.getUser() + " performing " + method + " in " + service);
            c.invokeWithCallback(service, method, arguments, cb);
        }
        catch(IOException e) {
            callback.onError(e);
        }
    }

    private LoLRTMPSClient getNextClient() {
        return _rtmpClients[taskCounter++ % 3];
    }
    
    /*public LoLRTMPSClient getInternalRTMPSClient(int clientIndex) {
        // This should eventually return a client off of the queue
        return _rtmpClients[clientIndex];
    }

    public LoLRTMPSClient[] getAllInternalRTMPSClients() {
        return _rtmpClients;
    }*/
    
    //// Services
    
    public SummonerService getSummonerService() {
        if(_summonerService == null)
            _summonerService = new SummonerService(this);
        return _summonerService;
    }
    
    public LeaguesService getLeaguesService() {
        if(_leaguesService == null)
            _leaguesService = new LeaguesService(this);
        return _leaguesService;
    }
    
    public PlayerStatsService getPlayerStatsService() {
        if(_playerStatsService == null)
            _playerStatsService = new PlayerStatsService(this);
        return _playerStatsService;
    }
    
    public GameService getGameService() {
        if(_gameService == null)
            _gameService = new GameService(this);
        return _gameService;
    }
}