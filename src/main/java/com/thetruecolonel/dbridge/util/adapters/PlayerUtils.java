package com.thetruecolonel.dbridge.util.adapters;

public final class PlayerUtils {

    private PlayerUtils() {}

    public static String getAvatarUrl(String username) {
        return "https://mc-heads.net/avatar/" + username + "/100";
    }
}
