package eu.flare.config.seed;

import eu.flare.model.TaskProgress;
import eu.flare.repository.task.TaskProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Component
public class TaskProgressSeeder {

    private TaskProgressRepository taskProgressRepository;

    @Autowired
    public TaskProgressSeeder(TaskProgressRepository taskProgressRepository) {
        this.taskProgressRepository = taskProgressRepository;
    }

    public void createDataIfNotExists(List<String> data) {
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
