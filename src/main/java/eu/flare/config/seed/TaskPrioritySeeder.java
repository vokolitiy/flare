package eu.flare.config.seed;

import eu.flare.model.TaskPriority;
import eu.flare.repository.task.TaskPriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Component
public class TaskPrioritySeeder {

    private TaskPriorityRepository taskPriorityRepository;

    @Autowired
    public TaskPrioritySeeder(TaskPriorityRepository taskPriorityRepository) {
        this.taskPriorityRepository = taskPriorityRepository;
    }

    public void createDataIfNotExists(List<String> data) {
        data.forEach(item -> {
            Optional<TaskPriority> taskPriorityOptional = taskPriorityRepository.findByName(item);
            if (taskPriorityOptional.isEmpty()) {
                TaskPriority taskPriority = new TaskPriority();
                taskPriority.setName(item);
                taskPriorityRepository.save(taskPriority);
            }
        });
    }
}
