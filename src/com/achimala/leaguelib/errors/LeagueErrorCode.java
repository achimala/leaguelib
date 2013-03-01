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

package com.achimala.leaguelib.errors;

public enum LeagueErrorCode {
    UNKNOWN,
    UNSUPPORTED_SERVER,
    NETWORK_ERROR,
    RTMP_ERROR,
    AUTHENTICATION_ERROR,
    SUMMONER_NOT_FOUND,
    SUMMONER_NOT_IN_LEAGUE,
    ACTIVE_GAME_NOT_FOUND("com.riotgames.platform.game.GameNotFoundException"),
    ACTIVE_GAME_NOT_SPECTATABLE("com.riotgames.platform.game.GameObserverModeNotEnabledException");
    
    private String _exceptionString;
    
    private LeagueErrorCode() { }
    
    private LeagueErrorCode(String exceptionString) {
        _exceptionString = exceptionString;
    }
    
    public String getExceptionString() {
        return _exceptionString;
    }
    
    public static LeagueErrorCode getErrorCodeForException(String exceptionString) {
        for(LeagueErrorCode code : LeagueErrorCode.values())
            if(code.getExceptionString() != null && code.getExceptionString().equals(exceptionString))
                return code;
        return RTMP_ERROR;
    }
}