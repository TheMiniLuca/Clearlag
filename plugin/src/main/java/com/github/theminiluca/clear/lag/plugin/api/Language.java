package com.github.theminiluca.clear.lag.plugin.api;

public class Language {

    private static final String untracking_log = "Untracked %i entities in %s";

    public static String getUntrackingLog(int i, String s) {
        return untracking_log.replace("%i", String.valueOf(i)).replace("%s", s);
    }

}
