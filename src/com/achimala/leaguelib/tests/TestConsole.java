package com.achimala.leaguelib.tests;

// import com.achimala.leaguelib.connection.*;
// import com.achimala.leaguelib.models.*;
import com.gvaneyck.rtmp.*;
import java.io.*;

public class TestConsole {
    public static void main(String[] args) throws Exception {
        Console console = System.console();
        String username, password, client, server;
        username = console.readLine("Username: ");
        password = new String(console.readPassword("Password: "));
        server = console.readLine("  Server: ");
        client = console.readLine("  Client: ");
        LoLRTMPSClient lol = new LoLRTMPSClient(server, client, username, password);
        lol.connectAndLogin();
        for(;;) {
            String cmd[] = console.readLine("> ").split(" ");
        }
    }
}