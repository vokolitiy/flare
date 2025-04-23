package eu.flare.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.exceptions.notfound.TaskNotFoundException;
import eu.flare.model.Task;
import eu.flare.model.dto.rename.RenameTaskDto;
import eu.flare.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Object> findTask(@RequestParam("name") String name) {
        Optional<Task> taskOptional = taskService.findTask(name);
        return taskOptional.<ResponseEntity<Object>>map(task -> ResponseEntity.status(HttpStatus.OK)
                .body(new TaskResponse(task))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new TaskNotFoundResponse("Task not found")));
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<Object> renameTask(@PathVariable("id") long id, @RequestBody RenameTaskDto dto) {
        try {
            Task task = taskService.renameTask(id, dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new TaskUpdatedResponse(task));
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TaskNotFoundResponse(e.getMessage()));
        }
    }

    private record TaskNotFoundResponse(@JsonProperty("error") String error){}
    private record TaskResponse(@JsonProperty("task") Task task) {}
    private record TaskUpdatedResponse(@JsonProperty("task") Task task) {}
}
