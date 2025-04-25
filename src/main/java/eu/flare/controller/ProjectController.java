package eu.flare.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.exceptions.conflicts.BacklogAlreadyExistsException;
import eu.flare.exceptions.conflicts.EpicNamesConflictException;
import eu.flare.exceptions.conflicts.ProjectNameConflictException;
import eu.flare.exceptions.conflicts.SprintNamesConflictsException;
import eu.flare.exceptions.empty.EpicsEmptyException;
import eu.flare.exceptions.notfound.ProjectNotFoundException;
import eu.flare.model.Epic;
import eu.flare.model.Project;
import eu.flare.model.dto.EmptyProjectDto;
import eu.flare.model.dto.add.AddBacklogDto;
import eu.flare.model.dto.add.AddEpicsDto;
import eu.flare.model.dto.add.AddMembersDto;
import eu.flare.model.dto.add.AddSprintDto;
import eu.flare.model.dto.rename.RenameProjectDto;
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

    @GetMapping
    public ResponseEntity<Object> findProject(@RequestParam("name") String name) {
        Optional<Project> project = projectService.findProject(name);
        return project.<ResponseEntity<Object>>map(value -> ResponseEntity.status(HttpStatus.OK)
                .body(new SearchProjectByNameResponse(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ProjectNotFoundResponse(MessageFormat.format("Project with given name {0} is not found", name))));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createEmptyProject(@RequestBody EmptyProjectDto dto) {
        boolean isRequestBodyValid = projectService.validateCreateProjectBody(dto);
        if (isRequestBodyValid) {
            try {
                Project project = projectService.createEmptyProject(dto);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new CreateProjectResponse(project));
            } catch (ProjectNameConflictException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ProjectNameConflictResponse(e.getMessage()));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CreateProjectErrorResponse("Request body is not valid"));
        }
    }

    @GetMapping("/{id}/epics")
    public ResponseEntity<Object> findProjectEpics(@PathVariable("id") long id) {
        List<Epic> epics = projectService.findEpics(id);
        if (epics.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new EpicsNotFoundResponse(MessageFormat.format("Project {0} does not have any epics", Long.toString(id))));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ProjectWithEpicsResponse(Long.toString(id), epics));
        }
    }

    @PutMapping("/{id}/epics/add")
    public ResponseEntity<Object> addEpics(@RequestBody AddEpicsDto dto, @PathVariable("id") long id) {
        try {
            Project project = projectService.addProjectEpics(id, dto);
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

    @PutMapping("/{id}/members/add")
    public ResponseEntity<Object> addProjectMembers(@RequestBody List<AddMembersDto> dto, @PathVariable("id") long id) {
        try {
            Project project = projectService.addProjectMembers(id, dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new UpdateProjectResponse(project));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ProjectNotFoundResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<Object> renameProject(@RequestBody RenameProjectDto dto, @PathVariable("id") long id) {
        try {
            Project project = projectService.renameProject(id, dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new UpdateProjectResponse(project));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ProjectNotFoundResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/sprints/add")
    public ResponseEntity<Object> createProjectSprint(@RequestBody AddSprintDto dto, @PathVariable("id") long id) {
        try {
            Project project = projectService.createSprintForProject(id, dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new UpdateProjectResponse(project));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ProjectNotFoundResponse(e.getMessage()));
        } catch (SprintNamesConflictsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SprintNamesConflictsResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/backlog/create")
    public ResponseEntity<Object> createBacklogForProject(@PathVariable("id") long id, @RequestBody AddBacklogDto dto) {
        try {
            Project project = projectService.createBacklogForProject(id, dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new UpdateProjectResponse(project));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ProjectNotFoundResponse(e.getMessage()));
        } catch (BacklogAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new BacklogAlreadyExistsResponse(e.getMessage()));
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
    private record UpdateProjectResponse(
            @JsonProperty("project") Project project
    ){}
    private record SprintNamesConflictsResponse(
            @JsonProperty("error") String error
    ){}
    private record ProjectNameConflictResponse(
            @JsonProperty("error") String error
    ) {}
    private record BacklogAlreadyExistsResponse(
            @JsonProperty("error") String error
    ) {}
}
