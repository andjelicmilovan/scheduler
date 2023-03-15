package com.clicker.scheduler.domain.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class SchedulerDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String description;
    private String cronExpression;
    private String projectId;
    private String username;
    private Date nextFireTime;
    private Date lastFireTime;
}