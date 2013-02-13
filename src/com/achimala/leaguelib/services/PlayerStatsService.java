package com.achimala.leaguelib.services;

import com.achimala.leaguelib.connection.*;
import com.achimala.leaguelib.models.*;
import com.achimala.leaguelib.errors.*;
import com.achimala.util.Callback;
import com.gvaneyck.rtmp.TypedObject;

public class PlayerStatsService extends LeagueAbstractService {
    private final String SUMMONERS_RIFT = "CLASSIC";
    
    public PlayerStatsService(LeagueConnection connection) {
        super(connection);
    }
    
    public String getServiceName() {
        return "playerStatsService";
    }
    
    public void fillRankedStats(LeagueSummoner summoner) throws LeagueException {
        TypedObject obj = call("getAggregatedStats", new Object[] { summoner.getAccountId(), SUMMONERS_RIFT, LeagueCompetitiveSeason.CURRENT.toString() });
        summoner.setRankedStats(new LeagueSummonerRankedStats(obj));
    }
    
    public void fillRankedStats(final LeagueSummoner summoner, final Callback<LeagueSummoner> callback) {
        callAsynchronously("getAggregatedStats", new Object[] { summoner.getAccountId(), SUMMONERS_RIFT, LeagueCompetitiveSeason.CURRENT.toString() }, new Callback<TypedObject>() {
            public void onCompletion(TypedObject obj) {
                summoner.setRankedStats(new LeagueSummonerRankedStats(obj));
                callback.onCompletion(summoner);
            }
            
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }
    
    // getRecentGames -> match history
}