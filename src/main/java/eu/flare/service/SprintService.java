package eu.flare.service;

import eu.flare.exceptions.conflicts.SprintAlreadyStartedException;
import eu.flare.exceptions.empty.RequestBodyEmptyException;
import eu.flare.exceptions.misc.SprintAlreadyCompletedException;
import eu.flare.exceptions.misc.SprintNotStartedException;
import eu.flare.exceptions.notfound.SprintNotFoundException;
import eu.flare.model.Sprint;
import eu.flare.model.Story;
import eu.flare.model.dto.add.AddSprintStoryDto;
import eu.flare.model.dto.rename.RenameSprintDto;
import eu.flare.repository.SprintRepository;
import eu.flare.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SprintService {

    private final SprintRepository sprintRepository;
    private final StoryRepository storyRepository;

    @Autowired
    public SprintService(SprintRepository sprintRepository, StoryRepository storyRepository) {
        this.sprintRepository = sprintRepository;
        this.storyRepository = storyRepository;
    }

    public Sprint findSprint(String name) throws SprintNotFoundException {
        return sprintRepository.findByName(name)
                .orElseThrow(() -> new SprintNotFoundException("Sprint not found"));
    }

    public Sprint renameSprint(long id, RenameSprintDto dto) throws SprintNotFoundException {
        Sprint sprint = findSprintById(id);
        sprint.setName(dto.name());
        return sprintRepository.save(sprint);
    }

    public Sprint startSprint(long id) throws SprintNotFoundException, SprintAlreadyStartedException {
        Sprint sprint = findSprintById(id);
        if (sprint.isStarted()) {
            throw new SprintAlreadyStartedException("Sprint is already started");
        }
        Date startDate = Date.from(Instant.now());
        sprint.setStartDate(startDate);
        sprint.setStarted(true);

        return sprintRepository.save(sprint);
    }

    public Sprint finishSprint(long id) throws SprintNotFoundException, SprintNotStartedException, SprintAlreadyCompletedException {
        Sprint sprint = findSprintById(id);
        if (!sprint.isStarted()) {
            throw new SprintNotStartedException("Sprint is not started yet");
        }
        if (sprint.isCompleted()) {
            throw new SprintAlreadyCompletedException("Sprint is already completed");
        }
        sprint.setCompleteDate(Date.from(Instant.now()));
        sprint.setCompleted(true);

        return sprintRepository.save(sprint);
    }

    private Sprint findSprintById(long id) throws SprintNotFoundException {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new SprintNotFoundException("Sprint not found"));
    }

    public Sprint addSprintStories(long id, List<AddSprintStoryDto> sprintStories) throws SprintNotFoundException, SprintAlreadyCompletedException, RequestBodyEmptyException {
        Sprint sprint = findSprintById(id);
        if (sprint.isCompleted()) {
            throw new SprintAlreadyCompletedException("Sprint is already completed");
        }
        if (sprintStories.isEmpty()) {
            throw new RequestBodyEmptyException("Request body is empty");
        }
        List<Story> storiesToAdd = sprintStories.stream().map(AddSprintStoryDto::storyId)
                .toList()
                .stream()
                .filter(predicated -> !sprintStoryExists(sprintStories, predicated))
                .map(storyRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        sprint.setStories(storiesToAdd);
        storiesToAdd.forEach(story -> {
            story.setSprint(sprint);
            storyRepository.save(story);
        });
        return sprintRepository.save(sprint);
    }

    private boolean sprintStoryExists(List<AddSprintStoryDto> sprintStories, Long id) {
        return sprintStories.stream().filter(story -> story.storyId() == id)
                .toList()
                .isEmpty();
    }
}
