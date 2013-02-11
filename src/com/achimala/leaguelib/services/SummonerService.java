package com.achimala.leaguelib.services;

import com.achimala.leaguelib.connection.*;
import com.achimala.leaguelib.models.LeagueSummoner;
import com.achimala.util.Callback;
import com.gvaneyck.rtmp.TypedObject;

public class SummonerService extends LeagueAbstractService {
    public SummonerService(LeagueConnection connection) {
        super(connection);
    }
    
    public String getServiceName() {
        return "summonerService";
    }
    
    public LeagueSummoner getSummonerByName(String name) throws LeagueServiceException {
        TypedObject obj = call("getSummonerByName", new Object[] { name });
        return new LeagueSummoner(obj);
    }
    
    public void getSummonerByName(String name, final Callback<LeagueSummoner> callback) {
        callAsynchronously("getSummonerByName", new Object[] { name }, new Callback<TypedObject>() {
            public void onCompletion(TypedObject obj) {
                callback.onCompletion(new LeagueSummoner(obj));
            }
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }
}