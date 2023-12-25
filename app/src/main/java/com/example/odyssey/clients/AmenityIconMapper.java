package com.example.odyssey.clients;

import com.example.odyssey.R;

import java.util.HashMap;
import java.util.Map;

public class AmenityIconMapper {
    private static final Map<String, Integer> amenityIconMap;

    static {
        amenityIconMap = new HashMap<>();
        // Add mappings for amenity titles to drawable resource IDs
        amenityIconMap.put("Wifi", R.drawable.ic_wifi_icon);
        amenityIconMap.put("Air conditioning", R.drawable.ic_snowflake);
        amenityIconMap.put("BBQ", R.drawable.ic_bbq);
        amenityIconMap.put("Mountain view", R.drawable.ic_mountain);
        amenityIconMap.put("Pet friendly", R.drawable.ic_paw);
        // Add more mappings as needed
    }

    public static int getIconResourceId(String amenityTitle) {
        // If the title exists in the map, return the corresponding drawable resource ID
        // If not, you can return a default icon or handle it as needed
        return amenityIconMap.getOrDefault(amenityTitle, R.drawable.ic_wifi_icon);
    }
}
