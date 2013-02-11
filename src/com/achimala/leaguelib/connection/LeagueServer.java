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