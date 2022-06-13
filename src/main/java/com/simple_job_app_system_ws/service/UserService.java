package com.simple_job_app_system_ws.service;

import com.simple_job_app_system_ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);

    UserDto getUser(String email);

    UserDto loginUser(UserDto userDto);
}
