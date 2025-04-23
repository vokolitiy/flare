package eu.flare.config.seed;

import eu.flare.model.TaskResolution;
import eu.flare.repository.task.TaskResolutionRepository;

import java.util.List;
import java.util.Optional;

public class TaskResolutionSeeder extends DataSeeder {

    public TaskResolutionSeeder(TaskResolutionRepository repository) {
        super(repository);
    }

    @Override
    public void createDataIfNotExists(List<String> data) {
        TaskResolutionRepository taskResolutionRepository = (TaskResolutionRepository) repository;
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
