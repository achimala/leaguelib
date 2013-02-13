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

package com.achimala.leaguelib.tests;

import com.achimala.leaguelib.connection.*;
import com.achimala.leaguelib.models.*;
import com.achimala.util.Callback;
import java.util.Map;

public class MainTest {
    public static void main(String[] args) throws Exception {
        final LeagueConnection c = new LeagueConnection(LeagueServer.NORTH_AMERICA);
        c.setCredentials("anshuchimala2", args[0], "3.01.asdf");
        c.connect();
        
        c.getSummonerService().getSummonerByName("chdmwu", new Callback<LeagueSummoner>() {
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
                        System.out.println("    ~Elo: " + stats.getApproximateElo());
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