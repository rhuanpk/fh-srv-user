package com.hack.user.core.entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email {

    private String email;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public Email(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email inválido");
        }
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }
}
