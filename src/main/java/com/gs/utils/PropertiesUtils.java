package com.gs.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

    public static String read(String key, String path) throws IOException {

        Properties properties = new Properties();
        InputStream in = new FileInputStream(path);
        properties.load(in);

        return properties.getProperty(key);
    }

    public static int readInt(String key,String path) throws IOException {
        return Integer.parseInt(read(key,path));
    }

}
