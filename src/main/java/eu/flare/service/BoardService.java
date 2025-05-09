package eu.flare.service;

import eu.flare.exceptions.conflicts.BoardAlreadyExistsException;
import eu.flare.exceptions.conflicts.SprintAlreadyHasBoardException;
import eu.flare.exceptions.notfound.BoardNotFoundException;
import eu.flare.exceptions.notfound.SprintNotFoundException;
import eu.flare.model.Board;
import eu.flare.model.Sprint;
import eu.flare.model.dto.CreateBoardDto;
import eu.flare.repository.BoardRepository;
import eu.flare.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final SprintRepository sprintRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository, SprintRepository sprintRepository) {
        this.boardRepository = boardRepository;
        this.sprintRepository = sprintRepository;
    }

    public Board findBoard(String name) throws BoardNotFoundException {
        return boardRepository.findByName(name)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));
    }

    public Board addSprintBoard(long boardId, long sprintId) throws BoardNotFoundException, SprintNotFoundException, SprintAlreadyHasBoardException {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new SprintNotFoundException("Sprint not found"));
        if (board.getSprint() != null || sprint.getBoard() != null) {
            throw new SprintAlreadyHasBoardException("Sprint already has a board");
        } else {
            board.setSprint(sprint);
            sprint.setBoard(board);
            sprintRepository.save(sprint);
            return boardRepository.save(board);
        }
    }

    public Board createNewBoard(CreateBoardDto createBoard) throws BoardAlreadyExistsException {
        String name = createBoard.name();
        Optional<Board> boardOptional = boardRepository.findByName(name);
        if (boardOptional.isPresent()) {
            throw new BoardAlreadyExistsException("Board already exists");
        }
        Board board = new Board();
        board.setName(name);

        return boardRepository.save(board);
    }
}
