package com.achimala.leaguelib.connection;

public enum LeagueServer {
    NorthAmerica("NA"),
    EuropeWest("EUW"),
    EuropeNordicAndEast("EUNE"),
    Brazil("BR"),
    Korea("KR");
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