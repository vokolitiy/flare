package eu.flare.service;

import eu.flare.exceptions.notfound.BoardNotFoundException;
import eu.flare.model.Board;
import eu.flare.model.dto.add.AddBoardDto;
import eu.flare.model.dto.add.AddBoardStoriesDto;
import eu.flare.repository.BoardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
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
}
