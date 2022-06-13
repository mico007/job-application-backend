package com.simple_job_app_system_ws.service.impl;

import com.simple_job_app_system_ws.exceptions.JobApplServiceException;
import com.simple_job_app_system_ws.io.entity.UserEntity;
import com.simple_job_app_system_ws.io.repository.UserRepository;
import com.simple_job_app_system_ws.service.UserService;
import com.simple_job_app_system_ws.shared.Utils;
import com.simple_job_app_system_ws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {

        if(userRepository.findByEmail(userDto.getEmail()) != null) throw new JobApplServiceException("Record already exists");

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDto, userEntity);

        String userPublicId = utils.generateUserId(30);
        userEntity.setUserId(userPublicId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        UserEntity storedUserDetails = userRepository.save(userEntity);


        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, returnValue);

        return returnValue;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null) throw new JobApplServiceException("User with " + email + "not found");

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public UserDto loginUser(UserDto userDto) {

        UserEntity userEntity = userRepository.findByEmail(userDto.getEmail());

        if(userEntity == null) throw new JobApplServiceException("Invalid Username");

        if (
                !bCryptPasswordEncoder.matches(userDto.getPassword(), userEntity.getEncryptedPassword())
        ) throw new JobApplServiceException("Invalid Password");



        String token = utils.generateToken(userEntity.getUserId());

        userDto.setToken(token);
        userDto.setFullName(userEntity.getFullName());
        userDto.setUserId(userEntity.getUserId());

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userDto, returnValue);


        return returnValue;

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null) throw new JobApplServiceException("User with " + email + "not found");

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
