package eu.flare.config.seed;

import eu.flare.model.TaskResolution;
import eu.flare.repository.task.TaskResolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TaskResolutionSeeder {

    private TaskResolutionRepository taskResolutionRepository;

    @Autowired
    public TaskResolutionSeeder(TaskResolutionRepository taskResolutionRepository) {
        this.taskResolutionRepository = taskResolutionRepository;
    }

    public void createDataIfNotExists(List<String> data) {
        data.forEach(item -> {
            Optional<TaskResolution> taskResolutionOptional = taskResolutionRepository.findByName(item);
            if (taskResolutionOptional.isEmpty()) {
                TaskResolution taskResolution = new TaskResolution();
                taskResolution.setName(item);
                taskResolutionRepository.save(taskResolution);
            }
        });
    }
}
