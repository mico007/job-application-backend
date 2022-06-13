package com.simple_job_app_system_ws.security;

import com.simple_job_app_system_ws.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 3600000; // 1h
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static final String SUBMITTING_APPLICATION_URL = "/applications/create-application";
    public static final String UPLOADING_FILE_URL = "/applications/uploadFile";
    public static final String DOWNLOAD_FILE_URL = "/applications/downloadFile/*";
    public static final String LOGIN_URL = "/users/login-user";

    public static String getTokenSecret(){
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");
        return appProperties.getTokenSecret();
    }
}
