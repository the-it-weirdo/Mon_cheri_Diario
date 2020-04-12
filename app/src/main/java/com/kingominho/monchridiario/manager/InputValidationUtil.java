package com.kingominho.monchridiario.manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidationUtil {

    public static final int SUCCESS_CODE_PASSWORDS_MATCH = 2;
    public static final int SUCCESS_CODE_REGEX_MATCH = 1;
    public static final int ERROR_CODE_EMPTY_STRING = -1;
    public static final int ERROR_CODE_REGEX_NO_MATCH = -2;
    public static final int ERROR_CODE_PASSWORD_NO_MATCH = -3;
    public static final String ERROR_MESSAGE_INVALID_PASSWORD = "Please enter a valid password." +
            " Your password should contain at least 1 number, 1 capital letter, 1 small letter" +
            "and 1 special character. Minimum password length should be 8 characters.";

    public int validatePassword(String passPhrase) {
        if (passPhrase.trim().isEmpty()) {
            return ERROR_CODE_EMPTY_STRING;
        } else {
            String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
            Pattern patternPassword = Pattern.compile(passwordRegex);
            Matcher matcherPassword = patternPassword.matcher(passPhrase);

            if (!matcherPassword.matches()) {
                return ERROR_CODE_REGEX_NO_MATCH;
            } else {
                return SUCCESS_CODE_REGEX_MATCH;
            }
        }
    }

    public int validateEmail(String email) {
        if (email.trim().isEmpty()) {
            return ERROR_CODE_EMPTY_STRING;
        } else {
            String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:" +
                    "[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
            Pattern patternEmail = Pattern.compile(emailRegex);
            Matcher matcherEmail = patternEmail.matcher(email);

            if (!matcherEmail.matches()) {
                return ERROR_CODE_REGEX_NO_MATCH;
            } else {
                return SUCCESS_CODE_REGEX_MATCH;
            }
        }
    }

    public int confirmPassword(String passPhrase, String confirmedPassPhrase) {
        int passPhraseResult = validatePassword(passPhrase);
        int confirmedPassPhraseResult = validatePassword(confirmedPassPhrase);
        if (passPhraseResult == SUCCESS_CODE_REGEX_MATCH) {
            if (confirmedPassPhraseResult == SUCCESS_CODE_REGEX_MATCH) {
                if (passPhrase.compareTo(confirmedPassPhrase) == 0) {
                    return SUCCESS_CODE_PASSWORDS_MATCH;
                } else {
                    return ERROR_CODE_PASSWORD_NO_MATCH;
                }
            } else {
                return confirmedPassPhraseResult;
            }
        } else {
            return passPhraseResult;
        }
    }

    public int validateName(String name) {
        if (name.trim().isEmpty()) {
            return ERROR_CODE_EMPTY_STRING;
        } else {
            String nameRegex = "^[\\p{L} .'-]+$";
            Pattern patternName = Pattern.compile(nameRegex);
            Matcher matcherName = patternName.matcher(name);

            if (!matcherName.matches()) {
                return ERROR_CODE_REGEX_NO_MATCH;
            } else {
                return SUCCESS_CODE_REGEX_MATCH;
            }
        }
    }
}
