package com.example.ubercorp.utils;

import android.util.Base64;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public class JwtUtils {
    public static JSONObject decodeToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String payload = parts[1];
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE | Base64.NO_WRAP);
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

            return new JSONObject(decodedString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getEmailFromToken(String token) {
        try {
            JSONObject payload = decodeToken(token);
            if (payload != null) {
                return payload.optString("sub", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRoleFromToken(String token) {
        try {
            JSONObject payload = decodeToken(token);
            if (payload != null) {
                return payload.optString("roles", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getExpirationTime(String token) {
        try {
            JSONObject payload = decodeToken(token);
            if (payload != null) {
                return payload.optLong("exp", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isTokenExpired(String token) {
        try {
            long exp = getExpirationTime(token);
            if (exp == 0) {
                return true;
            }
            long currentTime = System.currentTimeMillis() / 1000;
            return currentTime > exp;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}