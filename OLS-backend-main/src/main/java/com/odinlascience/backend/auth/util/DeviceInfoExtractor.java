package com.odinlascience.backend.auth.util;

import jakarta.servlet.http.HttpServletRequest;

public final class DeviceInfoExtractor {

    private DeviceInfoExtractor() {}

    public static String extract(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || userAgent.isBlank()) {
            return "Appareil inconnu";
        }
        return parseUserAgent(userAgent);
    }

    public static String extractIpAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private static String parseUserAgent(String ua) {
        String browser = parseBrowser(ua);
        String os = parseOS(ua);
        return browser + " sur " + os;
    }

    private static String parseBrowser(String ua) {
        if (ua.contains("Edg/")) return "Edge";
        if (ua.contains("OPR/") || ua.contains("Opera")) return "Opera";
        if (ua.contains("Chrome/") && !ua.contains("Edg/")) return "Chrome";
        if (ua.contains("Safari/") && !ua.contains("Chrome/")) return "Safari";
        if (ua.contains("Firefox/")) return "Firefox";
        return "Navigateur inconnu";
    }

    private static String parseOS(String ua) {
        if (ua.contains("Windows")) return "Windows";
        if (ua.contains("Macintosh") || ua.contains("Mac OS")) return "macOS";
        if (ua.contains("Android")) return "Android";
        if (ua.contains("iPhone") || ua.contains("iPad")) return "iOS";
        if (ua.contains("Linux")) return "Linux";
        return "OS inconnu";
    }
}
