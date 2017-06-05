package com.redgear.cog.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Caseifier {

    /**
     * Rexex totally 'borrowed' from lodash. Thanks Lodash!
     **/
    private static final Pattern breaker = Pattern.compile("[A-Z\\xc0-\\xd6\\xd8-\\xde]?[a-z\\xdf-\\xf6\\xf8-\\xff]+" +
            "(?:['’](?:d|ll|m|re|s|t|ve))?(?=[\\xac\\xb1\\xd7\\xf7\\x00-\\x2f\\x3a-\\x40\\x5b-\\" +
            "x60\\x7b-\\xbf\\u2000-\\u206f \\t\\x0b\\f\\xa0\\ufeff\\n\\r\\u2028\\u2029\\u1680\\" +
            "u180e\\u2000\\u2001\\u2002\\u2003\\u2004\\u2005\\u2006\\u2007\\u2008\\u2009\\u200a" +
            "\\u202f\\u205f\\u3000]|[A-Z\\xc0-\\xd6\\xd8-\\xde]|$)|(?:[A-Z\\xc0-\\xd6\\xd8-\\xde" +
            "]|[^\\ud800-\\udfff\\xac\\xb1\\xd7\\xf7\\x00-\\x2f\\x3a-\\x40\\x5b-\\x60\\x7b-\\xbf" +
            "\\u2000-\\u206f \\t\\x0b\\f\\xa0\\ufeff\\n\\r\\u2028\\u2029\\u1680\\u180e\\u2000\\" +
            "u2001\\u2002\\u2003\\u2004\\u2005\\u2006\\u2007\\u2008\\u2009\\u200a\\u202f\\u205f" +
            "\\u3000\\d+\\u2700-\\u27bfa-z\\xdf-\\xf6\\xf8-\\xffA-Z\\xc0-\\xd6\\xd8-\\xde])+(?:" +
            "['’](?:D|LL|M|RE|S|T|VE))?(?=[\\xac\\xb1\\xd7\\xf7\\x00-\\x2f\\x3a-\\x40\\x5b-\\x60" +
            "\\x7b-\\xbf\\u2000-\\u206f \\t\\x0b\\f\\xa0\\ufeff\\n\\r\\u2028\\u2029\\u1680\\u180e" +
            "\\u2000\\u2001\\u2002\\u2003\\u2004\\u2005\\u2006\\u2007\\u2008\\u2009\\u200a\\u202f" +
            "\\u205f\\u3000]|[A-Z\\xc0-\\xd6\\xd8-\\xde](?:[a-z\\xdf-\\xf6\\xf8-\\xff]|[^\\ud800-" +
            "\\udfff\\xac\\xb1\\xd7\\xf7\\x00-\\x2f\\x3a-\\x40\\x5b-\\x60\\x7b-\\xbf\\u2000-\\u206f" +
            " \\t\\x0b\\f\\xa0\\ufeff\\n\\r\\u2028\\u2029\\u1680\\u180e\\u2000\\u2001\\u2002\\u2003" +
            "\\u2004\\u2005\\u2006\\u2007\\u2008\\u2009\\u200a\\u202f\\u205f\\u3000\\d+\\u2700-\\" +
            "u27bfa-z\\xdf-\\xf6\\xf8-\\xffA-Z\\xc0-\\xd6\\xd8-\\xde])|$)|[A-Z\\xc0-\\xd6\\xd8-" +
            "\\xde]?(?:[a-z\\xdf-\\xf6\\xf8-\\xff]|[^\\ud800-\\udfff\\xac\\xb1\\xd7\\xf7\\x00-" +
            "\\x2f\\x3a-\\x40\\x5b-\\x60\\x7b-\\xbf\\u2000-\\u206f \\t\\x0b\\f\\xa0\\ufeff\\n\\r" +
            "\\u2028\\u2029\\u1680\\u180e\\u2000\\u2001\\u2002\\u2003\\u2004\\u2005\\u2006\\u2007" +
            "\\u2008\\u2009\\u200a\\u202f\\u205f\\u3000\\d+\\u2700-\\u27bfa-z\\xdf-\\xf6\\xf8-" +
            "\\xffA-Z\\xc0-\\xd6\\xd8-\\xde])+(?:['’](?:d|ll|m|re|s|t|ve))?|[A-Z\\xc0-\\xd6\\xd8-" +
            "\\xde]+(?:['’](?:D|LL|M|RE|S|T|VE))?|\\d*(?:(?:1ST|2ND|3RD|(?![123])\\dTH)\\b)|\\d*" +
            "(?:(?:1st|2nd|3rd|(?![123])\\dth)\\b)|\\d+|(?:[\\u2700-\\u27bf]|(?:\\ud83c[\\udde6-" +
            "\\uddff]){2}|[\\ud800-\\udbff][\\udc00-\\udfff])[\\ufe0e\\ufe0f]?(?:[\\u0300-\\u036f" +
            "\\ufe20-\\ufe2f\\u20d0-\\u20ff]|\\ud83c[\\udffb-\\udfff])?(?:\\u200d(?:[^\\ud800-" +
            "\\udfff]|(?:\\ud83c[\\udde6-\\uddff]){2}|[\\ud800-\\udbff][\\udc00-\\udfff])[\\ufe0e" +
            "\\ufe0f]?(?:[\\u0300-\\u036f\\ufe20-\\ufe2f\\u20d0-\\u20ff]|\\ud83c[\\udffb-\\udfff])?)*");

    /* Definitions

    camelCase
    PascalCase

    snake_case
    CAPS_CASE

    kebab-case
    Train-Case

    Sentence case
    Title Case

     */


    public static String camelCase(String input) {
        String full = pascalCase(input);

        return full.substring(0, 1).toLowerCase() + full.substring(1);
    }

    public static String pascalCase(String input) {
        return Lambda.foldLeft("", words(input), (result, next) -> result + next.substring(0, 1).toUpperCase() + next.substring(1).toLowerCase());
    }

    public static String snakeCase(String input) {
        return Lambda.foldLeft("", words(input), (result, next) -> result + "_" + next.toLowerCase()).substring(1);
    }

    public static String capsCase(String input) {
        return Lambda.foldLeft("", words(input), (result, next) -> result + "_" + next.toUpperCase()).substring(1);
    }

    public static String kebabCase(String input) {
        return Lambda.foldLeft("", words(input), (result, next) -> result + "-" + next.toLowerCase()).substring(1);
    }

    public static String trainCase(String input) {
        return Lambda.foldLeft("", words(input), (result, next) -> result + "-" + next.substring(0, 1).toUpperCase() + next.substring(1).toLowerCase()).substring(1);
    }

    public static String sentenceCase(String input) {
        String full = Lambda.foldLeft("", words(input), (result, next) -> result + " " + next.toLowerCase()).substring(1);

        return full.substring(0, 1).toUpperCase() + full.substring(1);
    }

    public static String titleCase(String input) {
        return Lambda.foldLeft("", words(input), (result, next) -> result + " " + next.substring(0, 1).toUpperCase() + next.substring(1).toLowerCase()).substring(1);
    }

    private static List<String> words(String input) {
        Matcher matcher = breaker.matcher(input);

        List<String> words = new ArrayList<>();

        while(matcher.find()) {
            words.add(matcher.group());
        }

        return words;
    }


}
