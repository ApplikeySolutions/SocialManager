package com.applikeysolutions.library;


import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    private StringUtils() {
    }

    public static boolean isBlank(String text) {
        return text.trim().length() == 0;
    }

    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static String reverse(String source) {
        return (new StringBuilder(source)).reverse().toString();
    }

    public static String replaceLast(String source, String pattern, String to) {
        int index = source.lastIndexOf(pattern);
        if(index >= 0) {
            source = (new StringBuilder(source)).replace(index, index + pattern.length(), to).toString();
        }

        return source;
    }

    public static List<Integer> indexesOf(String src, String target) {
        List<Integer> positions = new ArrayList();

        for(int index = src.indexOf(target); index >= 0; index = src.indexOf(target, index + 1)) {
            positions.add(Integer.valueOf(index));
        }

        return positions;
    }

    public static String capitalize(String str, char... delimiters) {
        int delimLen = delimiters == null?-1:delimiters.length;
        if(!isEmpty(str) && delimLen != 0) {
            char[] buffer = str.toCharArray();
            boolean capitalizeNext = true;

            for(int i = 0; i < buffer.length; ++i) {
                char ch = buffer[i];
                if(isDelimiter(ch, delimiters)) {
                    capitalizeNext = true;
                } else if(capitalizeNext) {
                    buffer[i] = Character.toTitleCase(ch);
                    capitalizeNext = false;
                }
            }

            return new String(buffer);
        } else {
            return str;
        }
    }

    public static String capitalize(String str) {
        return capitalize(str, (char[])null);
    }

    public static String capitalizeFully(String str, char... delimiters) {
        int delimLen = delimiters == null?-1:delimiters.length;
        if(!isEmpty(str) && delimLen != 0) {
            str = str.toLowerCase();
            return capitalize(str, delimiters);
        } else {
            return str;
        }
    }

    public static String capitalizeFully(String str) {
        return capitalizeFully(str, (char[])null);
    }

    public static String uncapitalize(String str, char... delimiters) {
        int delimLen = delimiters == null?-1:delimiters.length;
        if(!isEmpty(str) && delimLen != 0) {
            char[] buffer = str.toCharArray();
            boolean uncapitalizeNext = true;

            for(int i = 0; i < buffer.length; ++i) {
                char ch = buffer[i];
                if(isDelimiter(ch, delimiters)) {
                    uncapitalizeNext = true;
                } else if(uncapitalizeNext) {
                    buffer[i] = Character.toLowerCase(ch);
                    uncapitalizeNext = false;
                }
            }

            return new String(buffer);
        } else {
            return str;
        }
    }

    public static String uncapitalize(String str) {
        return uncapitalize(str, (char[])null);
    }

    private static boolean isDelimiter(char ch, char[] delimiters) {
        if(delimiters == null) {
            return Character.isWhitespace(ch);
        } else {
            char[] var2 = delimiters;
            int var3 = delimiters.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                char delimiter = var2[var4];
                if(ch == delimiter) {
                    return true;
                }
            }

            return false;
        }
    }

    public static String withSuffix(long count) {
        if(count < 1000L) {
            return "" + count;
        } else {
            int exp = (int)(Math.log((double)count) / Math.log(1000.0D));
            return String.format("%.1f %c", new Object[]{Double.valueOf((double)count / Math.pow(1000.0D, (double)exp)), Character.valueOf("kMGTPE".charAt(exp - 1))});
        }
    }
}

