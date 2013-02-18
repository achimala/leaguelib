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

import com.achimala.util.Callback;
import com.gvaneyck.rtmp.TypedObject;
import com.achimala.leaguelib.services.*;
import com.achimala.leaguelib.errors.*;
import java.io.IOException;

public class LeagueConnection {
    private SummonerService _summonerService;
    private LeaguesService _leaguesService;
    private PlayerStatsService _playerStatsService;
    private GameService _gameService;
    private LeagueServer _server;
    
    private LeagueAccountQueue _accountQueue;
    
    public LeagueConnection() {
        this(null);
    }
    
    public LeagueConnection(LeagueServer server) {
        _server = server;
        _accountQueue = new LeagueAccountQueue();
    }
    
    public String toString() {
        return String.format("<LeagueConnection (%d accounts)>", _accountQueue.getAllAccounts().size());
    }
    
    public void setAccountQueue(LeagueAccountQueue queue) {
        _accountQueue = queue;
    }
    
    public void setServer(LeagueServer server) {
        _server = server;
    }
    
    public LeagueAccountQueue getAccountQueue() {
        return _accountQueue;
    }
    
    public LeagueServer getServer() {
        return _server;
    }
    
    //// RTMP
    
    private LeagueAccount nextValidAccount() throws LeagueException {
        LeagueAccount account = _accountQueue.nextAccount();
        if(account == null)
            throw new LeagueException(LeagueErrorCode.RTMP_ERROR, toString() + " has no connected account");
        return account;
    }
    
    /**
     * Performs an API call on the League of Legends RTMP server.
     * Returns the raw packet data from the API call.
     * The API call will go through whichever account the account queue chooses.
     * You should probably not use this method; rather, use one of the services.
     */
    public TypedObject invoke(String service, String method, Object arguments) throws LeagueException {
        return nextValidAccount().invoke(service, method, arguments);
    }
    
    /**
     * Performs an API call on the League of Legends RTMP server through whichever account the account queue chooses.
     * Same as invoke() but takes place asynchronously on a background thread.
     */
    public void invokeWithCallback(String service, String method, Object arguments, final Callback<TypedObject> callback) {
        try {
            nextValidAccount().invokeWithCallback(service, method, arguments, callback);
        } catch(LeagueException ex) {
            callback.onError(ex);
        }
    }
    
    //// Services
    
    /**
     * Represents `summonerService` on the League of Legends RTMP API.
     * This service allows you to interact with Summoners, including retrieving their profile information and other data.
     * In the context of LeagueLib, enables you to get LeagueSummoner objects for summoners you are interested in.
     */
    public SummonerService getSummonerService() {
        if(_summonerService == null)
            _summonerService = new SummonerService(this);
        return _summonerService;
    }
    
    /**
     * Represents `leaguesServiceProxy` on the League of Legends RTMP API.
     * This service allows you to interact with the new leagues ladder ranking system.
     * You can retrieve league information such as league names, league points, division, tier, rank, etc.
     * In the context of LeagueLib, populates a LeagueSummoner object with leagues information.
     */
    public LeaguesService getLeaguesService() {
        if(_leaguesService == null)
            _leaguesService = new LeaguesService(this);
        return _leaguesService;
    }
    
    /**
     * Represents `playerStatsService` on the League of Legends RTMP API.
     * This service allows you to interact with player ranked statistics (and some normal game statistics).
     * In the context of LeagueLib, populates a LeagueSummoner object with ranked stats information, etc.
     */
    public PlayerStatsService getPlayerStatsService() {
        if(_playerStatsService == null)
            _playerStatsService = new PlayerStatsService(this);
        return _playerStatsService;
    }
    
    /**
     * Represents `gameService` on the League of Legends RTMP API.
     * This service allows you to retrieve real-time information about ongoing games.
     * In the context of LeagueLib, populates a LeagueSummoner object with active game data,
     * such as who they're in game with, who they're playing, etc.
     */
    public GameService getGameService() {
        if(_gameService == null)
            _gameService = new GameService(this);
        return _gameService;
    }
}