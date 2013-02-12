package com.achimala.leaguelib.services;

import com.achimala.leaguelib.connection.*;
import com.achimala.leaguelib.models.*;
import com.achimala.leaguelib.errors.*;
import com.achimala.util.Callback;
import com.gvaneyck.rtmp.TypedObject;

public class PlayerStatsService extends LeagueAbstractService {
    public PlayerStatsService(LeagueConnection connection) {
        super(connection);
    }
    
    public String getServiceName() {
        return "playerStatsService";
    }
    
    // getAggregatedStats -> champ data
    // getRecentGames -> match history
}