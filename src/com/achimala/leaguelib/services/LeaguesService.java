package com.achimala.leaguelib.services;

import com.achimala.leaguelib.connection.*;
import com.achimala.leaguelib.models.*;
import com.achimala.leaguelib.errors.*;
import com.achimala.util.Callback;
import com.gvaneyck.rtmp.TypedObject;

public class LeaguesService extends LeagueAbstractService {
    public LeaguesService(LeagueConnection connection) {
        super(connection);
    }
    
    public String getServiceName() {
        return "leaguesServiceProxy";
    }
    
    public void fillSoloQueueLeagueData(LeagueSummoner summoner) throws LeagueException {
        TypedObject obj = call("getLeagueForPlayer", new Object[] { summoner.getId(), LeagueMatchmakingQueue.RANKED_SOLO_5x5.toString() });
        summoner.setLeagueStats(new LeagueSummonerLeagueStats(obj));
    }
    
    public void fillSoloQueueLeagueData(final LeagueSummoner summoner, final Callback<LeagueSummoner> callback) {
        callAsynchronously("getLeagueForPlayer", new Object[] { summoner.getId(), LeagueMatchmakingQueue.RANKED_SOLO_5x5.toString() }, new Callback<TypedObject>() {
            public void onCompletion(TypedObject obj) {
                try {
                    summoner.setLeagueStats(new LeagueSummonerLeagueStats(obj));
                    callback.onCompletion(summoner);
                } catch(LeagueException ex) {
                    callback.onError(ex);
                }
            }
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }
}