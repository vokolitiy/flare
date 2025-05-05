package eu.flare.service.story;

import eu.flare.exceptions.conflicts.TasksNamesConflictException;
import eu.flare.exceptions.notfound.SprintNotFoundException;
import eu.flare.exceptions.notfound.StoryNotFoundException;
import eu.flare.model.*;
import eu.flare.model.dto.add.AddTaskDto;
import eu.flare.model.dto.rename.RenameStoryDto;
import eu.flare.repository.SprintRepository;
import eu.flare.repository.UserRepository;
import eu.flare.repository.story.StoryRepository;
import eu.flare.repository.task.TaskPriorityRepository;
import eu.flare.repository.task.TaskProgressRepository;
import eu.flare.repository.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoryService {

    private final StoryRepository storyRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskPriorityRepository taskPriorityRepository;
    private final TaskProgressRepository taskProgressRepository;
    private final SprintRepository sprintRepository;

    @Autowired
    public StoryService(
            StoryRepository storyRepository,
            TaskRepository taskRepository,
            UserRepository userRepository,
            TaskPriorityRepository taskPriorityRepository,
            TaskProgressRepository taskProgressRepository,
            SprintRepository sprintRepository
    ) {
        this.storyRepository = storyRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskPriorityRepository = taskPriorityRepository;
        this.taskProgressRepository = taskProgressRepository;
        this.sprintRepository = sprintRepository;
    }

    public Optional<Story> findStoryWithName(String name) {
        return storyRepository.findByName(name);
    }

    public Story createTasksForStory(long id, List<AddTaskDto> addTaskDtos) throws StoryNotFoundException, TasksNamesConflictException {
        Optional<Story> storyOptional = storyRepository.findById(id);
        if (storyOptional.isEmpty()) {
            throw new StoryNotFoundException("Story not found");
        }
        Story story = storyOptional.get();
        List<Task> storyTasks = story.getStoryTasks();
        return createStoryTasks(storyTasks, story, addTaskDtos);
    }

    private Story createStoryTasks(List<Task> tasks, Story story, List<AddTaskDto> addTaskDtos) {
        List<Task> createdTasks = addTaskDtos.stream()
                .distinct()
                .filter(addTaskDto -> !taskExists(addTaskDto.name()))
                .map(mappable -> {
                    Task task = new Task();
                    Optional<User> taskCreator = userRepository.findByUsername(mappable.taskCreator());
                    Optional<User> taskAssignee = userRepository.findByUsername(mappable.taskAssignee());
                    if (taskCreator.isEmpty() || taskAssignee.isEmpty()) {
                        throw new UsernameNotFoundException("Task creator or task assignee is not found");
                    }
                    Optional<TaskPriority> taskPriority = taskPriorityRepository.findByName(mappable.taskPriority());
                    Optional<TaskProgress> taskProgress = taskProgressRepository.findByName(mappable.taskProgress());
                    task.setName(mappable.name());
                    task.setDescription(mappable.description());
                    task.setEstimatedCompletionDate(new Date(mappable.estimatedCompletionDate()));
                    task.setOriginalEstimate(mappable.originalEstimate());
                    task.setRemainingEstimate(mappable.remainingEstimate());
                    task.setStoryTasks(story);
                    task.setTaskCreator(taskCreator.get());
                    task.setTaskAssignee(taskAssignee.get());
                    task.setTaskPriority(taskPriority.get());
                    task.setTaskProgress(taskProgress.get());
                    task.setTaskCreator(taskCreator.get());
                    task.setTaskAssignee(taskAssignee.get());
                    taskRepository.save(task);
                    return task;
                })
                .collect(Collectors.toList());
        if (tasks.isEmpty()) {
            story.setStoryTasks(createdTasks);
            return storyRepository.save(story);
        } else {
            tasks.addAll(createdTasks);
            story.setStoryTasks(tasks);
            return storyRepository.save(story);
        }
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

    private boolean taskExists(String taskName) {
        return taskRepository.findByName(taskName).isPresent();
    }

    public Story moveStoryIntoSprint(long storyId, long sprintId) throws StoryNotFoundException, SprintNotFoundException {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException("Story not found"));
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new SprintNotFoundException("Sprint not found"));
        List<Story> sprintStories = sprint.getSprintStories();
        if (sprintStories.isEmpty() || !sprintStories.contains(story)) {
            sprintStories.add(story);
            sprint.setSprintStories(sprintStories);
            story.setSprintStory(sprint);
            storyRepository.save(story);
            sprintRepository.save(sprint);
        }

        return story;
    }
}
