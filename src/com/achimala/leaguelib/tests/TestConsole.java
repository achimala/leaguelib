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
            if(cmd.length < 2) {
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
    }
}