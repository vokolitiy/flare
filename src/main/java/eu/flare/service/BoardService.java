package eu.flare.service;

import eu.flare.exceptions.notfound.BoardNotFoundException;
import eu.flare.exceptions.notfound.SprintNotFoundException;
import eu.flare.model.Board;
import eu.flare.model.Sprint;
import eu.flare.model.dto.add.AddBoardDto;
import eu.flare.model.dto.add.AddBoardStoriesDto;
import eu.flare.repository.BoardRepository;
import eu.flare.repository.SprintRepository;
import jakarta.transaction.Transactional;
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
        return boardRepository.findByName(name).orElseThrow(() -> new BoardNotFoundException("Board not found"));
    }

    public Board createOrUpdateBoardStories(long boardId, AddBoardStoriesDto dto) {
        return null;
    }

    @Transactional(rollbackOn = Exception.class)
    public Board createBoard(AddBoardDto addBoardDto) {
        Board board = new Board();
        board.setName(addBoardDto.name());
        return boardRepository.save(board);
    }

    @Transactional(rollbackOn = Exception.class)
    public Board addSprintBoard(long boardId, long sprintId) throws SprintNotFoundException, BoardNotFoundException {
        Optional<Board> boardOptional = boardRepository.findById(boardId);
        if (boardOptional.isEmpty()) {
            throw new BoardNotFoundException("Board not found");
        }
        Board board = boardOptional.get();
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new SprintNotFoundException("Sprint not found"));
        if (board.getSprintBoard() == null) {
            board.setSprintBoard(sprint);
            sprint.setSprintBoard(board);
            sprintRepository.save(sprint);
            return boardRepository.save(board);
        }

        return board;
    }
}
