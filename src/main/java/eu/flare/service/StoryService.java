package eu.flare.service;

import eu.flare.exceptions.notfound.StoryNotFoundException;
import eu.flare.exceptions.conflicts.TasksNamesConflictException;
import eu.flare.model.*;
import eu.flare.model.dto.add.AddTaskDto;
import eu.flare.model.dto.rename.RenameStoryDto;
import eu.flare.repository.StoryRepository;
import eu.flare.repository.TaskRepository;
import eu.flare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StoryService {

    private final StoryRepository storyRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public StoryService(StoryRepository storyRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.storyRepository = storyRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Optional<Story> findStoryWithName(String name) {
        return storyRepository.findByName(name);
    }

    public Story createTasksForStory(long id, List<AddTaskDto> addTaskDtos) throws StoryNotFoundException, TasksNamesConflictException {
        Optional<Story> storyOptional = storyRepository.findById(id);
        if (storyOptional.isEmpty()) {
            throw new StoryNotFoundException("Story not found");
        } else {
            Story story = storyOptional.get();
            List<Task> storyTasks = story.getStoryTasks();
            if (storyTasks.isEmpty()) {
                return createStoryTasks(story, addTaskDtos);
            } else {
                List<String> providedTaskNames = addTaskDtos.stream().map(AddTaskDto::name).toList();
                List<String> existingTaskNames = storyTasks.stream().map(Task::getName).toList();
                List<String> combined = existingTaskNames.stream()
                        .filter(two -> providedTaskNames.stream().anyMatch(one -> one.equals(two)))
                        .toList();
                if (combined.isEmpty()) {
                    return createStoryTasks(story, addTaskDtos);
                } else {
                    throw new TasksNamesConflictException("A task already exists in story");
                }
            }
        }
    }

    private Story createStoryTasks(Story story, List<AddTaskDto> objects) {
        List<Task> createdTasks = new ArrayList<>();
        objects.forEach(addTaskObject -> {
            Task task = new Task();
            Optional<User> taskCreator = userRepository.findByUsername(addTaskObject.taskCreator().getUsername());
            Optional<User> taskAssignee = userRepository.findByUsername(addTaskObject.taskAssignee().getUsername());
            if (taskCreator.isEmpty() || taskAssignee.isEmpty()) {
                throw new UsernameNotFoundException("Task creator or task assignee is not found");
            }
            task.setName(addTaskObject.name());
            task.setDescription(addTaskObject.description());
            task.setEstimatedCompletionDate(new Date(addTaskObject.estimatedCompletionDate()));
            task.setOriginalEstimate(addTaskObject.originalEstimate());
            task.setRemainingEstimate(addTaskObject.remainingEstimate());
            task.setStoryTasks(story);
            task.setTaskPriority(addTaskObject.taskPriority());
            task.setTaskProgress(addTaskObject.taskProgress());
            task.setTaskCreator(taskCreator.get());
            task.setTaskAssignee(taskAssignee.get());
            createdTasks.add(task);
            taskRepository.save(task);
        });

        story.setStoryTasks(createdTasks);
        return storyRepository.save(story);
    }

    public Story renameStory(long id, RenameStoryDto dto) throws StoryNotFoundException {
        Optional<Story> storyOptional = storyRepository.findById(id);
        if (storyOptional.isEmpty()) {
            throw new StoryNotFoundException("Story not found");
        }

        Story story = storyOptional.get();
        story.setName(dto.name());
        return storyRepository.save(story);
    }
}
