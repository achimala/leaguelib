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

import com.achimala.leaguelib.errors.*;
import com.achimala.util.Callback;
import com.gvaneyck.rtmp.LoLRTMPSClient;
import com.gvaneyck.rtmp.TypedObject;

import java.io.IOException;

public class LeagueAccount {
    private LeagueServer _server;
    private String _clientVersion, _username, _password;
    LoLRTMPSClient _internalClient;
    
    /**
     * Creates a LeagueAccount, which is used to authenticate API calls to the League of Legends RTMP API.
     */
    public LeagueAccount(LeagueServer server, String clientVersion, String username, String password) {
        _server = server;
        _clientVersion = clientVersion;
        _username = username;
        _password = password;
    }
    
    private void setupClient() throws LeagueException {
        if(_server == null || _clientVersion == null || _username == null || _password == null || _username.length() <= 0 || _password.length() <= 0)
            throw new LeagueException(LeagueErrorCode.AUTHENTICATION_ERROR, "Missing credentials");
        if(_internalClient == null)
            _internalClient = new LoLRTMPSClient(_server.getServerCode(), _clientVersion, _username, _password);
    }
    
    /**
     * Returns true if this account is connected to the League of Legends RTMP server, or false otherwise.
     */
    public boolean isConnected() {
        return _internalClient != null && _internalClient.isConnected() && _internalClient.isLoggedIn();
    }
    
    /**
     * Attempts to connect this account to the League of Legends RTMP server.
     * Upon failure, throws a LeagueException
     * Connects synchronously. Upon returning, guarantees account is valid and connected.
     * If the account is already connected, has no effect.
     */
    public void connect() throws LeagueException {
        try {
            if(_internalClient == null)
                setupClient();
            if(_internalClient.isConnected()) {
                if(_internalClient.isLoggedIn())
                    return;
                else
                    _internalClient.login();
            } else
                _internalClient.connectAndLogin();
        } catch(IOException e) {
            throw new LeagueException(LeagueErrorCode.NETWORK_ERROR, e.getMessage());
        }
    }
    
    /**
     * Cleanly disconnects from the League of Legends RTMP server.
     * No effect if the account is not connected.
     * You should always remember to call this, because the LoL RTMP service expects a logout message.
     * Otherwise it will be in an inconsistent state until the heartbeat timeout kicks the user.
     */
    public void close() {
        if(!isConnected())
            return;
        _internalClient.close();
    }
    
    /**
     * Invokes an API call on the RTMP server.
     * Returns the raw packet data retrieved from the API call.
     * You should never have to call this method directly; create your accounts and create a LeagueConnection.
     * Then push your accounts onto the LeagueConnection's account queue.
     * You can then use LeagueConnection's invoke method or (preferably) use a League service abstraction.
     */
    public TypedObject invoke(String service, String method, Object arguments) throws LeagueException {
        if(!isConnected())
            throw new LeagueException(LeagueErrorCode.RTMP_ERROR, toString() + " is unable to connect to RTMP server");
        try {
            // System.out.println(_username + " performing " + method + " in " + service);
            return _internalClient.getResult(_internalClient.invoke(service, method, arguments));
        } catch(IOException e) {
            throw new LeagueException(LeagueErrorCode.NETWORK_ERROR, e.getMessage());
        }
    }
    
    /**
     * Same as invoke() but happens in a background thread, asynchronously. Returns immediately.
     * Suppresses exceptions and calls onError of the provided callback instead.
     * You shouldn't call this directly, but create a wrapping LeagueConnection and use its invokeWithCallback.
     */
    public void invokeWithCallback(String service, String method, Object arguments, final Callback<TypedObject> callback) {
        try {
            // System.out.println(_username + " performing " + method + " in " + service);
            _internalClient.invokeWithCallback(service, method, arguments, new com.gvaneyck.rtmp.Callback() {
                public void callback(TypedObject result) {
                    callback.onCompletion(result);
                }
            });
        } catch(IOException e) {
            callback.onError(e);
        }
    }
    
    public void setServer(LeagueServer server) {
        _server = server;
    }
    
    public void setClientVersion(String clientVersion) {
        _clientVersion = clientVersion;
    }
    
    public void setUsername(String username) {
        _username = username;
    }
    
    public void setPassword(String password) {
        _password = password;
    }
    
    public LeagueServer getServer() {
        return _server;
    }
    
    public String getClientVersion() {
        return _clientVersion;
    }
    
    public String getUsername() {
        return _username;
    }
    
    public String getPassword() {
        return _password;
    }
    
    public String toString() {
        return String.format("<LeagueAccount: %s on %s>", _username, _server.getServerCode());
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof LeagueAccount))
            return false;
        LeagueAccount other = (LeagueAccount)o;
        return other.getServer() == _server && other.getUsername().equals(_username) && other.getPassword().equals(_password);
    }
}