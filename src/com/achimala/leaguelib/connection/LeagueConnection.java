package com.achimala.leaguelib.connection;

import com.gvaneyck.rtmp.LoLRTMPSClient;
import com.achimala.leaguelib.services.*;
import com.achimala.leaguelib.errors.*;
import java.io.IOException;

public class LeagueConnection {
    private LeagueServer _server=null;
    // In the future, this should be a client queue rather than a single client:
    private LoLRTMPSClient _rtmpClient=null;
    private SummonerService _summonerService=null;
    private LeaguesService _leaguesService=null;
    private PlayerStatsService _playerStatsService=null;
    private GameService _gameService=null;
    
    public LeagueConnection(LeagueServer server) {
        _server = server;
    }
    
    public void setCredentials(String username, String password, String clientVersion) {
        if(_rtmpClient != null) {
            if(_rtmpClient.isConnected())
                _rtmpClient.close();
        }
        _rtmpClient = new LoLRTMPSClient(_server.getServerCode(), clientVersion, username, password);
    }
    
    public void connect() throws LeagueException {
        if(_rtmpClient == null)
            throw new LeagueException(LeagueErrorCode.AUTHENTICATION_ERROR, "Missing authentication credentials for connection to server " + _server);
        try {
            if(_rtmpClient.isConnected()) {
                if(_rtmpClient.isLoggedIn())
                    return;
                else
                    _rtmpClient.login();
            } else
                _rtmpClient.connectAndLogin();
        } catch(IOException ex) {
            throw new LeagueException(LeagueErrorCode.NETWORK_ERROR, ex.getMessage());
        }
    }
    
    public boolean isConnected() {
        return (_rtmpClient == null || _rtmpClient.isLoggedIn());
    }
    
    public String toString() {
        return String.format("<LeagueConnection:%s (%s)>", _server.getServerCode(), isConnected() ? "Online" : "Offline");
    }
    
    public LoLRTMPSClient getInternalRTMPClient() {
        // This should eventually return a client off of the queue
        return _rtmpClient;
    }
    
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