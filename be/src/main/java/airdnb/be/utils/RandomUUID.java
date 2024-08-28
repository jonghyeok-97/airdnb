package airdnb.be.utils;

import java.util.UUID;

public class RandomUUID {

    private static final int UUID_PREFIX_START = 0;
    private static final int UUID_PREFIX_END = 6;

    public static String createSixDigitUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(UUID_PREFIX_START, UUID_PREFIX_END);
    }
}
