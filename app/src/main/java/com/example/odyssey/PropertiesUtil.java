package com.example.odyssey;

import android.util.Log;

import com.example.odyssey.clients.ClientUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public class PropertiesUtil {
    private static final String PROPERTIES_PATH = "local.properties";
    private static final String SERVER_IP_KEY = "server.ip";

    public static String getServerIp() {
        return getProperty(SERVER_IP_KEY, "http://192.168.1.4:8080/api/v1/");
//        return getProperty(SERVER_IP_KEY, "http://192.168.1.4:8080/api/v1/");
    }

    private static String getProperty(String propertyKey, String defaultValue) {
        Properties properties = new Properties();
        InputStream inputStream = null;

        try {
            inputStream = Files.newInputStream(Paths.get(PROPERTIES_PATH));
            if (inputStream != null) {
                properties.load(inputStream);
                return properties.getProperty(propertyKey);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("PROPERTIES", "Failed reading key: " + propertyKey);
        return defaultValue;
    }
}