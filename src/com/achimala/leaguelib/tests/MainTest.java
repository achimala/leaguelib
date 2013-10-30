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
import com.achimala.leaguelib.errors.*;
import com.achimala.util.Callback;
import java.util.Map;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

// This tests pretty much everything. It downloads as much information as it can about a given summoner and displays it.
// NOTE: You must pass in the password of the account(s) being used as a command line argument, so if someone pulls
// this code and tries to run it, it's not going to work correctly out of the box.
// If you're not one of the developers of this project, you'll want to change the account usernames and passwords below
// to your own personal test accounts. (And change the summoner's name if you want).
public class MainTest {
    private static int count = 0;
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition done = lock.newCondition();
    
    private static void incrementCount() {
        lock.lock();
        count++;
        // System.out.println("+ count = " + count);
        lock.unlock();
    }
    
    private static void decrementCount() {
        lock.lock();
        count--;
        if(count == 0)
            done.signal();
        // System.out.println("- count = " + count);
        lock.unlock();
    }
    
    public static void main(String[] args) throws Exception {
        final LeagueConnection c = new LeagueConnection(LeagueServer.NORTH_AMERICA);
        c.getAccountQueue().addAccount(new LeagueAccount(LeagueServer.NORTH_AMERICA, "3.13.xx", "lolteam0debug", args[0]));
        final String SUMMONER_TO_LOOK_UP = "the breadmaker";
        
        Map<LeagueAccount, LeagueException> exceptions = c.getAccountQueue().connectAll();
        if(exceptions != null) {
            for(LeagueAccount account : exceptions.keySet())
                System.out.println(account + " error: " + exceptions.get(account));
            return;
        }
        
        lock.lock();
        incrementCount();
        c.getSummonerService().getSummonerByName(SUMMONER_TO_LOOK_UP, new Callback<LeagueSummoner>() {
            public void onCompletion(LeagueSummoner summoner) {
                lock.lock();
                
                System.out.println(summoner.getName() + ":");
                System.out.println("    accountID:  " + summoner.getAccountId());
                System.out.println("    summonerID: " + summoner.getId());
                
                incrementCount();
                System.out.println("Getting profile data...");
                c.getSummonerService().fillPublicSummonerData(summoner, new Callback<LeagueSummoner>() {
                    public void onCompletion(LeagueSummoner summoner) {
                        lock.lock();
                        System.out.println("Profile:");
                        System.out.println("    Prev Highest Tier: " + summoner.getProfileInfo().getPreviousSeasonHighestTier());
                        System.out.println();
                        System.out.flush();
                        decrementCount();
                        lock.unlock();
                    }
                    
                    public void onError(Exception ex) {
                        lock.lock();
                        System.out.println(ex.getMessage());
                        decrementCount();
                        lock.unlock();
                    }
                });
                
                incrementCount();
                System.out.println("Getting leagues data...");
                c.getLeaguesService().fillSoloQueueLeagueData(summoner, new Callback<LeagueSummoner>() {
                    public void onCompletion(LeagueSummoner summoner) {
                        lock.lock();
                        LeagueSummonerLeagueStats stats = summoner.getLeagueStats();
                        if(stats != null) {
                            System.out.println("League:");
                            System.out.println("    Name: " + stats.getLeagueName());
                            System.out.println("    Tier: " + stats.getTier());
                            System.out.println("    Rank: " + stats.getRank());
                            System.out.println("    Wins: " + stats.getWins());
                            System.out.println("    ~Elo: " + stats.getApproximateElo());
                        } else {
                            System.out.println("NOT IN LEAGUE");
                        }
                        System.out.println();
                        System.out.flush();
                        decrementCount();
                        lock.unlock();
                    }
                    
                    public void onError(Exception ex) {
                        lock.lock();
                        System.out.println(ex.getMessage());
                        decrementCount();
                        lock.unlock();
                    }
                });
                
                incrementCount();
                System.out.println("Getting champ data...");
                c.getPlayerStatsService().fillRankedStats(summoner, new Callback<LeagueSummoner>() {
                    public void onCompletion(LeagueSummoner summoner) {
                        lock.lock();
                        for(LeagueChampion champ : summoner.getRankedStats().getAllPlayedChampions())
                            System.out.println("Has played " + champ.getName());
                        
                        LeagueChampion champ = LeagueChampion.getChampionWithName("Anivia");
                        Map<LeagueRankedStatType, Integer> stats = summoner.getRankedStats().getAllStatsForChampion(champ);
                        if(stats == null) {
                            System.out.println("No stats for " + champ);
                        } else {
                            System.out.println("All stats for " + champ + ":");
                            for(LeagueRankedStatType type : LeagueRankedStatType.values())
                                System.out.println("    " + type + " = " + stats.get(type));
                            System.out.println();
                        }
                        System.out.flush();
                        decrementCount();
                        lock.unlock();
                    }
                    
                    public void onError(Exception ex) {
                        lock.lock();
                        System.out.println(ex.getMessage());
                        decrementCount();
                        lock.unlock();
                    }
                });
                
                incrementCount();
                System.out.println("Getting game data...");
                c.getGameService().fillActiveGameData(summoner, new Callback<LeagueSummoner>() {
                    public void onCompletion(LeagueSummoner summoner) {
                        lock.lock();
                        if(summoner.getActiveGame() != null) {
			    LeagueGame game = summoner.getActiveGame();
                            System.out.println("PLAYER TEAM (" + game.getPlayerTeamType() + "):");
                            for(LeagueSummoner sum : summoner.getActiveGame().getPlayerTeam())
                                System.out.println("    " + sum);
                            System.out.println("ENEMY TEAM (" + game.getEnemyTeamType() + "):");
                            for(LeagueSummoner sum : summoner.getActiveGame().getEnemyTeam())
                                System.out.println("    " + sum);
			    System.out.println("PLAYER TEAM BANS:");
			    for(LeagueChampion champion : game.getBannedChampionsForTeam(game.getPlayerTeamType()))
				System.out.println("    " + champion.getName());
			    System.out.println("ENEMY TEAM BANS:");
			    for(LeagueChampion champion : game.getBannedChampionsForTeam(game.getEnemyTeamType()))
				System.out.println("    " + champion.getName());
                        } else {
                            System.out.println("NOT IN GAME");
                        }
                        System.out.println();
                        System.out.flush();
                        decrementCount();
                        lock.unlock();
                    }
                    
                    public void onError(Exception ex) {
                        lock.lock();
                        if(ex instanceof LeagueException) {
                            System.out.println(((LeagueException)ex).getErrorCode());
                        } else {
                            System.out.println(ex.getMessage());
                        }
                        decrementCount();
                        lock.unlock();
                    }
                });
                
                decrementCount();
                lock.unlock();
            }
            public void onError(Exception ex) {
                lock.lock();
                ex.printStackTrace();
                decrementCount();
                lock.unlock();
            }
        });

        System.out.println("Out here, waiting for it to finish");
        done.await();
        // c.getInternalRTMPClient().join();
        System.out.println("Client joined, terminating");
        lock.unlock();
    }
}
