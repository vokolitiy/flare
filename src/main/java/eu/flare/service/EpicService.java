package eu.flare.service;

import eu.flare.exceptions.notfound.EpicNotFoundException;
import eu.flare.exceptions.empty.RequestBodyEmptyException;
import eu.flare.exceptions.conflicts.StoryNamesConflictException;
import eu.flare.model.*;
import eu.flare.model.dto.add.AddStoryDto;
import eu.flare.model.dto.rename.RenameEpicDto;
import eu.flare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EpicService {

    private final EpicRepository epicRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;

    @Autowired
    public EpicService(
            EpicRepository epicRepository,
            UserRepository userRepository,
            StoryRepository storyRepository
    ) {
        this.epicRepository = epicRepository;
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
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
        List<Story> epicStories = epic.getStories();
        if (epicStories.isEmpty()) {
            return createStoriesIfNotExist(dto, epic);
        }

        List<String> storiesToAdd = dto.stream().map(AddStoryDto::name).toList();
        List<String> epicStoriesNames = epicStories.stream().map(Story::getName).toList();
        List<String> combined = epicStoriesNames.stream()
                .filter(two -> storiesToAdd.stream().anyMatch(one -> one.equals(two)))
                .toList();
        if (!combined.isEmpty()) {
            throw new StoryNamesConflictException("Unable to add story, story with a given name already exists");
        } else {
            return createStoriesIfNotExist(dto, epic);
        }
    }

    private Epic createStoriesIfNotExist(List<AddStoryDto> dto, Epic epic) {
        List<Story> savedStories = new ArrayList<>();
        dto.forEach(addStoryDto -> {
            StoryPriority priority = new StoryPriority();
            priority.setName(addStoryDto.storyPriority().getName());

            StoryProgress storyProgress = new StoryProgress();
            storyProgress.setName(addStoryDto.storyProgress().getName());

            String storyCreatorName = addStoryDto.storyCreator().getUsername();
            Optional<User> creatorUser = userRepository.findByUsername(storyCreatorName);
            if (creatorUser.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }

            String storyAssigneeName = addStoryDto.storyAssignee().getUsername();
            Optional<User> assigneeUser = userRepository.findByUsername(storyAssigneeName);
            if (assigneeUser.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }

            List<User> storyWatchers = addStoryDto.storyWatchers().stream()
                    .map(User::getUsername)
                    .map(userRepository::findByUsername)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            Story story = new Story();
            story.setName(addStoryDto.name());
            story.setDescription(addStoryDto.description());
            story.setEstimatedCompletionDate(new Date(addStoryDto.estimatedCompletionDate()));
            story.setOriginalEstimate(addStoryDto.originalEstimate());
            story.setRemainingEstimate(addStoryDto.remainingEstimate());
            story.setStoryPoints(addStoryDto.storyPoints());
            story.setStoryPriority(priority);
            story.setStoryProgress(storyProgress);
            story.setStoryCreator(creatorUser.get());
            story.setStoryAssignee(assigneeUser.get());
            story.setStoryWatchers(storyWatchers);

            Story savedStory = storyRepository.save(story);
            savedStories.add(savedStory);
        });
        epic.setStories(savedStories);
        Epic savedEpic = epicRepository.save(epic);
        storyRepository.findAll()
                .forEach(story -> {
                    story.setEpic(savedEpic);
                    storyRepository.save(story);
                });
        return savedEpic;
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
}
