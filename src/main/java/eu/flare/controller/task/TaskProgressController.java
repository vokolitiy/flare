package eu.flare.controller.task;

import eu.flare.model.TaskProgress;
import eu.flare.model.response.Responses;
import eu.flare.service.task.TaskProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/taskProgress")
public class TaskProgressController {

    private final TaskProgressService taskProgressService;

    @Autowired
    public TaskProgressController(TaskProgressService taskProgressService) {
        this.taskProgressService = taskProgressService;
    }

    @GetMapping
    public ResponseEntity<Object> findTaskProgresses() {
        List<TaskProgress> taskProgresses = taskProgressService.findTaskProgresses();
        if (taskProgresses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.TaskProgressesNotFoundResponse("Task progresses not found"));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.TaskProgressesResponse(taskProgresses));
        }
    }
}
