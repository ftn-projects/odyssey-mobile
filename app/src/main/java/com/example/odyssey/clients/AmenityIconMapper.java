package com.example.odyssey.clients;

import com.example.odyssey.R;

import java.util.HashMap;
import java.util.Map;

public class AmenityIconMapper {
    private static final Map<String, Integer> amenityIconMap;

    static {
        amenityIconMap = new HashMap<>();
        amenityIconMap.put("Wifi", R.drawable.ic_wifi_icon);
        amenityIconMap.put("Air conditioning", R.drawable.ic_snowflake);
        amenityIconMap.put("BBQ", R.drawable.ic_bbq);
        amenityIconMap.put("Mountain view", R.drawable.ic_mountain);
        amenityIconMap.put("Pet friendly", R.drawable.ic_paw);
    }

    public static int getIconResourceId(String amenityTitle) {
        return amenityIconMap.getOrDefault(amenityTitle, R.drawable.ic_wifi_icon);
    }
}
