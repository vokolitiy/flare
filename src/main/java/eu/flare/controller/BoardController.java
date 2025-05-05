package eu.flare.controller;

import eu.flare.exceptions.notfound.BoardNotFoundException;
import eu.flare.exceptions.notfound.SprintNotFoundException;
import eu.flare.model.Board;
import eu.flare.model.dto.add.AddBoardDto;
import eu.flare.model.dto.add.AddBoardStoriesDto;
import eu.flare.model.response.Responses;
import eu.flare.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/board")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public ResponseEntity<?> findBoard(@RequestParam("name") String name) {
        try {
            Board board = boardService.findBoard(name);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.BoardResponse(board));
        } catch (BoardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.BoardNotFoundResponse(e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBoard(@Valid @RequestBody AddBoardDto addBoardDto) {
        Board board = boardService.createBoard(addBoardDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Responses.BoardResponse(board));
    }

    @PutMapping("/{boardId}/sprint/{sprintId}")
    public ResponseEntity<?> addBoardToSprint(
            @PathVariable("boardId") long boardId,
            @PathVariable("sprintId") long sprintId
    ) {
        try {
            Board board = boardService.addSprintBoard(boardId, sprintId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.BoardResponse(board));
        } catch (SprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.SprintNotFoundResponse(e.getMessage()));
        } catch (BoardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.BoardNotFoundResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/stories/refresh")
    public ResponseEntity<?> createOrUpdateBoardStories(
            @PathVariable("id") long boardId,
            @Valid @RequestBody AddBoardStoriesDto dto
    ) {
        return null;
    }
}
