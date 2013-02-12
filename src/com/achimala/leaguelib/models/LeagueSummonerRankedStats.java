package com.achimala.leaguelib.models;

import com.gvaneyck.rtmp.TypedObject;

public class LeagueSummonerRankedStats {
    private LeagueRankedTier _seasonOneTier=null, _seasonTwoTier=null;
    // TODO: Runes and masteries...
    
    public LeagueSummonerRankedStats() {
    }
    
    public LeagueSummonerRankedStats(TypedObject obj) {
        obj = obj.getTO("body").getTO("summoner");
        _seasonOneTier = LeagueRankedTier.valueOf(obj.getString("seasonOneTier"));
        _seasonTwoTier = LeagueRankedTier.valueOf(obj.getString("seasonTwoTier"));
    }
    
    public void setSeasonOneTier(LeagueRankedTier tier) {
        _seasonOneTier = tier;
    }
    
    public void setSeasonTwoTier(LeagueRankedTier tier) {
        _seasonTwoTier = tier;
    }
    
    public LeagueRankedTier getSeasonOneTier() {
        return _seasonOneTier;
    }
    
    public LeagueRankedTier getSeasonTwoTier() {
        return _seasonTwoTier;
    }
}