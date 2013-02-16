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

// import com.achimala.leaguelib.connection.*;
// import com.achimala.leaguelib.models.*;
import com.gvaneyck.rtmp.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

public class TestConsole {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        String username, password, client, server;
        username = args[0];
        password = args[1];
        server = args[2];
        client = args[3];
        
        System.out.println("Logging into " + server + " as " + username);
        LoLRTMPSClient lol = new LoLRTMPSClient(server, client, username, password);
        lol.connectAndLogin();
        
        Gson gson = new Gson();
        
        for(;;) {
            System.out.print("> ");
            String cmd[] = in.readLine().split(" ");
            if(cmd[0].equals("exit") || cmd[0].equals("quit") || cmd[0].equals("q"))
                break;
            if(cmd.length < 3) {
                System.out.println("USAGE: <service> <function> <args...>");
                continue;
            }
            
            String service = cmd[0];
            String function = cmd[1];
            String arguments = "";
            for(int i = 2; i < cmd.length; i++)
                arguments += cmd[i] + " ";
            Object[] params = gson.fromJson(arguments, ArrayList.class).toArray(new Object[0]);
            System.out.println(String.format("CALL %s.%s(%s)", service, function, params.toString()));
            
            int id = lol.invoke(service, function, params);
            System.out.println("RECV " + lol.getResult(id));
            System.out.println();
        }
        
        lol.close();
    }
}