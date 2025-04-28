package eu.flare.service;

import eu.flare.exceptions.notfound.BacklogNotFoundException;
import eu.flare.exceptions.notfound.StoryNotFoundException;
import eu.flare.model.Backlog;
import eu.flare.model.Story;
import eu.flare.model.dto.add.AddBacklogStoryDto;
import eu.flare.repository.BacklogRepository;
import eu.flare.repository.story.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BacklogService {

    private final BacklogRepository backlogRepository;
    private final StoryRepository storyRepository;

    @Autowired
    public BacklogService(BacklogRepository backlogRepository, StoryRepository storyRepository) {
        this.backlogRepository = backlogRepository;
        this.storyRepository = storyRepository;
    }

    public Optional<Backlog> findBacklog(String name) {
        return backlogRepository.findByName(name);
    }

    public Backlog addStoriesToBacklog(long backlogId, List<AddBacklogStoryDto> dtoList) throws BacklogNotFoundException, StoryNotFoundException {
        Optional<Backlog> backlogOptional = backlogRepository.findById(backlogId);
        if (backlogOptional.isEmpty()) {
            throw new BacklogNotFoundException("Backlog not found");
        }
        Backlog backlog = backlogOptional.get();
        return updateBacklogStories(dtoList, backlog);
    }

    private Backlog updateBacklogStories(List<AddBacklogStoryDto> dtoList, Backlog backlog) throws StoryNotFoundException {
        List<Story> backlogStories = backlog.getBacklogStories();
        if (backlogStories.isEmpty()) {
            List<Story> storiesToAdd = dtoList.stream().map(addBacklogStoryDto -> {
                Optional<Story> storyOptional = storyRepository.findById(addBacklogStoryDto.storyId());
                if (storyOptional.isPresent()) {
                    return storyOptional.get();
                } else {
                    throw new IllegalStateException("Story not found");
                }
            }).collect(Collectors.toList());

            backlog.setBacklogStories(storiesToAdd);
            return backlogRepository.save(backlog);
        } else {
            for (AddBacklogStoryDto dto : dtoList) {
                long storyId = dto.storyId();
                boolean match = backlogStories.stream().anyMatch(story -> story.getId() == storyId);
                if (!match) {
                    Optional<Story> storyOptional = storyRepository.findById(storyId);
                    if (storyOptional.isPresent()) {
                        backlogStories.add(storyOptional.get());
                    } else {
                        throw new StoryNotFoundException("Story not found");
                    }
                }
            }

            backlog.setBacklogStories(backlogStories);
            return backlogRepository.save(backlog);
        }
    }
}
