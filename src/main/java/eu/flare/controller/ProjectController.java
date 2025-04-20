package eu.flare.controller;

import eu.flare.model.Project;
import eu.flare.model.dto.EmptyProjectDto;
import eu.flare.model.response.CreateProjectErrorResponse;
import eu.flare.model.response.CreateProjectResponse;
import eu.flare.model.response.ProjectNotFoundResponse;
import eu.flare.model.response.SearchProjectByNameResponse;
import eu.flare.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<Object> findProject(@PathVariable("name") String name) {
        Optional<Project> project = projectService.findProject(name);
        return project.<ResponseEntity<Object>>map(value -> ResponseEntity.status(HttpStatus.OK)
                .body(new SearchProjectByNameResponse(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ProjectNotFoundResponse(MessageFormat.format("Project with given name {0} is not found", name))));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createEmptyProject(@RequestBody EmptyProjectDto dto) {
        boolean isRequestBodyValid = projectService.validateCreateProjectBody(dto);
        if (isRequestBodyValid) {
            Project project = projectService.createEmptyProject(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CreateProjectResponse(project));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CreateProjectErrorResponse("Request body is not valid"));
        }
    }
}
