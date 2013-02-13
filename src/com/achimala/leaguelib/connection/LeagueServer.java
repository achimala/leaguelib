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

package com.achimala.leaguelib.connection;

public enum LeagueServer {
    NORTH_AMERICA("NA"),
    EUROPE_WEST("EUW"),
    EUROPE_NORDIC_AND_EAST("EUNE"),
    BRAZIL("BR"),
    KOREA("KR");
    // Garena servers...
    // PublicBetaEnvironment
    
    private String _serverCode;

    private LeagueServer(String serverCode) {
        _serverCode = serverCode;
    }
    
    public String getServerCode() {
        return _serverCode;
    }
    
    public String toString() {
        return "<LeagueServer:" + _serverCode + ">";
    }
}