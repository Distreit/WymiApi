package com.hak.wymi.test;

import org.springframework.http.MediaType;

import java.nio.charset.Charset;

public class TestUtils {
    public static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
}
