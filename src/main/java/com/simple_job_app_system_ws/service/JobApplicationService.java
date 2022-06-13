package com.simple_job_app_system_ws.service;

import com.simple_job_app_system_ws.shared.dto.JobApplicationDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobApplicationService {
    JobApplicationDto createApplication(JobApplicationDto jobApplicationDto);

    JobApplicationDto getApplicationByPublicId(String id);

    List<JobApplicationDto> getAllApplications(int page, int limit);

    JobApplicationDto updateStatus(String applicationId);

    JobApplicationDto statusOffUpdate(String applicationId);

    JobApplicationDto storeFile(MultipartFile file, String applicationId);

    JobApplicationDto getFile(String applicationId);
}
