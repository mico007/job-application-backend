package com.simple_job_app_system_ws.service.impl;

import com.simple_job_app_system_ws.exceptions.JobApplServiceException;
import com.simple_job_app_system_ws.io.entity.JobApplicationEntity;
import com.simple_job_app_system_ws.io.repository.JobApplicationRepository;
import com.simple_job_app_system_ws.service.JobApplicationService;
import com.simple_job_app_system_ws.shared.Utils;
import com.simple_job_app_system_ws.shared.dto.JobApplicationDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Transactional
@Service
public class JobApplServiceImpl implements JobApplicationService {

    @Autowired
    JobApplicationRepository jobApplRepository;

    @Autowired
    Utils utils;

    @Override
    public JobApplicationDto createApplication(JobApplicationDto jobApplicationDto) {

        JobApplicationEntity jobApplEntity = new JobApplicationEntity();
        BeanUtils.copyProperties(jobApplicationDto, jobApplEntity);

        String applicationPublicId = utils.generateApplicationId(30);
        jobApplEntity.setApplicationId(applicationPublicId);

        JobApplicationEntity submittedApplicationDetails = jobApplRepository.save(jobApplEntity);

        JobApplicationDto returnValue = new JobApplicationDto();
        BeanUtils.copyProperties(submittedApplicationDetails, returnValue);

        return returnValue;

    }

    @Override
    public JobApplicationDto getApplicationByPublicId(String id) {

        JobApplicationEntity jobApplEntity = jobApplRepository.findByApplicationId(id);

        if (jobApplEntity == null) throw new JobApplServiceException("Job application with ID: " + id + " not found.");

        JobApplicationDto returnValue = new JobApplicationDto();
        BeanUtils.copyProperties(jobApplEntity, returnValue);

        return returnValue;
    }

    @Override
    public List<JobApplicationDto> getAllApplications(int page, int limit) {

        List<JobApplicationDto> returnValue = new ArrayList<>();

        List<Sort.Order> orders = new ArrayList<>();

        Sort.Order order1 = new Sort.Order(Sort.Direction.DESC, "status");
        orders.add(order1);
        Sort.Order order3 = new Sort.Order(Sort.Direction.ASC, "firstname");
        orders.add(order3);
        Sort.Order order4 = new Sort.Order(Sort.Direction.ASC, "lastname");
        orders.add(order4);
//        Sort.Order order2 = new Sort.Order(Sort.Direction.ASC, "email");
//        orders.add(order2);
//        Sort.Order order5 = new Sort.Order(Sort.Direction.ASC, "phone");
//        orders.add(order5);
//        Sort.Order order6 = new Sort.Order(Sort.Direction.ASC, "address");
//        orders.add(order6);
//        Sort.Order order7 = new Sort.Order(Sort.Direction.ASC, "city");
//        orders.add(order7);
//        Sort.Order order8 = new Sort.Order(Sort.Direction.ASC, "province");
//        orders.add(order8);
//        Sort.Order order9 = new Sort.Order(Sort.Direction.ASC, "country");
//        orders.add(order9);

        if (page > 0) page -= 1;

        Pageable pageableRequest = PageRequest.of(page, limit, Sort.by(orders));

        Page<JobApplicationEntity> applicationsPage = jobApplRepository.findAll(pageableRequest);

        List<JobApplicationEntity> jobApplications = applicationsPage.getContent();

        for (JobApplicationEntity jobApplEntity : jobApplications) {
            JobApplicationDto jobApplDto = new JobApplicationDto();
            BeanUtils.copyProperties(jobApplEntity, jobApplDto);
            returnValue.add(jobApplDto);
        }

        return returnValue;
    }

    @Override
    public JobApplicationDto updateStatus(String applicationId) {

        JobApplicationEntity jobApplEntity = jobApplRepository.findByApplicationId(applicationId);

        if (jobApplEntity == null) throw new JobApplServiceException("Could not find application for this id.");

        jobApplEntity.setStatus("Passed");

        JobApplicationEntity updatedApplDetails = jobApplRepository.save(jobApplEntity);

        JobApplicationDto returnValue = new JobApplicationDto();
        BeanUtils.copyProperties(updatedApplDetails, returnValue);

        return returnValue;
    }

    @Override
    public JobApplicationDto statusOffUpdate(String applicationId) {
        JobApplicationEntity jobApplEntity = jobApplRepository.findByApplicationId(applicationId);

        if (jobApplEntity == null) throw new JobApplServiceException("Could not find application for this id.");

        jobApplEntity.setStatus("Dropped");

        JobApplicationEntity updatedApplDetails = jobApplRepository.save(jobApplEntity);

        JobApplicationDto returnValue = new JobApplicationDto();
        BeanUtils.copyProperties(updatedApplDetails, returnValue);

        return returnValue;
    }

    @Override
    public JobApplicationDto storeFile(MultipartFile file, String applicationId) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        JobApplicationEntity jobApplEntity = jobApplRepository.findByApplicationId(applicationId);

        if (jobApplEntity == null) throw new JobApplServiceException("Job Application Record cannot be found.");

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new JobApplServiceException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            //converting Base64 file into string
            String dataFile = new String(Base64.getEncoder().encode(file.getBytes()));
            jobApplEntity.setData(dataFile);

            JobApplicationEntity savedFile = jobApplRepository.save(jobApplEntity);

            JobApplicationDto returnValue = new JobApplicationDto();
            BeanUtils.copyProperties(savedFile, returnValue);

            return returnValue;
        } catch (IOException ex) {
            throw new JobApplServiceException("Could not store file " + fileName + ". Please try again!");
        }
    }

    @Override
    public JobApplicationDto getFile(String applicationId) {

        JobApplicationEntity jobApplicationEntity = jobApplRepository.findByApplicationId(applicationId);

        if (jobApplicationEntity == null) throw new JobApplServiceException("Job Application Record cannot be found.");

        JobApplicationDto returnValue = new JobApplicationDto();
        BeanUtils.copyProperties(jobApplicationEntity, returnValue);

        return returnValue;

    }

}
