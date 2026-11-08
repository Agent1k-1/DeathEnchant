package com.agent1k.deathenchants.utils;

import org.bukkit.ChatColor;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HexUtils {
    private static final Pattern HEX = Pattern.compile("&#([a-fA-F0-9]{6})");

    private HexUtils() { }

    public static String color(String text) {
        if (text == null || text.isEmpty()) return text;
        Matcher matcher = HEX.matcher(text);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            StringBuilder color = new StringBuilder("§x");
            for (char digit : matcher.group(1).toCharArray()) color.append('§').append(digit);
            matcher.appendReplacement(result, Matcher.quoteReplacement(color.toString()));
        }
        matcher.appendTail(result);
        return ChatColor.translateAlternateColorCodes('&', result.toString());
    }

    public static List<String> color(List<String> lines) {
        return lines.stream().map(HexUtils::color).toList();
    }
}
