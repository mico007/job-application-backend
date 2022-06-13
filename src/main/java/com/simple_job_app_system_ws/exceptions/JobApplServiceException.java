package com.simple_job_app_system_ws.exceptions;

public class JobApplServiceException extends RuntimeException{

    private static final long serialVersionUID = 9162518725984564932L;

    public JobApplServiceException(String message){
        super(message);
    }
}
