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
    
    protected LeagueConnection getConnection() {
        return _connection;
    }
    
    protected TypedObject handleResult(TypedObject result) throws LeagueException {
        if(result.get("result").equals("_error")) {
            System.err.println(result);
            String reason = result.getExceptionMessage();
            throw new LeagueException(LeagueErrorCode.getErrorCodeForException(reason), reason);
        }
        return result.getTO("data");
    }
    
    protected TypedObject call(String method, Object arguments) throws LeagueException {
        return handleResult(_connection.invoke(getServiceName(), method, arguments));
        /*try {
            int id = _connection.getInternalRTMPSClient(0).invoke(getServiceName(), method, arguments);
            TypedObject result = _connection.getInternalRTMPSClient(0).getResult(id);
            return handleResult(result);
        } catch(IOException ex) {
            throw new LeagueException(LeagueErrorCode.NETWORK_ERROR, ex.getMessage());
        }*/
    }
    
    protected void callAsynchronously(String method, Object arguments, final Callback<TypedObject> callback) {
        _connection.invokeWithCallback(getServiceName(), method, arguments, new Callback<TypedObject>() {
            public void onCompletion(TypedObject obj) {
                try {
                    callback.onCompletion(handleResult(obj));
                } catch(LeagueException ex) {
                    callback.onError(ex);
                }
            }
            
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });

        /*try {
            _connection.getInternalRTMPSClient(0).invokeWithCallback(getServiceName(), method, arguments, new com.gvaneyck.rtmp.Callback() {
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
        }*/
    }
    
    public abstract String getServiceName();
    
    public String toString() {
        return String.format("<Service:%s>", getServiceName());
    }
}