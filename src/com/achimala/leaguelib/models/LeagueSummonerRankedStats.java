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
import java.util.HashMap;
import java.util.Map;

public class LeagueSummonerRankedStats {
    private HashMap<Integer, Map<LeagueRankedStatType, Integer>> _stats;
    
    public LeagueSummonerRankedStats() {
    }
    
    public LeagueSummonerRankedStats(TypedObject obj) {
        _stats = new HashMap<Integer, Map<LeagueRankedStatType, Integer>>();
        for(Object o : obj.getArray("lifetimeStatistics")) {
            TypedObject to = (TypedObject)o;
            int champId = to.getInt("championId");
            LeagueRankedStatType type = LeagueRankedStatType.valueOf(to.getString("statType"));
            if(!_stats.containsKey(champId))
                _stats.put(champId, new HashMap<LeagueRankedStatType, Integer>());
            _stats.get(champId).put(type, to.getInt("value"));
        }
    }
    
    public Map<LeagueRankedStatType, Integer> getAllStatsForChampion(LeagueChampion champion) {
        return _stats.get(champion.getId());
    }
    
    public Map<LeagueRankedStatType, Integer> getAllPlayerStats() {
        return _stats.get(0);
    }
    
    public int getStatForChampion(LeagueChampion champion, LeagueRankedStatType statType) {
        return _stats.get(champion.getId()).get(statType);
    }
    
    public int getPlayerStat(LeagueRankedStatType statType) {
        return _stats.get(0).get(statType);
    }
}