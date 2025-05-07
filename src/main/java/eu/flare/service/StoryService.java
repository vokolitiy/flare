package eu.flare.service;

import eu.flare.exceptions.conflicts.TasksNamesConflictException;
import eu.flare.exceptions.misc.UnknownPriorityTypeException;
import eu.flare.exceptions.misc.UnknownProgressTypeException;
import eu.flare.exceptions.notfound.StoryNotFoundException;
import eu.flare.model.*;
import eu.flare.model.dto.update.UpdateStoryResolutionDto;
import eu.flare.model.dto.add.AddTaskDto;
import eu.flare.model.dto.rename.RenameStoryDto;
import eu.flare.model.dto.update.UpdateStoryPriorityDto;
import eu.flare.model.dto.update.UpdateStoryProgressDto;
import eu.flare.repository.StoryRepository;
import eu.flare.repository.TaskRepository;
import eu.flare.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    @Autowired
    public StoryService(
            StoryRepository storyRepository,
            TaskRepository taskRepository,
            UserRepository userRepository
    ) {
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
        }
        Story story = storyOptional.get();
        List<Task> storyTasks = story.getStoryTasks();
        return createStoryTasks(storyTasks, story, addTaskDtos);
    }

    @Transactional(rollbackOn = {Exception.class})
    public Story updateStoryPriority(long id, UpdateStoryPriorityDto dto) throws StoryNotFoundException, UnknownPriorityTypeException {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new StoryNotFoundException("Story not found"));
        PriorityType priorityType = PriorityType.valueOfLabel(dto.name());
        if (priorityType == null) {
            throw new UnknownPriorityTypeException("Unknown priority type");
        }
        story.setPriorityType(priorityType);
        return storyRepository.save(story);
    }

    @Transactional(rollbackOn = {Exception.class})
    public Story updateStoryProgress(long id, UpdateStoryProgressDto dto) throws StoryNotFoundException, UnknownProgressTypeException {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new StoryNotFoundException("Story not found"));
        ProgressType progressType = ProgressType.valueOfLabel(dto.name());
        if (progressType == null) {
            throw new UnknownProgressTypeException("Unknown progress type");
        }
        story.setProgressType(progressType);
        return storyRepository.save(story);
    }

    @Transactional(rollbackOn = {Exception.class})
    public Story updateStoryResolution(long id, UpdateStoryResolutionDto dto) throws StoryNotFoundException, UnknownProgressTypeException {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new StoryNotFoundException("Story not found"));
        ResolutionType resolutionType = ResolutionType.valueOfLabel(dto.name());
        if (resolutionType == null) {
            throw new UnknownProgressTypeException("Unknown progress type");
        }
        story.setResolutionType(resolutionType);
        return storyRepository.save(story);
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
                    task.setName(mappable.name());
                    task.setDescription(mappable.description());
                    task.setEstimatedCompletionDate(new Date(mappable.estimatedCompletionDate()));
                    task.setOriginalEstimate(mappable.originalEstimate());
                    task.setRemainingEstimate(mappable.remainingEstimate());
                    task.setStoryTasks(story);
                    task.setTaskCreator(taskCreator.get());
                    task.setTaskAssignee(taskAssignee.get());
                    task.setPriorityType(PriorityType.valueOfLabel(mappable.taskPriority()));
                    task.setProgressType(ProgressType.valueOfLabel(mappable.taskProgress()));
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
}
