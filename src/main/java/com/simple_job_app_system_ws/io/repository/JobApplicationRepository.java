package com.simple_job_app_system_ws.io.repository;

import com.simple_job_app_system_ws.io.entity.JobApplicationEntity;
//import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends PagingAndSortingRepository<JobApplicationEntity, Long> {
    JobApplicationEntity findByApplicationId(String id);

}
