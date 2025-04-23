package eu.flare.controller.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.model.TaskPriority;
import eu.flare.service.task.TaskPriorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/taskPriorities")
public class TaskPriorityController {

    private final TaskPriorityService taskPriorityService;

    @Autowired
    public TaskPriorityController(TaskPriorityService taskPriorityService) {
        this.taskPriorityService = taskPriorityService;
    }

    @GetMapping
    public ResponseEntity<Object> findTaskPriorities() {
        List<TaskPriority> priorities = taskPriorityService.findPriorities();
        if (priorities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TaskPrioritiesNotFoundResponse("Task priories not found"));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new TaskPrioritiesResponse(priorities));
        }
    }

    private record TaskPrioritiesNotFoundResponse(@JsonProperty("error") String error){}
    private record TaskPrioritiesResponse(@JsonProperty("priorities") List<TaskPriority> priorities) {}
}
