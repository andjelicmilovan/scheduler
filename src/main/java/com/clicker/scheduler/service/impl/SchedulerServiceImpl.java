package com.clicker.scheduler.service.impl;

import com.clicker.scheduler.domain.dto.JobDescriptor;
import com.clicker.scheduler.domain.dto.SchedulerDto;
import com.clicker.scheduler.domain.dto.TriggerDescriptor;
import com.clicker.scheduler.service.SchedulerService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.quartz.JobKey.jobKey;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    private final static String groupName = "scheduler";
    private final static String groupNameTrigger = "scheduler";
    private final Scheduler scheduler;

    public List<JobDescriptor> findAllJobs() {
        List<JobDescriptor> jobList = new ArrayList<>();
        try {
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String name = jobKey.getName();
                    String group = jobKey.getGroup();
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey(name, group));
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
                    jobList.add(JobDescriptor.buildDescriptor(jobDetail, triggers, scheduler));
                }
            }
        } catch (SchedulerException e) {
            log.error("Could not find all jobs due to error - {}", e.getLocalizedMessage());
        }
        return jobList;
    }

    @Override
    public List<SchedulerDto> getSchedulerList() {
        List<JobDescriptor> jobDescriptors = findAllJobs();
        List<SchedulerDto> schedulerDtos = new ArrayList<>();

        jobDescriptors.forEach(
                jobDescriptor -> {
                    List<TriggerDescriptor> triggersForJob = jobDescriptor.getTriggerDescriptors();
                    Map<String, Object> map = jobDescriptor.getData();

                    SchedulerDto schedulerDto = map.get("schedulerDto") != null ? (SchedulerDto) map.get("schedulerDto") : new SchedulerDto();
                    getInformationsAboutFires(triggersForJob, schedulerDto);
                    schedulerDtos.add(schedulerDto);
                }
        );
        return schedulerDtos;
    }

    private void getInformationsAboutFires(List<TriggerDescriptor> triggersForJob, SchedulerDto schedulerDto) {
        for (TriggerDescriptor triggerDescriptor : triggersForJob) {
            String triggerName = triggerDescriptor.getName();
            String triggerGroup = triggerDescriptor.getGroup();
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
            try {
                Trigger trigger = scheduler.getTrigger(triggerKey);
                Date nextFireTime = trigger.getNextFireTime();
                schedulerDto.setNextFireTime(nextFireTime);
                schedulerDto.setLastFireTime(trigger.getPreviousFireTime());
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

    public Optional<JobDetail> updateJob(String group, String name, JobDescriptor descriptor) {
        try {
            JobDetail oldJobDetail = scheduler.getJobDetail(jobKey(name, group));
            if (Objects.nonNull(oldJobDetail)) {
                JobDataMap jobDataMap = oldJobDetail.getJobDataMap();
                for (Map.Entry<String, Object> entry : descriptor.getData().entrySet()) {
                    jobDataMap.put(entry.getKey(), entry.getValue());
                }
                JobBuilder jb = oldJobDetail.getJobBuilder();
                JobDetail newJobDetail = jb.usingJobData(jobDataMap).storeDurably().build();
                scheduler.addJob(newJobDetail, true);
                log.info("Updated job with key - {}", newJobDetail.getKey());
                return Optional.of(newJobDetail);
            }
            log.warn("Could not find job with key - {}.{} to update", group, name);
        } catch (SchedulerException e) {
            log.error("Could not find job with key - {}.{} to update due to error - {}", group, name, e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    public void pauseJob(SchedulerDto schedulerDto) throws SchedulerException {
        log.info("Paused job with key - {}.{}", schedulerDto.getName(), groupNameTrigger);
        scheduler.pauseJob(jobKey(schedulerDto.getName(), groupNameTrigger));
    }

    @Override
    public void resumeJob(SchedulerDto schedulerDto) throws SchedulerException {
        log.info("Resumed job with key - {}.{}", schedulerDto.getName(), groupNameTrigger);
        scheduler.resumeJob(jobKey(schedulerDto.getName(), groupNameTrigger));
    }

    @Override
    public Object createJob(SchedulerDto schedulerDto) {

        JobDescriptor descriptor = new JobDescriptor();
        descriptor.setGroup(groupName);
        descriptor.setName(schedulerDto.getName());
        List<TriggerDescriptor> triggerDescriptors = new ArrayList<>();
        TriggerDescriptor triggerDescriptor = new TriggerDescriptor();
        triggerDescriptor.setCron(schedulerDto.getCronExpression());
        triggerDescriptor.setName(schedulerDto.getName() + "_trigger");
        triggerDescriptor.setGroup(groupNameTrigger);
        triggerDescriptor.setTriggerState(null);
        triggerDescriptors.add(0, triggerDescriptor);
        descriptor.setTriggerDescriptors(triggerDescriptors);
        Map<String, Object> projectMap = new HashMap<>();
        projectMap.put("schedulerDto", schedulerDto);
        descriptor.setData(projectMap);

        JobDetail jobDetail = descriptor.buildJobDetail();
        Set<Trigger> triggersForJob = descriptor.buildTriggers();
        log.info("About to save job with key - {}", jobDetail.getKey());
        try {
            scheduler.scheduleJob(jobDetail, triggersForJob, false);
            log.info("Job with key - {} saved successfully", jobDetail.getKey());
        } catch (SchedulerException e) {
            log.error("Could not save job with key - {} due to error - {}", jobDetail.getKey(), e.getLocalizedMessage());
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
        return descriptor;
    }

    @Override
    public void deleteJob(SchedulerDto schedulerDto) throws SchedulerException {
        log.info("Deleted job with key - {}.{}", schedulerDto.getName(), groupNameTrigger);
        scheduler.deleteJob(jobKey(schedulerDto.getName(), groupNameTrigger));

    }
}
