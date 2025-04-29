package eu.flare.service;

import eu.flare.exceptions.conflicts.BacklogAlreadyExistsException;
import eu.flare.exceptions.conflicts.EpicNamesConflictException;
import eu.flare.exceptions.conflicts.ProjectNameConflictException;
import eu.flare.exceptions.conflicts.SprintNamesConflictsException;
import eu.flare.exceptions.empty.EpicsEmptyException;
import eu.flare.exceptions.notfound.ProjectNotFoundException;
import eu.flare.model.*;
import eu.flare.model.dto.EmptyProjectDto;
import eu.flare.model.dto.add.AddBacklogDto;
import eu.flare.model.dto.add.AddEpicsDto;
import eu.flare.model.dto.add.AddMembersDto;
import eu.flare.model.dto.add.AddSprintDto;
import eu.flare.model.dto.rename.RenameProjectDto;
import eu.flare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProjectService {

    private static final int MAX_PROJECT_NAME_LENGTH = 30;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EpicRepository epicRepository;
    private final SprintRepository sprintRepository;
    private final BacklogRepository backlogRepository;

    @Autowired
    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            EpicRepository epicRepository,
            SprintRepository sprintRepository,
            BacklogRepository backlogRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.epicRepository = epicRepository;
        this.sprintRepository = sprintRepository;
        this.backlogRepository = backlogRepository;
    }

    public Optional<Project> findProject(String name) {
        return projectRepository.findByName(name);
    }

    public boolean validateCreateProjectBody(EmptyProjectDto dto) {
        return !dto.name().isEmpty() && dto.name().length() <= MAX_PROJECT_NAME_LENGTH;
    }

    public Project createEmptyProject(EmptyProjectDto dto) throws ProjectNameConflictException {
        String newProjectName = dto.name();
        Optional<Project> projectOptional = projectRepository.findByName(newProjectName);
        if (projectOptional.isEmpty()) {
            Project project = new Project();
            project.setName(newProjectName);
            return projectRepository.save(project);
        } else {
            throw new ProjectNameConflictException("Project name is already taken");
        }
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
        List<String> epicNames = dto.epicNames();
        if (epicNames.isEmpty()) {
            throw new EpicsEmptyException("Unable to add empty epics");
        }
        List<Epic> projectEpics = project.getEpics();
        List<Epic> epicsToAdd = createEpics(dto);
        if (!projectEpics.isEmpty()) {
            List<String> epicNamesCombined = projectEpics.stream()
                    .map(Epic::getName)
                    .filter(two -> epicNames.stream().anyMatch(one -> one.equals(two)))
                    .toList();
            if (!epicNamesCombined.isEmpty()) {
                throw new EpicNamesConflictException("Unable to create project epics due to a names conflict");
            } else {
                project.setEpics(epicsToAdd);
            }
            addProjectToEpic(epicsToAdd, project);
        } else {
            projectEpics.addAll(epicsToAdd);
            project.setEpics(projectEpics);
            addProjectToEpic(projectEpics, project);
        }
        Project savedProject = projectRepository.save(project);
        return findProject(savedProject.getName()).get();
    }

    public Project addProjectMembers(long id, List<AddMembersDto> dto) throws ProjectNotFoundException {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException("Project not found");
        }

        Project project = projectOptional.get();
        List<User> projectMembers = project.getProjectMembers();
        if (projectMembers.isEmpty()) {
            List<User> freshProjectMembers = bulkAddProjectMembers(dto);
            project.setProjectMembers(freshProjectMembers);
            return projectRepository.save(project);
        } else {
            List<String> combined = dto.stream().map(AddMembersDto::username)
                    .filter(username -> projectMembers.stream().noneMatch(user -> user.getUsername().equals(username)))
                    .toList();
            if (!combined.isEmpty()) {
                List<User> freshProjectMembers = combined.stream().map(username -> {
                    Optional<User> userOptional = userRepository.findByUsername(username);
                    if (userOptional.isEmpty()) {
                        throw new UsernameNotFoundException("User not found");
                    }
                    return userOptional.get();
                }).toList();
                projectMembers.addAll(freshProjectMembers);
                project.setProjectMembers(projectMembers);
                return projectRepository.save(project);
            }
        }

        return project;
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
        Optional<Sprint> sprintOptional = sprintRepository.findByName(dto.name());
        if (sprintOptional.isEmpty()) {
            List<Sprint> projectSprints = project.getSprints();
            if (projectSprints.isEmpty()) {
                project.setSprints(createSprintObjects(dto.name()));
            } else {
                projectSprints.addAll(createSprintObjects(dto.name()));
                project.setSprints(projectSprints);
            }
            return projectRepository.save(project);
        } else {
            throw new SprintNamesConflictsException("Sprint already exists");
        }
    }

    public Project createBacklogForProject(long id, AddBacklogDto dto) throws ProjectNotFoundException, BacklogAlreadyExistsException {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException("Project is not found");
        }
        Project project = projectOptional.get();
        Backlog projectBacklog = project.getBacklog();
        if (projectBacklog != null) {
            throw new BacklogAlreadyExistsException("Backlog for project already exists");
        }
        String backlogName = dto.name();
        Backlog backlog = new Backlog();
        backlog.setName(backlogName);
        project.setBacklog(backlog);

        return projectRepository.save(project);
    }

    private List<Epic> createEpics(AddEpicsDto dtos) {
        return dtos.epicNames().stream().map(name -> {
            Epic epic = new Epic();
            epic.setName(name);
            return epic;
        }).collect(Collectors.toList());
    }

    private List<Sprint> createSprintObjects(String name) {
        List<Sprint> sprints = new ArrayList<>();
        Sprint sprint = new Sprint();
        sprint.setName(name);
        sprints.add(sprint);
        return sprints;
    }

    private void addProjectToEpic(List<Epic> epics, Project project) {
        epics.forEach(epic -> {
            epic.setProject(project);
            epicRepository.save(epic);
        });
    }
    private List<User> bulkAddProjectMembers(List<AddMembersDto> dto) {
        return dto.stream().map(addMembersDto -> {
            Optional<User> userOptional = userRepository.findByUsername(addMembersDto.username());
            if (userOptional.isEmpty()) {
                throw new UsernameNotFoundException("Username not found");
            }
            return userOptional.get();
        }).collect(Collectors.toList());
    }
}
