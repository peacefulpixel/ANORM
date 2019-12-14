package com.cplusjuice.anorm.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cplusjuice.anorm.util.CaseFormat.CAMEL_CASE;
import static com.cplusjuice.anorm.util.CaseFormat.SNAKE_CASE;

public class CaseFormatter {
    private String input;
    private CaseFormat format;

    public CaseFormatter(@NotNull String input) {
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Can't format an empty line");
        }

        this.input = input;

        boolean mixedRegisters =
                input.matches(".*[a-z].*") && input.matches(".*[A-Z].*");
        
        boolean allInUpperRegister = input.matches("^[A-Z_\\-]*$");
        boolean allInLowerRegister = input.matches("^[a-z_\\-]*$");
        boolean hasUnderscore = input.matches(".*_.*");

        if ((mixedRegisters || allInLowerRegister) && !hasUnderscore) {
            format = CAMEL_CASE;
        } else if (allInUpperRegister || allInLowerRegister) {
            format = SNAKE_CASE;
            this.input = input.toUpperCase();
        } else {
            throw new IllegalArgumentException("Can't determine the text format in " + input);
        }
    }

    @Nullable
    public String convert(@NotNull CaseFormat format) {
        if (format.equals(this.format)) {
            return input;
        }

        if (format.equals(CAMEL_CASE)) {
            return fromSnakeToCamel();
        }

        if (format.equals(CaseFormat.SNAKE_CASE)) {
            return fromCamelToSnake();
        }

        return null;
    }

    private String fromCamelToSnake() {
        StringBuilder result = new StringBuilder();

        Pattern upperCasePattern = Pattern.compile("[A-Z]");
        Matcher upperCase = upperCasePattern.matcher(input);

        if (input.charAt(0) == input.toUpperCase().charAt(0)) {
            upperCase.find(); // Skip first letter
        }

        String cuttedInput = input;
        while (upperCase.find()) {
            result.append(cuttedInput.substring(0, upperCase.start()).toUpperCase())
                  .append("_").append(cuttedInput.charAt(upperCase.start()));

            cuttedInput = cuttedInput.substring(upperCase.start() + 1);
            upperCase = upperCasePattern.matcher(cuttedInput);
        }

        return result.append(cuttedInput.toUpperCase()).toString();
    }

    private String fromSnakeToCamel() {
        StringBuilder result = new StringBuilder();

        Pattern underscorePattern = Pattern.compile("_");
        Matcher underscore = underscorePattern.matcher(input);

        String cuttedInput = input;
        while (underscore.find()) {
            result.append(cuttedInput.substring(0, underscore.start()).toLowerCase())
                    .append(cuttedInput.toUpperCase().charAt(underscore.start() + 1));

            cuttedInput = cuttedInput.substring(underscore.start() + 2);
            underscore = underscorePattern.matcher(cuttedInput);
        }

        return result.append(cuttedInput.toLowerCase()).toString();
    }
}
