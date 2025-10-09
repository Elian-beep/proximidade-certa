package com.elian.proximidade_certa.services.exceptions;

public class GeocodingException extends RuntimeException{
    public GeocodingException(String msg) {
        super(msg);
    }
}
