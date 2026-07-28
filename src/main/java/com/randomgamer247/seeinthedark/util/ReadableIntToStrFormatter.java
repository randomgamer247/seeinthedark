package com.randomgamer247.seeinthedark.util;

import net.minecraft.util.Mth;

/**
 * Formats large numbers with SI-system suffixes
 * Might crash if more than 3 decimals are used
 */
public class ReadableIntToStrFormatter {
    public static final String[] SI_SUFFIXES = {"", "k", "M", "G"};

    public static String format(int integer, int decimals) {
        if (Mth.abs(integer) < 1000) return Integer.toString(integer);
        StringBuilder sb = new StringBuilder(16);
        sb.append(Mth.abs(integer));
        int magn = (sb.length() - 1) / 3;
        int mod = sb.length() % 3;
        int shift = mod == 0 ? 3 : mod;
        sb.delete(shift + decimals, sb.length());
        sb.insert(shift, '.');
        sb.append(SI_SUFFIXES[magn]);
        if (integer < 0) sb.insert(0, '-');
        return sb.toString();
    }
}
