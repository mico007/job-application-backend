package com.simple_job_app_system_ws.ui.controller;

import com.simple_job_app_system_ws.exceptions.JobApplServiceException;
import com.simple_job_app_system_ws.service.UserService;
import com.simple_job_app_system_ws.shared.dto.UserDto;
import com.simple_job_app_system_ws.ui.model.request.UserDetailsRequestModel;
import com.simple_job_app_system_ws.ui.model.request.UserLoginRequestModel;
import com.simple_job_app_system_ws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<UserRest> createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception
    {

        UserRest returnValue = new UserRest();

        if(userDetails.getFullName().isEmpty()) throw new JobApplServiceException("Invalid full name passed, please check your data.");
        if(userDetails.getEmail().isEmpty()) throw new JobApplServiceException("Invalid email passed, please check your data.");
        if(userDetails.getPassword().isEmpty()) throw new JobApplServiceException("Invalid password passed, please check your data.");

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);

        return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
    }

    @PostMapping(path = "/login-user",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<UserRest> userLogin(@RequestBody UserLoginRequestModel loginDetails) throws Exception
    {

        UserRest returnValue = new UserRest();

        if(loginDetails.getEmail().isEmpty()) throw new JobApplServiceException("Invalid email passed, please check your data.");
        if(loginDetails.getPassword().isEmpty()) throw new JobApplServiceException("Invalid password passed, please check your data.");

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(loginDetails, userDto);

        UserDto loginUser = userService.loginUser(userDto);
        BeanUtils.copyProperties(loginUser, returnValue);

        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

}
