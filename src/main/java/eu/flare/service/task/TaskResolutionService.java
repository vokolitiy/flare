package eu.flare.service.task;

import eu.flare.model.TaskResolution;
import eu.flare.repository.task.TaskResolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskResolutionService {

    private final TaskResolutionRepository taskResolutionRepository;

    @Autowired
    public TaskResolutionService(TaskResolutionRepository taskResolutionRepository) {
        this.taskResolutionRepository = taskResolutionRepository;
    }

    public List<TaskResolution> findTaskResolutions() {
        return taskResolutionRepository.findAll();
    }
}
