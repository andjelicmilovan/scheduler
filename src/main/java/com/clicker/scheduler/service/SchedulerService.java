package com.clicker.scheduler.service;

import com.clicker.scheduler.domain.dto.JobDescriptor;
import com.clicker.scheduler.domain.dto.SchedulerDto;
import java.util.List;
import java.util.Optional;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

public interface SchedulerService {

    List<JobDescriptor> findAllJobs();

    List<SchedulerDto> getSchedulerList();

    Optional<JobDetail> updateJob(String group, String name, JobDescriptor descriptor);

    void resumeJob(SchedulerDto schedulerDto) throws SchedulerException;

    Object createJob(SchedulerDto schedulerDto);

    void deleteJob(SchedulerDto schedulerDto) throws SchedulerException;

    void pauseJob(SchedulerDto schedulerDto) throws SchedulerException;
}
