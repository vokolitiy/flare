package eu.flare.config.seed;

import eu.flare.model.TaskProgress;
import eu.flare.repository.task.TaskProgressRepository;

import java.util.List;
import java.util.Optional;

public class TaskProgressSeeder extends DataSeeder {

    public TaskProgressSeeder(TaskProgressRepository repository) {
        super(repository);
    }

    @Override
    public void createDataIfNotExists(List<String> data) {
        TaskProgressRepository taskProgressRepository = (TaskProgressRepository) repository;
        data.forEach(item -> {
            Optional<TaskProgress> taskProgressOptional = taskProgressRepository.findByName(item);
            if (taskProgressOptional.isEmpty()) {
                TaskProgress taskProgress = new TaskProgress();
                taskProgress.setName(item);
                taskProgressRepository.save(taskProgress);
            }
        });
    }
}
