package com.achimala.leaguelib.services;

import com.achimala.leaguelib.connection.*;
import com.achimala.leaguelib.models.*;
import com.achimala.leaguelib.errors.*;
import com.achimala.util.Callback;
import com.gvaneyck.rtmp.TypedObject;

public class SummonerService extends LeagueAbstractService {
    public SummonerService(LeagueConnection connection) {
        super(connection);
    }
    
    public String getServiceName() {
        return "summonerService";
    }
    
    public LeagueSummoner getSummonerByName(String name) throws LeagueException {
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
    
    public void fillPublicSummonerData(LeagueSummoner summoner) throws LeagueException {
        TypedObject obj = call("getAllPublicSummonerDataByAccount", new Object[] { summoner.getAccountId() });
        summoner.setProfileInfo(new LeagueSummonerProfileInfo(obj));
    }
    
    public void fillPublicSummonerData(final LeagueSummoner summoner, final Callback<LeagueSummoner> callback) {
        callAsynchronously("getAllPublicSummonerDataByAccount", new Object[] { summoner.getAccountId() }, new Callback<TypedObject>() {
            public void onCompletion(TypedObject obj) {
                summoner.setProfileInfo(new LeagueSummonerProfileInfo(obj));
                callback.onCompletion(summoner);
            }
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }
}