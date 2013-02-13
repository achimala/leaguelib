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
        return new LeagueSummoner(obj.getTO("body"));
    }
    
    public void getSummonerByName(String name, final Callback<LeagueSummoner> callback) {
        callAsynchronously("getSummonerByName", new Object[] { name }, new Callback<TypedObject>() {
            public void onCompletion(TypedObject obj) {
                callback.onCompletion(new LeagueSummoner(obj.getTO("body")));
            }
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }
    
    public void fillPublicSummonerData(LeagueSummoner summoner) throws LeagueException {
        TypedObject obj = call("getAllPublicSummonerDataByAccount", new Object[] { summoner.getAccountId() });
        summoner.setProfileInfo(new LeagueSummonerProfileInfo(obj.getTO("body").getTO("summoner")));
    }
    
    public void fillPublicSummonerData(final LeagueSummoner summoner, final Callback<LeagueSummoner> callback) {
        callAsynchronously("getAllPublicSummonerDataByAccount", new Object[] { summoner.getAccountId() }, new Callback<TypedObject>() {
            public void onCompletion(TypedObject obj) {
                summoner.setProfileInfo(new LeagueSummonerProfileInfo(obj.getTO("body").getTO("summoner")));
                callback.onCompletion(summoner);
            }
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }
}