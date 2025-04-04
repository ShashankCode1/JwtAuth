package com.jwt_auth.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class UtilService {

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
    private final DateTimeFormatter formatter;

    public UtilService() {
        this.formatter = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.systemDefault());
    }

    public String getCurrentDateAndTime() {
        return formatter.format(Instant.now());
    }
}
