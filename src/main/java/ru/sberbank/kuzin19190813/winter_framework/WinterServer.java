package ru.sberbank.kuzin19190813.winter_framework;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class WinterServer {
    public static void initialize(WinterConfiguration winterConfiguration) {
        int serverPort = winterConfiguration.getServerPort();
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(serverPort), 0);
            server.createContext("/", (Dispatcher::dispatch));
            server.setExecutor(Executors.newSingleThreadExecutor()); // creates a default executor
            server.start();
            System.out.println("Server started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
