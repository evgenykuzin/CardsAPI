package ru.sberbank.kuzin19190813.winter_framework;

public class WinterConfigurationHolder {
    private static WinterConfiguration winterConfiguration;

    public static void setConfiguration(WinterConfiguration config) {
        winterConfiguration = config;
    }

    public static WinterConfiguration getConfiguration() {
        return winterConfiguration;
    }
}
