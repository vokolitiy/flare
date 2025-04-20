package eu.flare.service;

import eu.flare.model.Project;
import eu.flare.model.dto.EmptyProjectDto;
import eu.flare.model.response.CreateProjectResponse;
import eu.flare.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
