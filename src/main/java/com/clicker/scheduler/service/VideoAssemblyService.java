package com.clicker.scheduler.service;

import com.c9weather.domain.C9ProjectDef;
import com.c9weather.domain.SceneDef;
import com.c9weather.domain.assembly.TaskStatusEnum;
import com.c9weather.domain.assembly.VideoAssemblyTask;
import com.c9weather.domain.assembly.VideoAssemblyTaskRequest;
import com.clicker.scheduler.config.RabbitMQConfiguration;
import com.clicker.scheduler.domain.entities.VAssemblyTask;
import com.clicker.scheduler.repository.VAssemblyTaskRepository;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VideoAssemblyService {
    private final RabbitMQConfiguration rabbitMQConfiguration;
    private final MessageService messageService;
    private final VAssemblyTaskRepository vAssemblyTaskRepository;

    public VideoAssemblyService(RabbitMQConfiguration rabbitMQConfiguration, MessageService messageService, VAssemblyTaskRepository vAssemblyTaskRepository) {
        this.rabbitMQConfiguration = rabbitMQConfiguration;
        this.messageService = messageService;
        this.vAssemblyTaskRepository = vAssemblyTaskRepository;
    }

//    public VideoAssemblyTask initiateVATask(String projectId, String userName) {
//        List<UUID> sceneDefIds = new ArrayList<>();
//            sceneDefIds.add(UUID.fromString(projectId));
//            return initiateNewVATask(request.getProjectDef(), "scene", sceneDefIds, userName);
//    }

    private VideoAssemblyTask initiateNewVATask(C9ProjectDef projectDef, String type, List<UUID> sceneDefIds, String userName) {
        String random = UUID.randomUUID().toString();
        VideoAssemblyTask videoAssemblyTask = new VideoAssemblyTask();
        videoAssemblyTask.setCreatedBy(userName);
        videoAssemblyTask.setType(type);
        videoAssemblyTask.setCreationTime(new Date().getTime());
        if (type.equalsIgnoreCase("scene")) {
            List<SceneDef> s = projectDef.getSceneDefs().stream().filter(sceneDef -> sceneDef.getId().equals(sceneDefIds.get(0))).collect(Collectors.toList());
            String name = s.isEmpty() ? projectDef.getName() : s.get(0).getName();
            videoAssemblyTask.setName(name + " - " + random.substring(0, 8));
        } else {
            videoAssemblyTask.setName(projectDef.getName() + " - " + random.substring(0, 8));
        }

        videoAssemblyTask.setProjectDefId(projectDef.getId());
        videoAssemblyTask.setProjectDefVersionId(projectDef.getVersionId());
        videoAssemblyTask.setPercentageFinished(0D);
        videoAssemblyTask.setResponseNum(0);
        VideoAssemblyTaskRequest taskRequest = new VideoAssemblyTaskRequest();
        taskRequest.setId(videoAssemblyTask.getId());
        taskRequest.setName(videoAssemblyTask.getName());
        taskRequest.setCreatedBy(userName);
        taskRequest.setCreationTime(new Date().getTime());
        taskRequest.setCallbackQueue(rabbitMQConfiguration.getAssemblerResultsQueueName());
        taskRequest.setProjectDef(projectDef);
        taskRequest.getSceneDefIds().addAll(sceneDefIds);
        videoAssemblyTask.setTaskRequest(taskRequest);
        try {
            messageService.sendMessageToVideoAssembly(taskRequest);
            videoAssemblyTask.setRequestSentToQueue(true);
            videoAssemblyTask.setTaskStatus(TaskStatusEnum.INITIATED);
        } catch (Exception e) {
            videoAssemblyTask.setErrorMessage(e.getMessage());
            videoAssemblyTask.setRequestSentToQueue(false);
            videoAssemblyTask.setTaskStatus(TaskStatusEnum.FAILED);
        }
        VAssemblyTask vat = new VAssemblyTask(videoAssemblyTask);
        vAssemblyTaskRepository.save(vat);
        log.info("New task id {} is saved; task request has sendToQueue flag set to: {}", videoAssemblyTask.getId(), videoAssemblyTask.getRequestSentToQueue());
        return vat.getJsonVideoAssemblyTask();
    }
}
