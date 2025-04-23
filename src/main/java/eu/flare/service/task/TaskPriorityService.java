package eu.flare.service.task;

import eu.flare.model.TaskPriority;
import eu.flare.repository.task.TaskPriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskPriorityService {

    private final TaskPriorityRepository taskPriorityRepository;

    @Autowired
    public TaskPriorityService(TaskPriorityRepository taskPriorityRepository) {
        this.taskPriorityRepository = taskPriorityRepository;
    }

    public List<TaskPriority> findPriorities() {
        return taskPriorityRepository.findAll();
    }
}
