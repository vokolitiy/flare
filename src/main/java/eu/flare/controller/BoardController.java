package eu.flare.controller;

import eu.flare.exceptions.conflicts.BoardAlreadyExistsException;
import eu.flare.exceptions.conflicts.SprintAlreadyHasBoardException;
import eu.flare.exceptions.notfound.BoardNotFoundException;
import eu.flare.exceptions.notfound.SprintNotFoundException;
import eu.flare.model.Board;
import eu.flare.model.dto.CreateBoardDto;
import eu.flare.model.response.Responses;
import eu.flare.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/board")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public ResponseEntity<?> findBoardByName(@RequestParam("name") String name) {
        try {
            Board board = boardService.findBoard(name);
            return ResponseEntity.ok(new Responses.BoardResponse(board));
        } catch (BoardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.BoardNotFoundResponse(e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBoard(@Valid @RequestBody CreateBoardDto createBoard) {
        try {
            Board board = boardService.createNewBoard(createBoard);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Responses.BoardResponse(board));
        } catch (BoardAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Responses.BoardCreationConflictResponse(e.getMessage()));
        }
    }

    @PutMapping("/{boardId}/sprint/{sprintId}/add")
    public ResponseEntity<?> addBoardToSprint(
            @PathVariable("boardId") long boardId,
            @PathVariable("sprintId") long sprintId
    ) {
        try {
            Board board = boardService.addSprintBoard(boardId, sprintId);
            return ResponseEntity.ok(new Responses.BoardResponse(board));
        } catch (BoardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.BoardNotFoundResponse(e.getMessage()));
        } catch (SprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.SprintNotFoundResponse(e.getMessage()));
        } catch (SprintAlreadyHasBoardException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Responses.BoardCreationConflictResponse(e.getMessage()));
        }
    }
}
