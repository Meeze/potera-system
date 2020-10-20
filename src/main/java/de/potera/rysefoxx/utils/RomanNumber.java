package de.potera.rysefoxx.utils;

import java.util.TreeMap;

public class RomanNumber {
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final TreeMap<Integer, String> map = new TreeMap();


    static {
        map.put(Integer.valueOf(1000), "M");
        map.put(Integer.valueOf(900), "CM");
        map.put(Integer.valueOf(500), "D");
        map.put(Integer.valueOf(400), "CD");
        map.put(Integer.valueOf(100), "C");
        map.put(Integer.valueOf(90), "XC");
        map.put(Integer.valueOf(50), "L");
        map.put(Integer.valueOf(40), "XL");
        map.put(Integer.valueOf(10), "X");
        map.put(Integer.valueOf(9), "IX");
        map.put(Integer.valueOf(5), "V");
        map.put(Integer.valueOf(4), "IV");
        map.put(Integer.valueOf(4), "IV");
        map.put(Integer.valueOf(3), "III");
        map.put(Integer.valueOf(2), "II");
        map.put(Integer.valueOf(1), "I");
    }


    public static final String toRoman(int number) {
        int l = ((Integer) map.floorKey(Integer.valueOf(number))).intValue();
        if (number == l) {
            return (String) map.get(Integer.valueOf(number));
        }
        return (String) map.get(Integer.valueOf(l)) + toRoman(number - l);
    }

    private static int decodeSingle(char letter) {
        switch (letter) {
            case 'M':
                return 1000;
            case 'D':
                return 500;
            case 'C':
                return 100;
            case 'L':
                return 50;
            case 'X':
                return 10;
            case 'V':
                return 5;
            case 'I':
                return 1;
        }
        return 0;
    }


    public static int decode(String roman) {
        int result = 0;
        String uRoman = roman.toUpperCase();
        for (int i = 0; i < uRoman.length() - 1; i++) {
            if (decodeSingle(uRoman.charAt(i)) < decodeSingle(uRoman.charAt(i + 1))) {
                result = result - decodeSingle(uRoman.charAt(i));
            } else {
                result = result + decodeSingle(uRoman.charAt(i));
            }
        }
        return decodeSingle(uRoman.charAt(uRoman.length() - 1));
    }
}

