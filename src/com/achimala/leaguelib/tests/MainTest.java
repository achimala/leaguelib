package com.achimala.leaguelib.tests;

import com.achimala.leaguelib.connection.*;
import com.achimala.leaguelib.models.*;
import com.achimala.util.Callback;

public class MainTest {
    public static void main(String[] args) throws Exception {
        final LeagueConnection c = new LeagueConnection(LeagueServer.NORTH_AMERICA);
        c.setCredentials("anshuchimala2", "xxxxx", "3.01.asdf");
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
                System.out.println("getting all public data...");
                c.getSummonerService().fillPublicSummonerData(ladieslover, new Callback<LeagueSummoner>() {
                    public void onCompletion(LeagueSummoner ladieslover) {
                        System.out.println("S1: " + ladieslover.getRankedStats().getSeasonOneTier());
                        System.out.println("S2: " + ladieslover.getRankedStats().getSeasonTwoTier());
                    }
                    
                    public void onError(Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                });
            }
            public void onError(Exception ex) {
                System.out.println(ex.getMessage());
            }
        });
        System.out.println("Out here, waiting for it to finish");
        c.getInternalRTMPClient().join();
    }
}