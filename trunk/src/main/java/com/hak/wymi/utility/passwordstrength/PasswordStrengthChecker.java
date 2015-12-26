package com.hak.wymi.utility.passwordstrength;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordStrengthChecker {
    private static final Integer MINIMUM_PASSWORD_LENGTH = 8;


    private static final Integer MULTIPLIERS_SEQUENTIAL_LETTER = 3;
    private static final Integer MULTIPLIERS_SEQUENTIAL_NUMBER = 3;
    private static final Integer MULTIPLIERS_SEQUENTIAL_SYMBOL = 3;

    private static final Integer MULTIPLIERS_CONSECUTIVE_LETTER = 2;
    private static final Integer MULTIPLIERS_CONSECUTIVE_NUMBER = 2;
    private static final Integer MULTIPLIERS_NUMBER = 4;
    private static final Integer MULTIPLIERS_SYMBOLS = 6;
    private static final Integer MULTIPLIERS_MIDDLE_CHARACTER = 2;
    private static final Integer MULTIPLIERS_LENGTH = 4;

    private static int matchCount(String input, String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(input);

        int result = 0;
        while (matcher.find()) {
            result += 1;
        }

        return result;
    }

    public static PasswordStrengthResult getStrengthResults(String password) {
        PasswordStrengthResult result = new PasswordStrengthResult();

        if (password != null) {
            result.bonuses.put("passwordLength", password.length() * MULTIPLIERS_LENGTH);

            int consecutiveLetters = matchCount(password, "(?=[A-Z]{2})") + matchCount(password, "(?=[a-z]{2})");
            result.penalties.put("consecutiveLetters", consecutiveLetters * MULTIPLIERS_CONSECUTIVE_LETTER);

            result.penalties.put("consecutiveNumbers", matchCount(password, "(?=[0-9]{2})") * MULTIPLIERS_CONSECUTIVE_NUMBER);

            int sequentialLetters = getSequentialPenalty(password, "abcdefghijklmnopqrstuvwxyz", MULTIPLIERS_SEQUENTIAL_LETTER);
            sequentialLetters += getSequentialPenalty(password, "qwertyuiop[]\\\\", MULTIPLIERS_SEQUENTIAL_LETTER);
            sequentialLetters += getSequentialPenalty(password, "asdfghjkl;'", MULTIPLIERS_SEQUENTIAL_LETTER);
            sequentialLetters += getSequentialPenalty(password, "zxcvbnm,./", MULTIPLIERS_SEQUENTIAL_LETTER);

            result.penalties.put("sequentialLetters", sequentialLetters);
            result.penalties.put("sequentialNumbers", getSequentialPenalty(password, "01234567890", MULTIPLIERS_SEQUENTIAL_NUMBER));
            result.penalties.put("sequentialSymbols", getSequentialPenalty(password, "~!@#$%^&*()_+", MULTIPLIERS_SEQUENTIAL_SYMBOL));

            result.penalties.put("poorPractices", getPoorPracticesPenalty(password));
            result.penalties.put("repeatCharacters", getRepeatCharacterPenalty(password));

            if (password.length() > 2) {
                result.bonuses.put("middleCharacters", password.substring(1, password.length() - 1).replaceAll("[a-zA-Z]", "").length() * MULTIPLIERS_MIDDLE_CHARACTER);
            }
            result.bonuses.put("upperCaseLetters", getLetterBonus(password, "[A-Z]"));
            result.bonuses.put("lowerCaseLetters", getLetterBonus(password, "[a-z]"));
            result.bonuses.put("numbers", getNumberBonus(password));
            result.bonuses.put("symbols", getSymbolCount(password) * MULTIPLIERS_SYMBOLS);
            result.bonuses.put("requirements", getRequirementsBonus(password));
        }
        return result;
    }

    private static Integer getRequirementsBonus(String password) {
        int score = 0;
        int requirementsMetCount = 0;

        if (password.matches(".*[a-z].*")) {
            requirementsMetCount += 1;
        }

        if (password.matches(".*[A-Z].*")) {
            requirementsMetCount += 1;
        }

        if (password.matches(".*[0-9].*")) {
            requirementsMetCount += 1;
        }

        if (!password.matches("^[a-zA-Z0-9]+$")) {
            requirementsMetCount += 1;
        }

        int minimumNumberOfRequirements = 4;
        if (password.length() >= MINIMUM_PASSWORD_LENGTH) {
            requirementsMetCount += 1;
            minimumNumberOfRequirements = 3;
        }

        if (requirementsMetCount > minimumNumberOfRequirements) {
            score += requirementsMetCount * 2;
        }

        return score;
    }

    private static Integer getNumberBonus(String password) {
        int numberCount = matchCount(password, "[0-9]");
        int passwordLength = password.length();

        if (numberCount > 0 && numberCount < passwordLength) {
            return numberCount * MULTIPLIERS_NUMBER;
        }

        return 0;
    }

    private static Integer getLetterBonus(String password, String pattern) {
        int letterCount = matchCount(password, pattern);
        int passwordLength = password.length();

        if (letterCount > 0 && letterCount < passwordLength) {
            return (passwordLength - letterCount) * 2;
        }

        return 0;
    }

    private static Integer getRepeatCharacterPenalty(String password) {
        boolean repeatFound;
        int score = 0;
        int iLength = password.length();
        int uniqueCharacterCount = iLength;
        for (int i = 0; i < iLength; i += 1) {
            repeatFound = false;
            for (int j = 0; j < iLength; j += 1) {
                if (password.charAt(i) == password.charAt(j) && i != j) {
                    repeatFound = true;
                    score += Math.abs(iLength / (j - i));
                }
            }

            if (repeatFound) {
                uniqueCharacterCount -= 1;
                score = (int) Math.ceil(score / Math.max(uniqueCharacterCount, 1d));
            }
        }
        return score;
    }

    private static Integer getPoorPracticesPenalty(String password) {

        int score = 0;
        int passwordLength = password.length();
        int numberCount = matchCount(password, "[0-9]");
        int upperCaseLetterCount = matchCount(password, "[A-Z]");
        int lowerCaseLetterCount = matchCount(password, "[a-z]");
        int symbolCount = getSymbolCount(password);

        if ((lowerCaseLetterCount > 0 || upperCaseLetterCount > 0) && symbolCount == 0 && numberCount == 0) {
            score += passwordLength;
        }

        if (lowerCaseLetterCount == 0 && upperCaseLetterCount == 0 && symbolCount == 0 && numberCount > 0) {
            score += passwordLength;
        }

        return score;
    }

    private static int getSymbolCount(String password) {
        return matchCount(password, "[-!\\\\$%^&*()_+|~=`{}\\[\\]:\";'<>?,\\./ #@]");
    }

    private static int getSequentialPenalty(String password, String characters, Integer multiplier) {

        int iLength = characters.length() - 3;
        int score = 0;
        String lowerCasePassword = password.toLowerCase();
        String reversedPassword = new StringBuffer(password).reverse().toString();

        String subString;
        for (int i = 0; i < iLength; i += 1) {
            subString = characters.substring(i, i + 3);
            if (lowerCasePassword.contains(subString) || reversedPassword.contains(subString)) {
                score += multiplier;
            }
        }

        return score;
    }
}
