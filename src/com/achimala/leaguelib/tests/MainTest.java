package com.achimala.leaguelib.tests;

import com.achimala.leaguelib.connection.*;
import com.achimala.leaguelib.models.*;
import com.achimala.util.Callback;

public class MainTest {
    public static void main(String[] args) throws Exception {
        LeagueConnection c = new LeagueConnection(LeagueServer.NORTH_AMERICA);
        c.setCredentials("anshuchimala2", "xxxxxxx", "3.01.asdf");
        c.connect();
        // LeagueSummoner ladieslover = c.getSummonerService().getSummonerByName("ladieslover");
        // System.out.println("ladieslover's");
        // System.out.println("   accountID: " + ladieslover.getAccountId());
        // System.out.println("  summonerID: " + ladieslover.getId());
        c.getSummonerService().getSummonerByName("ladieslover", new Callback<LeagueSummoner>() {
            public void onCompletion(LeagueSummoner ladieslover) {
                System.out.println("ladieslover's");
                System.out.println("   accountID: " + ladieslover.getAccountId());
                System.out.println("  summonerID: " + ladieslover.getId());
            }
            public void onError(Exception ex) {
                System.out.println(ex.getMessage());
            }
        });
        System.out.println("Out here, waiting for it to finish");
        c.getInternalRTMPClient().join();
    }
}