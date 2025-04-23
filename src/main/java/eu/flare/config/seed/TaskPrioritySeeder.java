package eu.flare.config.seed;

import eu.flare.model.TaskPriority;
import eu.flare.repository.task.TaskPriorityRepository;

import java.util.List;
import java.util.Optional;

public class TaskPrioritySeeder extends DataSeeder {

    public TaskPrioritySeeder(TaskPriorityRepository repository) {
        super(repository);
    }

    @Override
    public void createDataIfNotExists(List<String> data) {
        TaskPriorityRepository taskPriorityRepository = (TaskPriorityRepository) repository;
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
