package ru.sberbank.kuzin19190813.app;

import ru.sberbank.kuzin19190813.winter_framework.WinterConfiguration;
import ru.sberbank.kuzin19190813.winter_framework.WinterConfigurationHolder;
import ru.sberbank.kuzin19190813.winter_framework.WinterServer;

public class Application {
    public static void main(String[] args) {
        start();
    }

    public static void start(WinterConfiguration winterConfiguration) {
        WinterConfigurationHolder.setConfiguration(winterConfiguration);
        WinterServer.initialize(winterConfiguration);
    }

    public static void start() {
        WinterConfiguration winterConfiguration = new DefaultConfiguration();
        WinterConfigurationHolder.setConfiguration(winterConfiguration);
        WinterServer.initialize(winterConfiguration);
    }
}
