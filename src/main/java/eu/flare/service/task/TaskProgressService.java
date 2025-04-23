package eu.flare.service.task;

import eu.flare.model.TaskProgress;
import eu.flare.repository.task.TaskProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskProgressService {

    private final TaskProgressRepository taskProgressRepository;

    @Autowired
    public TaskProgressService(TaskProgressRepository taskProgressRepository) {
        this.taskProgressRepository = taskProgressRepository;
    }

    public List<TaskProgress> findTaskProgresses() {
        return taskProgressRepository.findAll();
    }
}
