package com.clicker.scheduler.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

@Slf4j
public class Action implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            ApplicationContext applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");

            ObjectMapper objectMapper = applicationContext.getBean(ObjectMapper.class);

            log.info("Action ** {} ** starting @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());
            log.info("Stored Data {}", objectMapper.writeValueAsString(context.getJobDetail().getJobDataMap()));
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }

        log.info("Action ** {} ** completed.  Next action scheduled @ {}", context.getJobDetail().getKey().getName(), context.getNextFireTime());
    }
}