package eu.flare.controller.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.model.TaskResolution;
import eu.flare.service.task.TaskResolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/taskResolution")
public class TaskResolutionController {

    private final TaskResolutionService taskResolutionService;

    @Autowired
    public TaskResolutionController(TaskResolutionService taskResolutionService) {
        this.taskResolutionService = taskResolutionService;
    }

    @GetMapping
    public ResponseEntity<Object> findTaskResolutions() {
        List<TaskResolution> taskResolutions = taskResolutionService.findTaskResolutions();
        if (taskResolutions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TaskResolutionsNotFoundResponse("Task resolutions not found"));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new TaskResolutionsResponse(taskResolutions));
        }
    }

    private record TaskResolutionsNotFoundResponse(@JsonProperty("error") String error){}
    private record TaskResolutionsResponse(@JsonProperty("resolutions") List<TaskResolution> resolutions) {}
}
