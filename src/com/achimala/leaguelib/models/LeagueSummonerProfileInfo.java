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

public class LeagueSummonerProfileInfo {
    private LeagueRankedTier _seasonOneTier, _seasonTwoTier;
    // TODO: Runes and masteries...
    
    public LeagueSummonerProfileInfo() {
    }
    
    public LeagueSummonerProfileInfo(TypedObject obj) {
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
