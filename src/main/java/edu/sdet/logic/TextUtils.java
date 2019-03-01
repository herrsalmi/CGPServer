package edu.sdet.logic;

import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * project FunctionalAnalysis
 * Created by ayyoub on 2/21/18.
 */
public final class TextUtils {

    private TextUtils() {
    }

    /**
     * Case insensitive contains()
     *
     * @param src  source text
     * @param what target text
     * @return true if contained, false if not
     */
    public static boolean containsIgnoreCase(String src, String what) {
        final int length = what.length();
        if (length == 0)
            return true; // Empty string is contained

        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));

        for (int i = src.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = src.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;

            if (src.regionMatches(true, i, what, 0, length))
                return true;
        }

        return false;
    }

    public static boolean containsIgnoreCase(String src, List<String> what) {
        Optional<Boolean> any = what.stream().map(e -> containsIgnoreCase(src, e)).filter(e -> e).findAny();
        return any.isPresent();
    }

    /**
     * case insensitive marking words using html tag
     *
     * @param src     source text
     * @param keywords keywords to mark
     * @return marked text
     */
    public static String markAllIgnoreCase(String src, List<String> keywords) {
        StringTokenizer st = new StringTokenizer(src);
        StringBuilder sb = new StringBuilder();
        Pattern pattern;
        Matcher matcher;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            for (String k : keywords) {
                pattern = Pattern.compile(k, Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(token);
                if (matcher.find()) {
                    //token = token.replaceFirst(matcher.group(0), "<mark>" + matcher.group(0) + "</mark>");
                    token = "<mark>" + token + "</mark>";
                }
            }
            sb.append(token).append(" ");
        }
        return sb.toString();
    }

    /**
     * Count occurences of a given word in a text
     * @param src text
     * @param what word
     * @return number of occurences
     */
    public static int occurences(String src, String what) {
        Pattern pattern = Pattern.compile(what, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(src);
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }
}
