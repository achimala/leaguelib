package com.achimala.leaguelib.tests;

import com.achimala.leaguelib.connection.*;
import com.achimala.leaguelib.models.*;
import com.achimala.util.Callback;
import java.util.Map;

public class MainTest {
    public static void main(String[] args) throws Exception {
        final LeagueConnection c = new LeagueConnection(LeagueServer.NORTH_AMERICA);
        c.setCredentials("anshuchimala2", "dogmeat1", "3.01.asdf");
        c.connect();
        
        c.getSummonerService().getSummonerByName("GavinVS", new Callback<LeagueSummoner>() {
            public void onCompletion(LeagueSummoner summoner) {
                System.out.println(summoner.getName() + ":");
                System.out.println("    accountID:  " + summoner.getAccountId());
                System.out.println("    summonerID: " + summoner.getId());
                
                System.out.println("Getting profile data...");
                c.getSummonerService().fillPublicSummonerData(summoner, new Callback<LeagueSummoner>() {
                    public void onCompletion(LeagueSummoner summoner) {
                        System.out.println("Profile:");
                        System.out.println("    S1: " + summoner.getProfileInfo().getSeasonOneTier());
                        System.out.println("    S2: " + summoner.getProfileInfo().getSeasonTwoTier());
                        System.out.println();
                        System.out.flush();
                    }
                    
                    public void onError(Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                });
                
                System.out.println("Getting leagues data...");
                c.getLeaguesService().fillSoloQueueLeagueData(summoner, new Callback<LeagueSummoner>() {
                    public void onCompletion(LeagueSummoner summoner) {
                        LeagueSummonerLeagueStats stats = summoner.getLeagueStats();
                        System.out.println("League:");
                        System.out.println("    Name: " + stats.getLeagueName());
                        System.out.println("    Tier: " + stats.getTier());
                        System.out.println("    Rank: " + stats.getRank());
                        System.out.println("    Wins: " + stats.getWins());
                        System.out.println();
                        System.out.flush();
                    }
                    
                    public void onError(Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                });
                
                System.out.println("Getting champ data...");
                c.getPlayerStatsService().fillRankedStats(summoner, new Callback<LeagueSummoner>() {
                    public void onCompletion(LeagueSummoner summoner) {
                        LeagueChampion champ = LeagueChampion.getChampionWithName("Anivia");
                        Map<LeagueRankedStatType, Integer> stats = summoner.getRankedStats().getAllStatsForChampion(champ);
                        System.out.println("All stats for " + champ + ":");
                        for(LeagueRankedStatType type : LeagueRankedStatType.values())
                            System.out.println("    " + type + " = " + stats.get(type));
                        System.out.println();
                        System.out.flush();
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
        System.out.println("Client joined, terminating");
    }
}