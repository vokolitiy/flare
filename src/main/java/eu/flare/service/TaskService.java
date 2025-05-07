package eu.flare.service;

import eu.flare.exceptions.notfound.TaskNotFoundException;
import eu.flare.model.Task;
import eu.flare.model.dto.rename.RenameTaskDto;
import eu.flare.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Optional<Task> findTask(String name) {
        return taskRepository.findByName(name);
    }

    public Task renameTask(long id, RenameTaskDto dto) throws TaskNotFoundException {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            throw new TaskNotFoundException("Task not found");
        }

        Task task = taskOptional.get();
        task.setName(dto.name());
        return taskRepository.save(task);
    }
}
