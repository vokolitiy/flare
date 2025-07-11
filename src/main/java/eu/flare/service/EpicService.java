package eu.flare.service;

import eu.flare.exceptions.conflicts.StoryNamesConflictException;
import eu.flare.exceptions.empty.RequestBodyEmptyException;
import eu.flare.exceptions.notfound.EpicNotFoundException;
import eu.flare.model.*;
import eu.flare.model.dto.add.AddStoryDto;
import eu.flare.model.dto.rename.RenameEpicDto;
import eu.flare.repository.BacklogRepository;
import eu.flare.repository.EpicRepository;
import eu.flare.repository.StoryRepository;
import eu.flare.repository.UserRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackOn = {Exception.class})
public class EpicService {

    private final EpicRepository epicRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final BacklogRepository backlogRepository;

    @Autowired
    public EpicService(
            EpicRepository epicRepository,
            UserRepository userRepository,
            StoryRepository storyRepository,
            BacklogRepository backlogRepository
    ) {
        this.epicRepository = epicRepository;
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
        this.backlogRepository = backlogRepository;
    }

    public Epic addStoriesForEpic(long id, List<AddStoryDto> dto) throws EpicNotFoundException, RequestBodyEmptyException, StoryNamesConflictException {
        Optional<Epic> epicOptional = epicRepository.findById(id);
        if (epicOptional.isEmpty()) {
            throw new EpicNotFoundException("Epic with given id %s not found".formatted(id));
        }
        if (dto.isEmpty()) {
            throw new RequestBodyEmptyException("Request body is empty");
        }
        Epic epic = epicOptional.get();
        List<Story> stories = storyRepository.findAll();
        Set<AddStoryDto> freshStories = dto.stream()
                .filter(addStoryDto -> isFreshStory(addStoryDto, stories))
                .collect(Collectors.toSet());
        if (!freshStories.isEmpty()) {
            return createStoriesIfNotExist(freshStories, epic);
        } else {
            throw new StoryNamesConflictException("Stories exist");
        }
    }

    public Optional<Epic> findEpic(String name) {
        return epicRepository.findByName(name);
    }

    public Epic renameEpic(long id, RenameEpicDto epicDto) throws EpicNotFoundException {
        Optional<Epic> epicOptional = epicRepository.findById(id);
        if (epicOptional.isEmpty()) {
            throw new EpicNotFoundException("Epic not found");
        }

        Epic epic = epicOptional.get();
        epic.setName(epicDto.name());
        return epicRepository.save(epic);
    }

    private Epic createStoriesIfNotExist(Set<AddStoryDto> addStoryDto, Epic epic) {
        List<Story> createdStories = addStoryDto.stream().map(addStory -> {
            User creatorUser = findStoryCreator(addStory);
            User assigneeUser = findUserAssignee(addStory);
            Story story = new Story();
            story.setName(addStory.name());
            story.setDescription(addStory.description());
            story.setEstimatedCompletionDate(new Date(addStory.estimatedCompletionDate()));
            story.setOriginalEstimate(addStory.originalEstimate());
            story.setRemainingEstimate(addStory.remainingEstimate());
            story.setStoryPoints(addStory.storyPoints());
            story.setPriorityType(mapStoryPriority(addStory));
            story.setProgressType(mapStoryProgress(addStory));
            story.setStoryCreator(findStoryCreator(addStory));
            story.setStoryAssignee(findUserAssignee(addStory));
            story.setStoryWatchers(findStoryWatchers(addStory));
            story.setEpic(epic);

            Story savedStory = storyRepository.save(story);
            if (creatorUser != null) {
                List<Story> userCreatedStories = creatorUser.getCreatedStories();
                if (!userCreatedStories.contains(savedStory)) {
                    userCreatedStories.add(savedStory);
                    creatorUser.setCreatedStories(userCreatedStories);
                    userRepository.save(creatorUser);
                }
            }
            if (assigneeUser != null) {
                List<Story> userAssignedStories = assigneeUser.getAssignedStories();
                if (!userAssignedStories.contains(savedStory)) {
                    userAssignedStories.add(savedStory);
                    assigneeUser.setAssignedStories(userAssignedStories);
                    userRepository.save(assigneeUser);
                }
            }

            updateStoryWatchers(addStory, savedStory);

            return savedStory;
        }).collect(Collectors.toList());

        List<Story> stories = epic.getStories();
        if (stories.isEmpty()) {
            epic.setStories(createdStories);
        } else {
            List<Story> filtered = new ArrayList<>();
            for (Story createdStory : createdStories) {
                if (!stories.contains(createdStory)) {
                    filtered.add(createdStory);
                }
            }
            stories.addAll(filtered);
            epic.setStories(stories);
        }
        return epicRepository.save(epic);
    }

    private ProgressType mapStoryProgress(AddStoryDto addStory) {
        String progress = addStory.storyProgressName();
        return ProgressType.valueOfLabel(progress);
    }

    private PriorityType mapStoryPriority(AddStoryDto addStory) {
        String priority = addStory.storyPriorityName();
        return PriorityType.valueOfLabel(priority);
    }

    @Nullable
    private User findStoryCreator(AddStoryDto addStoryDto) {
        Optional<User> storyCreatorOptional = userRepository.findByUsername(addStoryDto.storyCreatorName());
        return storyCreatorOptional.orElse(null);
    }

    @Nullable
    private User findUserAssignee(AddStoryDto addStoryDto) {
        Optional<User> storyAssigeeOptional = userRepository.findByUsername(addStoryDto.storyCreatorName());
        return storyAssigeeOptional.orElse(null);
    }

    private List<User> findStoryWatchers(AddStoryDto addStoryDto) {
        return addStoryDto.storyWatcherNames()
                .stream()
                .map(name -> userRepository.findByUsername(name).orElse(null))
                .collect(Collectors.toList());
    }

    private boolean isFreshStory(AddStoryDto addStoryDto, List<Story> stories) {
        return stories.stream().filter(story -> story.getName().equals(addStoryDto.name()))
                .toList().isEmpty();
    }

    private void updateStoryWatchers(AddStoryDto addStory, Story savedStory) {
        addStory.storyWatcherNames()
                .stream()
                .map(name -> {
                    Optional<User> userOptional = userRepository.findByUsername(name);
                    if (userOptional.isEmpty()) {
                        throw new UsernameNotFoundException("User not found");
                    } else {
                        return userOptional.get();
                    }
                })
                .forEach(user -> {
                    List<Story> watchedStories = user.getWatchedStories();
                    if (!watchedStories.contains(savedStory)) {
                        watchedStories.add(savedStory);
                        user.setWatchedStories(watchedStories);
                        userRepository.save(user);
                    }
                });
    }
}
