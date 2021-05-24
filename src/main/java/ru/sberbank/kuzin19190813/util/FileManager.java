package ru.sberbank.kuzin19190813.util;

import java.io.File;

public class FileManager {
    public static File getFromResources(String name) {
        String str = String.format("%s/%s", getResourcesDir().getAbsolutePath(), name);
        return new File(str);
    }

    public static File getResourcesDir() {
        String path = String.format("%s/src/main/resources", System.getProperty("user.dir"));
        return new File(path);
    }
}
