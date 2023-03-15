package com.spring.quartz.service;

import com.spring.quartz.domain.dto.SchedulerDto;
import com.spring.quartz.model.JobDescriptor;
import java.util.List;
import java.util.Optional;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

public interface SchedulerService {

    JobDescriptor createJob(String group, JobDescriptor descriptor);

    List<JobDescriptor> findAllJobs();

    List<SchedulerDto> getSchedulerList();

    Optional<JobDescriptor> findJob(String group, String name);

    Optional<JobDetail> updateJob(String group, String name, JobDescriptor descriptor);

    void deleteJob(String group, String name);

    void pauseJob(String group, String name);

    void resumeJob(String group, String name);

    void resumeJob(SchedulerDto schedulerDto) throws SchedulerException;

    Object createJob(SchedulerDto schedulerDto);

    void deleteJob(SchedulerDto schedulerDto) throws SchedulerException;

    void pauseJob(SchedulerDto schedulerDto) throws SchedulerException;
}
