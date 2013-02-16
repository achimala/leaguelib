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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ListIterator;

public class LeagueAccountQueue {
    private ArrayList<LeagueAccount> _internalQueue = new ArrayList<LeagueAccount>();
    private LeagueServer _server;
    private int _index = 0;
    
    /**
     * Adds an account to the queue.
     * (Thread-safe)
     */
    public synchronized void addAccount(LeagueAccount account) throws LeagueException {
        if(!account.isConnected())
            account.connect();
        _internalQueue.add(account);
    }
    
    /**
     * Gets the next account off the queue that is connected.
     * Returns null if no connected accounts are available.
     * Unlike traditional queues, not destructive.
     */
    public synchronized LeagueAccount nextAccount() {
        if(_internalQueue.size() == 0)
            return null;
        for(int i = 0; i < _internalQueue.size(); i++) {
            _index = (_index + 1) % _internalQueue.size();
            LeagueAccount account = _internalQueue.get(_index);
            if(account.isConnected())
                return account;
        }
        // no accounts are connected
        return null;
    }
    
    /**
     * Returns a list view of all the accounts in the queue.
     */
    public List<LeagueAccount> getAllAccounts() {
        return _internalQueue;
    }
    
    /**
    * Iterates through accounts in the queues and connects any that are disconnected.
    * In a Play! app or similar, don't call this as it connects each account synchronously
    * Instead, iterate over getAllAccounts() and call connect() on each account in a worker thread
    * and have your scheduler connect them in parallel.
    */
    public synchronized Map<LeagueAccount, LeagueException> connectAll() {
        Map<LeagueAccount, LeagueException> exceptions = new HashMap<LeagueAccount, LeagueException>();
        for(LeagueAccount account : _internalQueue) {
            try {
                if(!account.isConnected())
                    account.connect();
            } catch(LeagueException ex) {
                exceptions.put(account, ex);
            }
        }
        return exceptions.size() > 0 ? exceptions : null;
    }
    
    /**
     * Returns true iff there exists an account that is connected.
     */
    public synchronized boolean isAnyAccountConnected() {
        for(LeagueAccount account : _internalQueue) {
            if(account.isConnected())
                return true;
        }
        return false;
    }
    
    /**
     * Returns true iff every account is connected.
     */
    public synchronized boolean isEveryAccountConnected() {
        for(LeagueAccount account : _internalQueue) {
            if(!account.isConnected())
                return false;
        }
        return true;
    }
}