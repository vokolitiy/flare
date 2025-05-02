package eu.flare.controller;

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
import eu.flare.model.dto.response.ResponsesDto;
import eu.flare.model.response.Responses;
import eu.flare.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
                .body(new Responses.SearchProjectByNameResponse(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Responses.ProjectNotFoundResponse(MessageFormat.format("Project with given name {0} is not found", name))));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createEmptyProject(@Valid @RequestBody EmptyProjectDto dto) {
        boolean isRequestBodyValid = projectService.validateCreateProjectBody(dto);
        if (isRequestBodyValid) {
            try {
                Project project = projectService.createEmptyProject(dto);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new Responses.CreateProjectResponse(new ResponsesDto.CreateProjectResponseDto(
                                project.getId(),
                                project.getName(),
                                project.getStartedAt(),
                                project.getUpdatedAt()
                        )));
            } catch (ProjectNameConflictException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new Responses.ProjectNameConflictResponse(e.getMessage()));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.CreateProjectErrorResponse("Request body is not valid"));
        }
    }

    @GetMapping("/{id}/epics")
    public ResponseEntity<Object> findProjectEpics(@PathVariable("id") long id) {
        List<Epic> epics = projectService.findEpics(id);
        if (epics.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.EpicsNotFoundResponse(MessageFormat.format("Project {0} does not have any epics", Long.toString(id))));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.ProjectWithEpicsResponse(projectService.findProject(id).get().getName(), epics));
        }
    }

    @PutMapping("/{id}/epics/add")
    public ResponseEntity<Object> addEpics(@Validated @RequestBody AddEpicsDto dto, @PathVariable("id") long id) {
        try {
            Pair<String, List<Epic>> epics = projectService.addProjectEpics(id, dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.ProjectWithEpicsResponse(epics.getFirst(), epics.getSecond()));
        } catch (EpicNamesConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Responses.ProjectWithEpicsErrorResponse(e.getMessage()));
        } catch (EpicsEmptyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.ProjectWithEpicsErrorResponse(e.getMessage()));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.ProjectWithEpicsErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/members/add")
    public ResponseEntity<Object> addProjectMembers(@Valid @RequestBody List<AddMembersDto> dto, @PathVariable("id") long id) {
        try {
            Project project = projectService.addProjectMembers(id, dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Responses.UpdateProjectResponse(project));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.ProjectNotFoundResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<Object> renameProject(@Valid @RequestBody RenameProjectDto dto, @PathVariable("id") long id) {
        try {
            Project project = projectService.renameProject(id, dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.UpdateProjectResponse(project));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.ProjectNotFoundResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/sprints/add")
    public ResponseEntity<Object> createProjectSprint(@Valid @RequestBody AddSprintDto dto, @PathVariable("id") long id) {
        try {
            Project project = projectService.createSprintForProject(id, dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.UpdateProjectResponse(project));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.ProjectNotFoundResponse(e.getMessage()));
        } catch (SprintNamesConflictsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Responses.SprintNamesConflictsResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/backlog/create")
    public ResponseEntity<Object> createBacklogForProject(@PathVariable("id") long id, @Valid @RequestBody AddBacklogDto dto) {
        try {
            Project project = projectService.createBacklogForProject(id, dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Responses.UpdateProjectResponse(project));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.ProjectNotFoundResponse(e.getMessage()));
        } catch (BacklogAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Responses.BacklogAlreadyExistsResponse(e.getMessage()));
        }
    }
}
