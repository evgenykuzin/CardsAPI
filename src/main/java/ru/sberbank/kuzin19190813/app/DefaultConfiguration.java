package ru.sberbank.kuzin19190813.app;

import ru.sberbank.kuzin19190813.winter_framework.WinterConfiguration;

public class DefaultConfiguration implements WinterConfiguration {
    @Override
    public Integer getServerPort() {
        return 8000;
    }

    @Override
    public String getControllersPackageToScan() {
        return "ru.sberbank.kuzin19190813.controller";
    }
}
