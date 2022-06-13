package com.simple_job_app_system_ws.ui.controller;

import com.simple_job_app_system_ws.exceptions.JobApplServiceException;
import com.simple_job_app_system_ws.service.JobApplicationService;
import com.simple_job_app_system_ws.shared.dto.JobApplicationDto;
import com.simple_job_app_system_ws.ui.model.request.ApplicationDetailsRequestModel;
import com.simple_job_app_system_ws.ui.model.response.JobApplicationRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/applications")
public class JobApplicationController {

    @Autowired
    JobApplicationService jobApplService;

    @PostMapping(path = "/create-application",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<JobApplicationRest> createApplication(@RequestBody ApplicationDetailsRequestModel applicationDetails) throws Exception
    {

        JobApplicationRest returnValue = new JobApplicationRest();

        if(applicationDetails.getFirstname().isEmpty()) throw new JobApplServiceException("Invalid fist name passed, please check your data.");
        if(applicationDetails.getLastname().isEmpty()) throw new JobApplServiceException("Invalid last name passed, please check your data.");
        if(applicationDetails.getEmail().isEmpty()) throw new JobApplServiceException("Invalid email passed, please check your data.");
        if(applicationDetails.getPhone().isEmpty()) throw new JobApplServiceException("Invalid mobile number passed, please check your data.");
        if(applicationDetails.getAddress().isEmpty()) throw new JobApplServiceException("Invalid address passed, please check your data.");
        if(applicationDetails.getCity().isEmpty()) throw new JobApplServiceException("Invalid city passed, please check your data.");
        if(applicationDetails.getProvince().isEmpty()) throw new JobApplServiceException("Invalid province passed, please check your data.");
        if(applicationDetails.getCountry().isEmpty()) throw new JobApplServiceException("Invalid country passed, please check your data.");

        JobApplicationDto jobApplDto = new JobApplicationDto();
        BeanUtils.copyProperties(applicationDetails, jobApplDto);

        JobApplicationDto submittedApplication = jobApplService.createApplication(jobApplDto);
        BeanUtils.copyProperties(submittedApplication, returnValue);

        return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<JobApplicationRest>> getAllApplications(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "10") int limit)
    {

        List<JobApplicationRest> returnValue = new ArrayList<>();

        List<JobApplicationDto> jobApplications = jobApplService.getAllApplications(page, limit);

        for(JobApplicationDto jobApplDto : jobApplications){
            JobApplicationRest jobApplModel = new JobApplicationRest();
            BeanUtils.copyProperties(jobApplDto, jobApplModel);
            returnValue.add(jobApplModel);
        }

        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<JobApplicationRest> getApplicationById(@PathVariable String id){

        JobApplicationRest returnValue = new JobApplicationRest();

        JobApplicationDto jobApplDto = jobApplService.getApplicationByPublicId(id);
        BeanUtils.copyProperties(jobApplDto, returnValue);

        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    @PutMapping(path = "/status-on/{applicationId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<JobApplicationRest> statusOnHandler(@PathVariable String applicationId)
    {
        JobApplicationRest returnValue = new JobApplicationRest();

        JobApplicationDto updatedApplication = jobApplService.updateStatus(applicationId);
        BeanUtils.copyProperties(updatedApplication, returnValue);

        return new ResponseEntity<>(returnValue, HttpStatus.OK);

    }

    @PutMapping(path = "/status-off/{applicationId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<JobApplicationRest> statusOffHandler(@PathVariable String applicationId)
    {
        JobApplicationRest returnValue = new JobApplicationRest();

        JobApplicationDto updatedApplication = jobApplService.statusOffUpdate(applicationId);
        BeanUtils.copyProperties(updatedApplication, returnValue);

        return new ResponseEntity<>(returnValue, HttpStatus.OK);

    }

    @PostMapping(path = "/uploadFile")
    public JobApplicationRest uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("app_id") String app_id) {


        JobApplicationRest returnValue = new JobApplicationRest();

        JobApplicationDto uploadedFile = jobApplService.storeFile(file, app_id);
        BeanUtils.copyProperties(uploadedFile, returnValue);


        return returnValue;
    }

    @GetMapping("/downloadFile/{applicationId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String applicationId) {

        // Load file from database
        JobApplicationDto downloadedFile = jobApplService.getFile(applicationId);

        //byte[] data=downloadedFile.getData().getBytes();

        //converting string data into Base64 file
        byte[] decodedString = Base64.getDecoder().decode(downloadedFile.getData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(new ByteArrayResource(decodedString));
    }



}
