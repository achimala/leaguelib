package com.achimala.leaguelib.errors;

public class LeagueException extends Exception {
    LeagueErrorCode _errorCode=null;
    
    public LeagueException(LeagueErrorCode code) {
        this(code, null);
    }
    
    public LeagueException(LeagueErrorCode code, String message) {
        super(message);
        _errorCode = code;
    }
    
    public void setErrorCode(LeagueErrorCode code) {
        _errorCode = code;
    }
    
    public LeagueErrorCode getErrorCode() {
        return _errorCode;
    }
}