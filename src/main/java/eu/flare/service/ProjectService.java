package eu.flare.service;

import eu.flare.exceptions.conflicts.EpicNamesConflictException;
import eu.flare.exceptions.conflicts.SprintNamesConflictsException;
import eu.flare.exceptions.empty.EpicsEmptyException;
import eu.flare.exceptions.notfound.ProjectNotFoundException;
import eu.flare.model.Epic;
import eu.flare.model.Project;
import eu.flare.model.Sprint;
import eu.flare.model.User;
import eu.flare.model.dto.EmptyProjectDto;
import eu.flare.model.dto.add.AddEpicsDto;
import eu.flare.model.dto.add.AddMembersDto;
import eu.flare.model.dto.add.AddSprintDto;
import eu.flare.model.dto.rename.RenameProjectDto;
import eu.flare.repository.ProjectRepository;
import eu.flare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private static final int MAX_PROJECT_NAME_LENGTH = 30;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Optional<Project> findProject(String name) {
        return projectRepository.findByName(name);
    }

    public boolean validateCreateProjectBody(EmptyProjectDto dto) {
        return !dto.name().isEmpty() && dto.name().length() <= MAX_PROJECT_NAME_LENGTH;
    }

    public Project createEmptyProject(EmptyProjectDto dto) {
        String projectName = dto.name();
        Project project = new Project();
        project.setName(projectName);
        return projectRepository.save(project);
    }

    public List<Epic> findEpics(long id) {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isEmpty()) {
            return Collections.emptyList();
        } else {
            return projectOptional.get().getEpics();
        }
    }

    public Project addProjectEpics(long id, AddEpicsDto dto) throws EpicNamesConflictException, EpicsEmptyException, ProjectNotFoundException {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException("Project with given name %s not found".formatted(id));
        }
        Project project = projectOptional.get();
        List<Epic> epicsToAdd = dto.epics();
        if (epicsToAdd.isEmpty()) {
            throw new EpicsEmptyException("Unable to add empty epics");
        }
        List<Epic> projectEpics = project.getEpics();
        if (!projectEpics.isEmpty()) {
            List<Epic> combined = projectEpics.stream()
                    .filter(two -> epicsToAdd.stream().anyMatch(one -> one.getName().equals(two.getName())))
                    .toList();
            if (!combined.isEmpty()) {
                throw new EpicNamesConflictException("Unable to create project epics due to a names conflict");
            } else {
                project.setEpics(epicsToAdd);
                return projectRepository.save(project);
            }
        } else {
            project.setEpics(epicsToAdd);
            return projectRepository.save(project);
        }
    }

    public Project addProjectMembers(long id, List<AddMembersDto> dto) throws ProjectNotFoundException {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException("Project not found");
        }
        Project project = projectOptional.get();
        List<User> users = new ArrayList<>();
        dto.forEach(addMember -> {
            Optional<User> userOptional = userRepository.findByUsername(addMember.username());
            if (userOptional.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }
            User appUser = userOptional.get();
            users.add(appUser);
        });

        project.setProjectMembers(users);
        return projectRepository.save(project);
    }

    public Project renameProject(long id, RenameProjectDto dto) throws ProjectNotFoundException {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException("Project not found");
        }
        Project project = projectOptional.get();
        project.setName(dto.newProjectName());
        return projectRepository.save(project);
    }

    public Project createSprintForProject(long id, AddSprintDto dto) throws ProjectNotFoundException, SprintNamesConflictsException {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException("Project not found");
        }
        Project project = projectOptional.get();
        List<Sprint> projectSprints = project.getSprints();
        String newSprintName = dto.name();
        if (!projectSprints.isEmpty()) {
            List<Sprint> filteredSprints = projectSprints.stream().filter(sprint -> sprint.getName().equals(newSprintName)).collect(Collectors.toList());
            if (!filteredSprints.isEmpty()) {
                throw new SprintNamesConflictsException("Sprint with such name already exists");
            } else {
                projectSprints.addAll(createSprintObjects(newSprintName));
                project.setSprints(projectSprints);
                return projectRepository.save(project);
            }
        } else {
            project.setSprints(createSprintObjects(newSprintName));
            return projectRepository.save(project);
        }
    }

    private List<Sprint> createSprintObjects(String name) {
        List<Sprint> sprints = new ArrayList<>();
        Sprint sprint = new Sprint();
        sprint.setName(name);
        sprints.add(sprint);
        return sprints;
    }
}
