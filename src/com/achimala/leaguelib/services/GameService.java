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

public class GameService extends LeagueAbstractService {
    public GameService(LeagueConnection connection) {
        super(connection);
    }
    
    public String getServiceName() {
        return "gameService";
    }
    
    // FIXME: Not sure if this is the right way to handle this
    protected TypedObject handleResult(TypedObject result) throws LeagueException {
        if(result.get("result").equals("_error")) {
            String reason = result.getExceptionMessage();
            LeagueErrorCode code = LeagueErrorCode.getErrorCodeForException(reason);
            if(code == LeagueErrorCode.ACTIVE_GAME_NOT_FOUND || code == LeagueErrorCode.ACTIVE_GAME_NOT_SPECTATABLE)
                return null;
        }
        return super.handleResult(result);
    }
    
    private void createAndSetGame(LeagueSummoner summoner, TypedObject obj) {
        if(obj == null || obj.getTO("body") == null)
            summoner.setActiveGame(null);
        else {
            LeagueGame game = new LeagueGame(obj.getTO("body"), summoner);
            summoner.setActiveGame(game);
        }
    }
    
    public void fillActiveGameData(LeagueSummoner summoner) throws LeagueException {
        TypedObject obj = call("retrieveInProgressSpectatorGameInfo", new Object[] { summoner.getInternalName() });
        createAndSetGame(summoner, obj);
    }
    
    public void fillActiveGameData(final LeagueSummoner summoner, final Callback<LeagueSummoner> callback) {
        callAsynchronously("retrieveInProgressSpectatorGameInfo", new Object[] { summoner.getInternalName() }, new Callback<TypedObject>() {
            public void onCompletion(TypedObject obj) {
                createAndSetGame(summoner, obj);
                callback.onCompletion(summoner);
            }
            
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }
}