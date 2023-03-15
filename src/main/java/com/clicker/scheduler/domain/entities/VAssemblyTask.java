package com.clicker.scheduler.domain.entities;

import com.c9weather.domain.C9FileDef;
import com.c9weather.domain.assembly.TaskStatusEnum;
import com.c9weather.domain.assembly.VideoAssemblyTask;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "video_assembly_task")
@Data
public class VAssemblyTask {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "creator")
    private String creator;

    @Column(name = "creation_date")
    private Long creationDate;

    @Column(name = "modifier")
    private String modifier;

    @Column(name = "modification_date")
    private Long modificationDate;

    @Column(name = "project_id")
    private String projectId;

    @Column(name = "project_version_id")
    private String projectVersionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatusEnum status;

    @Column(name = "error_msg")
    private String errorMsg;

    @Column(name = "sent_to_queue")
    private Boolean sentToQueue;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "output_video_json", columnDefinition = "json")
    private C9FileDef jsonOutputVideo;

    @Column(name = "duration_in_ms")
    private Long durationInMS;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "object_json", columnDefinition = "json")
    private VideoAssemblyTask jsonVideoAssemblyTask;

    @Column(name = "percentage_finished")
    private Double percentageFinished;

    public VAssemblyTask() {
    }

    public VAssemblyTask(VideoAssemblyTask videoAssemblyTask) {
        this.id = videoAssemblyTask.getId().toString();
        this.jsonVideoAssemblyTask = videoAssemblyTask;
        this.creator = videoAssemblyTask.getCreatedBy();
        this.creationDate = videoAssemblyTask.getCreationTime();
        this.percentageFinished = videoAssemblyTask.getPercentageFinished();
        this.jsonOutputVideo = videoAssemblyTask.getOutputVideoDef();
        this.durationInMS = videoAssemblyTask.getDurationOfVideoInMS();
        this.errorMsg = videoAssemblyTask.getErrorMessage();
        this.modificationDate = videoAssemblyTask.getModificationTime();
        this.modifier = videoAssemblyTask.getModifiedBy();
        this.name = videoAssemblyTask.getName();
        this.projectId = videoAssemblyTask.getProjectDefId().toString();
        this.projectVersionId = videoAssemblyTask.getProjectDefVersionId().toString();
        this.status = videoAssemblyTask.getTaskStatus();
        this.sentToQueue = videoAssemblyTask.getRequestSentToQueue();
    }
}
