package eu.flare.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.exceptions.EpicNamesConflictException;
import eu.flare.exceptions.EpicsEmptyException;
import eu.flare.exceptions.ProjectNotFoundException;
import eu.flare.model.Epic;
import eu.flare.model.Project;
import eu.flare.model.dto.AddEpicsDto;
import eu.flare.model.dto.EmptyProjectDto;
import eu.flare.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
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

    @GetMapping("/{name}/epics")
    public ResponseEntity<Object> findProjectEpics(@PathVariable("name") String name) {
        List<Epic> epics = projectService.findEpics(name);
        if (epics.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new EpicsNotFoundResponse(MessageFormat.format("Project {0} does not have any epics", name)));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ProjectWithEpicsResponse(name, epics));
        }
    }

    @PutMapping("/{name}/epics/add")
    public ResponseEntity<Object> addEpics(@RequestBody AddEpicsDto dto, @PathVariable("name") String name) {
        try {
            Project project = projectService.addProjectEpics(name, dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ProjectWithEpicsResponse(project.getName(), project.getEpics()));
        } catch (EpicNamesConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ProjectWithEpicsErrorResponse(e.getMessage()));
        } catch (EpicsEmptyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ProjectWithEpicsErrorResponse(e.getMessage()));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ProjectWithEpicsErrorResponse(e.getMessage()));
        }
    }

    private record CreateProjectErrorResponse(@JsonProperty("error") String errorMessage) { }
    private record CreateProjectResponse(@JsonProperty("project") Project project) { }
    private record EpicsNotFoundResponse(@JsonProperty("error") String error){}
    private record ProjectNotFoundResponse(String message) { }
    private record SearchProjectByNameResponse(Project project) { }
    private record ProjectWithEpicsResponse(
            @JsonProperty("project_name") String projectName,
            @JsonProperty("epics") List<Epic> epics
    ) {
    }
    private record ProjectWithEpicsErrorResponse(
            @JsonProperty("error") String error
    ){}
}
