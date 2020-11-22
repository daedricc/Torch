package com.climpy.torch.utils;

import org.bukkit.ChatColor;

import java.util.regex.Pattern;

public class PatternUtils {

    public static final transient Pattern REPLACE_ALL_PATTERN = Pattern.compile("(?<!&)&([0-9a-fk-orA-FK-OR])");

    public static final transient Pattern REPLACE_COLOR_PATTERN = Pattern.compile("(?<!&)&([0-9a-orA-FK])");

    public static final transient Pattern REPLACE_MAGIC_PATTERN = Pattern.compile("(?<!&)&([Kk])");

    public static final transient Pattern REPLACE_FORMAT_PATTERN = Pattern.compile("(?<!&)&([l-orL-OR])");

    public static final transient Pattern REPORT_PATTERN = Pattern.compile("(\\s|^)(hackers?|hacks?|hax|hacking|cheaters?|cheating|cheats?|hileci?|hile)($|\\s)", 2);

    public static final transient Pattern URL_PATTERN = Pattern.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-zA-Z]{2,3}(?:/\\S+)?)");

    public static final transient Pattern IP_PATTERN = Pattern.compile("([1-9]\\d?\\d?(\\.\\d{1,3}){2}\\.\\d{1,3},?)+", 2);

    private static final transient Pattern REPLACE_PATTERN = Pattern.compile("&&(?=[0-9a-fk-orA-FK-OR])");

    public static final transient Pattern TIME_PATTERN = Pattern.compile("\b([1-9][0-9]*[smhdy]$)", 2);

    public static String replaceColor(String input, Pattern pattern) {
        return REPLACE_PATTERN.matcher(pattern.matcher(input).replaceAll("§$1")).replaceAll("&");
    }

    public static String stripColor(String input, Pattern pattern) {
        return pattern.matcher(input).replaceAll("");
    }

    public static String stringColorized(String input) {
        return replaceColor(ChatColor.translateAlternateColorCodes('&', input), REPLACE_FORMAT_PATTERN);
    }
}
