package com.clicker.scheduler.domain.dto;

import com.clicker.scheduler.action.Action;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import static org.quartz.JobBuilder.newJob;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(builder = JobDescriptor.JobDescriptorBuilder.class)
public class JobDescriptor {

    @JsonProperty("name")
    private String name;

    @JsonProperty("group")
    private String group;

    @JsonProperty("data")
    private Map<String, Object> data = new LinkedHashMap<>();

    @JsonProperty("triggers")
    private List<TriggerDescriptor> triggerDescriptors = new ArrayList<>();

    public static JobDescriptor buildDescriptor(JobDetail jobDetail, List<? extends Trigger> triggersOfJob, Scheduler scheduler) throws SchedulerException {
        List<TriggerDescriptor> triggerDescriptors = new ArrayList<>();
        for (Trigger trigger : triggersOfJob) {
            triggerDescriptors.add(TriggerDescriptor.buildDescriptor(trigger, scheduler.getTriggerState(trigger.getKey())));
        }
        return JobDescriptor.builder()
                .name(jobDetail.getKey().getName())
                .group(jobDetail.getKey().getGroup())
                .data(jobDetail.getJobDataMap())
                .triggerDescriptors(triggerDescriptors).build();
    }

    public JobDescriptor setName(final String name) {
        this.name = name;
        return this;
    }

    public JobDescriptor setData(final Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public JobDescriptor setTriggerDescriptors(final List<TriggerDescriptor> triggerDescriptors) {
        this.triggerDescriptors = triggerDescriptors;
        return this;
    }

    @JsonIgnore
    public Set<Trigger> buildTriggers() {
        Set<Trigger> triggers = new LinkedHashSet<>();
        for (TriggerDescriptor triggerDescriptor : triggerDescriptors) {
            triggers.add(triggerDescriptor.buildTrigger());
        }
        return triggers;
    }

    public JobDetail buildJobDetail() {
        JobDataMap jobDataMap = data == null ? new JobDataMap(new LinkedHashMap<>()) : new JobDataMap(data);
        jobDataMap.put("jobGroup", group);
        jobDataMap.put("jobName", name);

        return newJob(Action.class)
                .withIdentity(getName(), getGroup())
                .usingJobData(jobDataMap)
                .build();
    }
}