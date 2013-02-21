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
import com.achimala.leaguelib.errors.*;

public class LeagueSummonerLeagueStats {
    private class MiniSeries {
        private int _target, _losses, _wins;
        
        public MiniSeries(TypedObject obj) {
            _target = obj.getInt("target");
            _losses = obj.getInt("losses");
            _wins = obj.getInt("wins");
            // timeLeftToPlayMillis
        }
        
        public void setTarget(int target) {
            _target = target;
        }
        
        public void setLosses(int losses) {
            _losses = losses;
        }
        
        public void setWins(int wins) {
            _wins = wins;
        }
        
        public int getTarget() {
            return _target;
        }
        
        public int getLosses() {
            return _losses;
        }
        
        public int getWins() {
            return _wins;
        }
    }
    
    private LeagueMatchmakingQueue _queue;
    private String _leagueName;
    private int _previousDayLeaguePosition=-1, _wins=0, _losses=0, _leaguePoints=0;
    private LeagueRankedTier _tier;
    private LeagueRankedRank _rank;
    private boolean _inactive=false, _veteran=false, _hotStreak=false, _freshBlood=false;
    private MiniSeries _miniSeries;
    
    public LeagueSummonerLeagueStats() {
    }
    
    public LeagueSummonerLeagueStats(TypedObject obj) throws LeagueException {
        if(obj == null)
            throw new LeagueException(LeagueErrorCode.SUMMONER_NOT_IN_LEAGUE);
        
        _queue = LeagueMatchmakingQueue.valueOf(obj.getString("queue"));
        _leagueName = obj.getString("name");
        
        String requestor = obj.getString("requestorsName");
        
        TypedObject sumObj = null;
        for(Object o : obj.getArray("entries")) {
            if(((TypedObject)o).getString("playerOrTeamName").equalsIgnoreCase(requestor)) {
                sumObj = (TypedObject)o;
                break;
            }
        }
        
        if(sumObj == null)
            throw new LeagueException(LeagueErrorCode.SUMMONER_NOT_FOUND);
        
        _previousDayLeaguePosition = sumObj.getInt("previousDayLeaguePosition");
        _wins = sumObj.getInt("wins");
        _losses = sumObj.getInt("losses");
        _leaguePoints = sumObj.getInt("leaguePoints");
        _tier = LeagueRankedTier.valueOf(sumObj.getString("tier"));
        _rank = LeagueRankedRank.valueOf(sumObj.getString("rank"));
        
        _inactive = sumObj.getBool("inactive");
        _veteran = sumObj.getBool("veteran");
        _hotStreak = sumObj.getBool("hotStreak");
        _freshBlood = sumObj.getBool("freshBlood");
        
        TypedObject miniSeriesObj = sumObj.getTO("miniSeries");
        if(miniSeriesObj != null)
            _miniSeries = new MiniSeries(miniSeriesObj);
    }
    
    public int getApproximateElo() {
        double elo = 450;
        elo += _tier.getApproximateEloContribution();
        if(_tier == LeagueRankedTier.CHALLENGER)
            elo += LeagueRankedRank.V.getApproximateEloContribution();
        else
            elo += _rank.getApproximateEloContribution();
        elo += 0.5 * _leaguePoints;
        if(_miniSeries != null)
            elo += 20.0 * _miniSeries.getWins()/_miniSeries.getTarget();
        return (int)elo;
    }
    
    public void setMatchmakingQueue(LeagueMatchmakingQueue queue) {
        _queue = queue;
    }
    
    public void setLeagueName(String name) {
        _leagueName = name;
    }
    
    public void setPreviousDayLeaguePosition(int position)  {
        _previousDayLeaguePosition = position;
    }
    
    public void setWins(int wins) {
        _wins = wins;
    }
    
    public void setLosses(int losses) {
        _losses = losses;
    }
    
    public void setLeaguePoints(int points) {
        _leaguePoints = points;
    }
    
    public void setTier(LeagueRankedTier tier) {
        _tier = tier;
    }
    
    public void setRank(LeagueRankedRank rank) {
        _rank = rank;
    }
    
    public void setInactive(boolean inactive) {
        _inactive = inactive;
    }
    
    public void setVeteran(boolean veteran) {
        _veteran = veteran;
    }
    
    public void setHotStreak(boolean hotStreak) {
        _hotStreak = hotStreak;
    }
    
    public void setFreshBlood(boolean freshBlood) {
        _freshBlood = freshBlood;
    }
    
    public LeagueMatchmakingQueue getMatchmakingQueue() {
        return _queue;
    }
    
    public String getLeagueName() {
        return _leagueName;
    }
    
    public int getPreviousDayLeaguePosition()  {
        return _previousDayLeaguePosition;
    }
    
    public int getWins() {
        return _wins;
    }
    
    public int getLosses() {
        return _losses;
    }
    
    public int getLeaguePoints() {
        return _leaguePoints;
    }
    
    public LeagueRankedTier getTier() {
        return _tier;
    }
    
    public LeagueRankedRank getRank() {
        return _rank;
    }
    
    public boolean getInactive() {
        return _inactive;
    }
    
    public boolean getVeteran() {
        return _veteran;
    }
    
    public boolean getHotStreak() {
        return _hotStreak;
    }
    
    public boolean getFreshBlood() {
        return _freshBlood;
    }
    
    public MiniSeries getMiniSeries() {
        return _miniSeries;
    }
}