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

package com.achimala.leaguelib.models;

import com.achimala.leaguelib.models.LeagueChampion;
import com.achimala.util.BidirectionalMap;
import java.util.HashMap;

public class LeagueChampion {
    private static BidirectionalMap<Integer, String> _modelMap;
    private static HashMap<Integer, LeagueChampion> _champMap;
    
    static {
        _modelMap = new BidirectionalMap<Integer, String>();
        _modelMap.put(  0,  null); // represents a catch-all champion for stats
        _modelMap.put(  1, "Annie");
        _modelMap.put(  2, "Olaf");
        _modelMap.put(  3, "Galio");
        _modelMap.put(  4, "Twisted Fate");
        _modelMap.put(  5, "Xin Zhao");
        _modelMap.put(  6, "Urgot");
        _modelMap.put(  7, "LeBlanc");
        _modelMap.put(  8, "Vladimir");
        _modelMap.put(  9, "Fiddlesticks");
        _modelMap.put( 10, "Kayle");
        _modelMap.put( 11, "Master Yi");
        _modelMap.put( 12, "Alistar");
        _modelMap.put( 13, "Ryze");
        _modelMap.put( 14, "Sion");
        _modelMap.put( 15, "Sivir");
        _modelMap.put( 16, "Soraka");
        _modelMap.put( 17, "Teemo");
        _modelMap.put( 18, "Tristana");
        _modelMap.put( 19, "Warwick");
        _modelMap.put( 20, "Nunu");
        _modelMap.put( 21, "Miss Fortune");
        _modelMap.put( 22, "Ashe");
        _modelMap.put( 23, "Tryndamere");
        _modelMap.put( 24, "Jax");
        _modelMap.put( 25, "Morgana");
        _modelMap.put( 26, "Zilean");
        _modelMap.put( 27, "Singed");
        _modelMap.put( 28, "Evelynn");
        _modelMap.put( 29, "Twitch");
        _modelMap.put( 30, "Karthus");
        _modelMap.put( 31, "Cho'Gath");
        _modelMap.put( 32, "Amumu");
        _modelMap.put( 33, "Rammus");
        _modelMap.put( 34, "Anivia");
        _modelMap.put( 35, "Shaco");
        _modelMap.put( 36, "Dr. Mundo");
        _modelMap.put( 37, "Sona");
        _modelMap.put( 38, "Kassadin");
        _modelMap.put( 39, "Irelia");
        _modelMap.put( 40, "Janna");
        _modelMap.put( 41, "Gangplank");
        _modelMap.put( 42, "Corki");
        _modelMap.put( 43, "Karma");
        _modelMap.put( 44, "Taric");
        _modelMap.put( 45, "Veigar");
        _modelMap.put( 48, "Trundle");
        _modelMap.put( 50, "Swain");
        _modelMap.put( 51, "Caitlyn");
        _modelMap.put( 53, "Blitzcrank");
        _modelMap.put( 54, "Malphite");
        _modelMap.put( 55, "Katarina");
        _modelMap.put( 56, "Nocturne");
        _modelMap.put( 57, "Maokai");
        _modelMap.put( 58, "Renekton");
        _modelMap.put( 59, "Jarvan IV");
        _modelMap.put( 61, "Orianna");
        _modelMap.put( 62, "Wukong");
        _modelMap.put( 63, "Brand");
        _modelMap.put( 64, "Lee Sin");
        _modelMap.put( 67, "Vayne");
        _modelMap.put( 68, "Rumble");
        _modelMap.put( 69, "Cassiopeia");
        _modelMap.put( 72, "Skarner");
        _modelMap.put( 74, "Heimerdinger");
        _modelMap.put( 75, "Nasus");
        _modelMap.put( 76, "Nidalee");
        _modelMap.put( 77, "Udyr");
        _modelMap.put( 78, "Poppy");
        _modelMap.put( 79, "Gragas");
        _modelMap.put( 80, "Pantheon");
        _modelMap.put( 81, "Ezreal");
        _modelMap.put( 82, "Mordekaiser");
        _modelMap.put( 83, "Yorick");
        _modelMap.put( 84, "Akali");
        _modelMap.put( 85, "Kennen");
        _modelMap.put( 86, "Garen");
        _modelMap.put( 89, "Leona");
        _modelMap.put( 90, "Malzahar");
        _modelMap.put( 91, "Talon");
        _modelMap.put( 92, "Riven");
        _modelMap.put( 96, "Kog'Maw");
        _modelMap.put( 98, "Shen");
        _modelMap.put( 99, "Lux");
        _modelMap.put(101, "Xerath");
        _modelMap.put(102, "Shyvana");
        _modelMap.put(103, "Ahri");
        _modelMap.put(104, "Graves");
        _modelMap.put(105, "Fizz");
        _modelMap.put(106, "Volibear");
        _modelMap.put(110, "Varus");
        _modelMap.put(111, "Nautilus");
        _modelMap.put(112, "Viktor");
        _modelMap.put(113, "Sejuani");
        _modelMap.put(114, "Fiora");
        _modelMap.put(115, "Ziggs");
        _modelMap.put(117, "Lulu");
        _modelMap.put(119, "Draven");
        _modelMap.put(120, "Hecarim");
        _modelMap.put(122, "Darius");
        _modelMap.put(126, "Jayce");
        _modelMap.put(143, "Zyra");
        _modelMap.put(131, "Diana");
        _modelMap.put(107, "Rengar");
        _modelMap.put(134, "Syndra");
        _modelMap.put(121, "Kha'Zix");
        _modelMap.put( 60, "Elise");
        _modelMap.put(238, "Zed");
        _modelMap.put(267, "Nami");
        _modelMap.put(254, "Vi");
        _modelMap.put(412, "Thresh");
        _modelMap.put(133, "Quinn");
        _modelMap.put(154, "Zac");
        _modelMap.put(127, "Lissandra");
        _modelMap.put(266, "Aatrox");
        _modelMap.put(236, "Lucian");
        _modelMap.put(222, "Jinx");
        _modelMap.put(157, "Yasuo");
        
        _champMap = new HashMap<Integer, LeagueChampion>();
    }
    
    public static String getNameForChampion(int id) {
        return _modelMap.get(id);
    }
    
    public static int getIdForChampion(String name) {
        return _modelMap.getKey(name);
    }
    
    public static LeagueChampion getChampionWithName(String name) {
        return getChampionWithId(_modelMap.getKey(name));
    }
    
    public static LeagueChampion getChampionWithId(int id) {
        if(!_champMap.containsKey(id))
            _champMap.put(id, new LeagueChampion(id));
        return _champMap.get(id);
    }
    
    private String _name;
    private int _id;
    
    private LeagueChampion(String name) {
        _name = name;
        _id = getIdForChampion(name);
    }
    
    private LeagueChampion(int id) {
        _name = getNameForChampion(id);
        _id = id;
    }
    
    public void setName(String name) {
        _name = name;
    }
    
    public void setId(int id) {
        _id = id;
    }
    
    public String getName() {
        return _name;
    }
    
    public int getId() {
        return _id;
    }
    
    public String toString() {
        return "<Champion " + _name + "(#" + _id + ")>";
    }
    
    // public String getFilename() {
    //     return getName().toLowerCase().replaceAll("[^a-z0-9]", "");
    // }
}
