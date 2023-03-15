package com.spring.quartz.controller;

import com.spring.quartz.domain.dto.SchedulerDto;
import com.spring.quartz.model.JobDescriptor;
import com.spring.quartz.service.SchedulerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduler")
@RequiredArgsConstructor
@Slf4j
public class SchedulerController {
    private final SchedulerService schedulerService;

    @PostMapping
    public ResponseEntity<JobDescriptor> createScheduleJob(@RequestBody SchedulerDto schedulerDto) {
        log.debug("Rest endpoint to create scheduler job");

        return new ResponseEntity(schedulerService.createJob(schedulerDto), HttpStatus.CREATED);
    }

    @GetMapping(path = "/jobs")
    public ResponseEntity<List<SchedulerDto>> findAllJobs() {
        log.debug("Rest endpoint to get all scheduled jobs");

        return ResponseEntity.ok(schedulerService.getSchedulerList());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteJob(@RequestBody SchedulerDto schedulerDto) throws SchedulerException {
        log.debug("Rest endpoint to delete scheduled job");

        schedulerService.deleteJob(schedulerDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/pause")
    public ResponseEntity<Void> pauseJob(@RequestBody SchedulerDto schedulerDto) throws SchedulerException {
        log.debug("Rest endpoint to pause scheduled job");

        schedulerService.pauseJob(schedulerDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/resume")
    public ResponseEntity<Void> resumeJob(@RequestBody SchedulerDto schedulerDto) throws SchedulerException {
        log.debug("Rest endpoint to resume scheduled job");

        schedulerService.resumeJob(schedulerDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/groups/{group}/jobs/{name}")
    public ResponseEntity<JobDetail> updateJob(@PathVariable String group, @PathVariable String name, @RequestBody JobDescriptor descriptor) {
        return schedulerService.updateJob(group, name, descriptor).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
