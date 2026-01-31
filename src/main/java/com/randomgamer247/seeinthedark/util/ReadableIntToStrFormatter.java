package com.randomgamer247.seeinthedark.util;

import net.minecraft.util.Mth;

/**
 * Work in progress
 */
public class ReadableIntToStrFormatter {
    public static final String[] SI_SUFFIXES = {" ", " k", " M", " G"};

    public static String format(int integer, int decimals) {
        if (integer < 1000) return Integer.toString(integer);
        StringBuilder sb = new StringBuilder(16);
        sb.append(Mth.abs(integer));
        int magn = (sb.length() + 1) / 3;
        int mod = sb.length() % 3;
        int shift = mod == 0 ? 3 : mod;
        sb.delete(shift + decimals + 1, sb.length() - 1);
        sb.insert(shift, '.');
        sb.append(SI_SUFFIXES[magn]);
        return sb.toString();
    }
}
