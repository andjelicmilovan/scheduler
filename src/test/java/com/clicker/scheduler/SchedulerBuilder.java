package com.clicker.scheduler;

import com.clicker.scheduler.domain.dto.SchedulerDto;
import java.util.Collections;
import java.util.List;

public class SchedulerBuilder {
    public static List<String> getIds() {
        return Collections.singletonList("1");
    }

    public static SchedulerDto getDto() {
        SchedulerDto dto = new SchedulerDto();
        dto.setId("1");
        return dto;
    }
}