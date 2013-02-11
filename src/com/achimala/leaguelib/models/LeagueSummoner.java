package com.achimala.leaguelib.models;

import com.gvaneyck.rtmp.TypedObject;

public class LeagueSummoner {
    private int _id=-1, _accountId=-1;
    private String _name=null, _internalName=null;
    
    public LeagueSummoner() {
    }
    
    public LeagueSummoner(int id, String name) {
        _id = id;
        _name = name;
        _accountId = -1;
        _internalName = null;
    }
    
    public LeagueSummoner(TypedObject obj) {
        obj = obj.getTO("body");
        _id = obj.getInt("summonerId");
        _accountId = obj.getInt("acctId");
        _name = obj.getString("name");
        _internalName = obj.getString("internalName");
    }
    
    public void setId(int id) {
        _id = id;
    }
    
    public void setAccountId(int id) {
        _accountId = id;
    }
    
    public void setName(String name) {
        _name = name;
    }
    
    public void setInternalName(String name) {
        _internalName = name;
    }
    
    public int getId() {
        return _id;
    }
    
    public int getAccountId() {
        return _accountId;
    }
    
    public String getName() {
        return _name;
    }
    
    public String getInternalName() {
        return _internalName;
    }
}