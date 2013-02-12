package com.achimala.leaguelib.services;

import com.gvaneyck.rtmp.TypedObject;
import com.achimala.leaguelib.connection.*;
import com.achimala.leaguelib.errors.*;
import com.achimala.util.Callback;
import java.io.IOException;

public abstract class LeagueAbstractService {
    protected LeagueConnection _connection = null;
    
    public LeagueAbstractService(LeagueConnection connection) {
        _connection = connection;
    }
    
    private TypedObject handleResult(TypedObject result) throws LeagueException {
        if(result.get("result").equals("_error")) {
            System.err.println(result);
            throw new LeagueException(LeagueErrorCode.RTMP_ERROR, _connection.getInternalRTMPClient().getErrorMessage(result));
        }
        return result.getTO("data");
    }
    
    protected TypedObject call(String method, Object arguments) throws LeagueException {
        try {
            int id = _connection.getInternalRTMPClient().invoke(getServiceName(), method, arguments);
            TypedObject result = _connection.getInternalRTMPClient().getResult(id);
            return handleResult(result);
        } catch(IOException ex) {
            throw new LeagueException(LeagueErrorCode.NETWORK_ERROR, ex.getMessage());
        }
    }
    
    protected void callAsynchronously(String method, Object arguments, final Callback<TypedObject> callback) {
        try {
            _connection.getInternalRTMPClient().invokeWithCallback(getServiceName(), method, arguments, new com.gvaneyck.rtmp.Callback() {
                public void callback(TypedObject result) {
                    try {
                        callback.onCompletion(handleResult(result));
                    } catch(LeagueException ex) {
                        callback.onError(ex);
                    }
                }
            });
        } catch(IOException ex) {
            callback.onError(ex);
        }
    }
    
    public abstract String getServiceName();
    
    public String toString() {
        return String.format("<Service:%s>", getServiceName());
    }
}