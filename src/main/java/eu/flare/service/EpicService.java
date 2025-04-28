package eu.flare.service;

import eu.flare.exceptions.conflicts.StoryNamesConflictException;
import eu.flare.exceptions.empty.RequestBodyEmptyException;
import eu.flare.exceptions.notfound.EpicNotFoundException;
import eu.flare.model.*;
import eu.flare.model.dto.add.AddStoryDto;
import eu.flare.model.dto.rename.RenameEpicDto;
import eu.flare.repository.EpicRepository;
import eu.flare.repository.UserRepository;
import eu.flare.repository.story.StoryPriorityRepository;
import eu.flare.repository.story.StoryProgressRepository;
import eu.flare.repository.story.StoryRepository;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EpicService {

    private final EpicRepository epicRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final StoryPriorityRepository storyPriorityRepository;
    private final StoryProgressRepository storyProgressRepository;

    @Autowired
    public EpicService(
            EpicRepository epicRepository,
            UserRepository userRepository,
            StoryRepository storyRepository,
            StoryPriorityRepository storyPriorityRepository,
            StoryProgressRepository storyProgressRepository
    ) {
        this.epicRepository = epicRepository;
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
        this.storyPriorityRepository = storyPriorityRepository;
        this.storyProgressRepository = storyProgressRepository;
    }

    public Epic addStoriesForEpic(long id, List<AddStoryDto> dto) throws EpicNotFoundException, RequestBodyEmptyException, UsernameNotFoundException, StoryNamesConflictException {
        Optional<Epic> epicOptional = epicRepository.findById(id);
        if (epicOptional.isEmpty()) {
            throw new EpicNotFoundException("Epic with given name %s not found".formatted(id));
        }
        if (dto.isEmpty()) {
            throw new RequestBodyEmptyException("Request body is empty");
        }
        Epic epic = epicOptional.get();
        List<String> storyNamesToAdd = dto.stream().map(AddStoryDto::name).collect(Collectors.toList());
        List<String> allStories = storyRepository.findAll().stream().map(Story::getName).collect(Collectors.toList());
        List<String> combined = allStories.stream()
                .filter(two -> storyNamesToAdd.stream().anyMatch(one -> one.equals(two)))
                .toList();
        if (combined.isEmpty()) {
            return createStoriesIfNotExist(dto, epic);
        } else {
            throw new StoryNamesConflictException("Story exists");
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

    private Epic createStoriesIfNotExist(List<AddStoryDto> dto, Epic epic) {
        List<Story> createdStories = dto.stream().map(addStoryDto ->  {
            User creatorUser = findStoryCreator(addStoryDto);
            User assigneeUser = findUserAssignee(addStoryDto);
            Story story = new Story();
            story.setName(addStoryDto.name());
            story.setDescription(addStoryDto.description());
            story.setEstimatedCompletionDate(new Date(addStoryDto.estimatedCompletionDate()));
            story.setOriginalEstimate(addStoryDto.originalEstimate());
            story.setRemainingEstimate(addStoryDto.remainingEstimate());
            story.setStoryPoints(addStoryDto.storyPoints());
            story.setStoryPriority(findStoryPriority(addStoryDto));
            story.setStoryProgress(findStoryProgress(addStoryDto));
            story.setStoryCreator(findStoryCreator(addStoryDto));
            story.setStoryAssignee(findUserAssignee(addStoryDto));
            story.setStoryWatchers(findStoryWatchers(addStoryDto));

            Story savedStory = storyRepository.save(story);
            if (creatorUser != null) {
                creatorUser.setStoryCreator(savedStory);
                userRepository.save(creatorUser);
            }
            if (assigneeUser != null) {
                assigneeUser.setStoryAssignee(savedStory);
                userRepository.save(assigneeUser);
            }

            return savedStory;
        }).collect(Collectors.toList());

        epic.setStories(createdStories);
        Epic savedEpic = epicRepository.save(epic);
        storyRepository.findAll()
                .forEach(story -> {
                    story.setEpic(savedEpic);
                    storyRepository.save(story);
                });
        return savedEpic;
    }

    private StoryPriority findStoryPriority(AddStoryDto addStoryDto) {
        Optional<StoryPriority> storyPriorityOptional = storyPriorityRepository.findByName(addStoryDto.storyPriorityName());
        return storyPriorityOptional.orElse(null);
    }

    private StoryProgress findStoryProgress(AddStoryDto addStoryDto) {
        Optional<StoryProgress> storyProgressOptional = storyProgressRepository.findByName(addStoryDto.storyProgressName());
        return storyProgressOptional.orElse(null);
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
}
