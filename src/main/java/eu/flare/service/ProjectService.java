package eu.flare.service;

import eu.flare.exceptions.EpicNamesConflictException;
import eu.flare.exceptions.EpicsEmptyException;
import eu.flare.exceptions.ProjectNotFoundException;
import eu.flare.model.Epic;
import eu.flare.model.Project;
import eu.flare.model.dto.AddEpicsDto;
import eu.flare.model.dto.EmptyProjectDto;
import eu.flare.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private static final int MAX_PROJECT_NAME_LENGTH = 30;
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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

    public List<Epic> findEpics(String name) {
        Optional<Project> projectOptional = projectRepository.findByName(name);
        if (projectOptional.isEmpty()) {
            return Collections.emptyList();
        } else {
            return projectOptional.get().getEpics();
        }
    }

    public Project addProjectEpics(String name, AddEpicsDto dto) throws EpicNamesConflictException, EpicsEmptyException, ProjectNotFoundException {
        Optional<Project> projectOptional = projectRepository.findByName(name);
        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException("Project with given name %s not found".formatted(name));
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
}
