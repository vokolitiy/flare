package eu.flare.service;

import eu.flare.exceptions.conflicts.BoardAlreadyExistsException;
import eu.flare.exceptions.conflicts.SprintAlreadyHasBoardException;
import eu.flare.exceptions.notfound.BoardNotFoundException;
import eu.flare.exceptions.notfound.SprintNotFoundException;
import eu.flare.model.Board;
import eu.flare.model.ProgressType;
import eu.flare.model.Sprint;
import eu.flare.model.Story;
import eu.flare.model.dto.CreateBoardDto;
import eu.flare.repository.BoardRepository;
import eu.flare.repository.SprintRepository;
import eu.flare.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final SprintRepository sprintRepository;
    private final StoryRepository storyRepository;

    @Autowired
    public BoardService(
            BoardRepository boardRepository,
            SprintRepository sprintRepository,
            StoryRepository storyRepository
    ) {
        this.boardRepository = boardRepository;
        this.sprintRepository = sprintRepository;
        this.storyRepository = storyRepository;
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
            addBoardStories(board, sprint);
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

    public Board refreshBoardStories(long boardId) throws BoardNotFoundException {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));
        Sprint assignedSprint = board.getSprint();
        List<Story> sprintStories = assignedSprint.getStories();
        List<Story> sprintTodoStories = findStories(sprintStories, ProgressType.TODO);
        List<Story> sprintInProgressStories = findStories(sprintStories, ProgressType.IN_PROGRESS);
        List<Story> sprintInReviewStories = findStories(sprintStories, ProgressType.IN_REVIEW);
        List<Story> sprintDoneStories = findStories(sprintStories, ProgressType.DONE);

        updateStoryBoards(board, sprintTodoStories, ProgressType.TODO);
        updateStoryBoards(board, sprintInProgressStories, ProgressType.IN_PROGRESS);
        updateStoryBoards(board, sprintInReviewStories, ProgressType.IN_REVIEW);
        updateStoryBoards(board, sprintDoneStories, ProgressType.DONE);

        return boardRepository.save(board);
    }

    private void addBoardStories(Board board, Sprint sprint) {
        List<Story> sprintStories = sprint.getStories();
        List<Story> sprintTodoStories = findStories(sprintStories, ProgressType.TODO);
        List<Story> sprintInProgressStories = findStories(sprintStories, ProgressType.IN_PROGRESS);
        List<Story> sprintInReviewStories = findStories(sprintStories, ProgressType.IN_REVIEW);
        List<Story> sprintDoneStories = findStories(sprintStories, ProgressType.DONE);

        addStoryBoards(board, sprintTodoStories, ProgressType.TODO);
        addStoryBoards(board, sprintInProgressStories, ProgressType.IN_PROGRESS);
        addStoryBoards(board, sprintInReviewStories, ProgressType.IN_REVIEW);
        addStoryBoards(board, sprintDoneStories, ProgressType.DONE);
    }

    private void updateStoryBoards(Board board, List<Story> stories, ProgressType progressType) {
        switch (progressType) {
            case TODO -> board.setTodoStories(stories);
            case IN_PROGRESS -> board.setInProgressStories(stories);
            case IN_REVIEW -> board.setReviewStories(stories);
            case DONE -> board.setDoneStories(stories);
        }
    }

    private void addStoryBoards(Board board, List<Story> stories, ProgressType progressType) {
        setBoardStories(board, stories, progressType);
        stories.forEach(story -> {
            List<Board> boards = getStoryBoards(story, progressType);
            if (boards.isEmpty()) {
                boards.add(board);
                setStoryBoards(story, boards, progressType);
                storyRepository.save(story);
            } else {
                if (!boards.contains(board)) {
                    boards.add(board);
                    setStoryBoards(story, boards, progressType);
                    storyRepository.save(story);
                }
            }
        });
    }

    private void setBoardStories(Board board, List<Story> stories, ProgressType progressType) {
        List<Story> boardStories = getBoardStories(board, progressType);
        if (boardStories.isEmpty()) {
            boardStories.addAll(stories);
            updateBoardWithStories(board, boardStories, progressType);
            boardRepository.save(board);
        } else {
            List<Story> filtered = new ArrayList<>();
            for (Story story : stories) {
                if (!boardStories.contains(story)) {
                    filtered.add(story);
                }
            }
            boardStories.addAll(filtered);
            updateBoardWithStories(board, boardStories, progressType);
            boardRepository.save(board);
        }
    }

    private List<Story> findStories(List<Story> stories, ProgressType progressType) {
        return stories.stream()
                .filter(story -> story.getProgressType() == progressType)
                .collect(Collectors.toList());
    }

    private List<Board> getStoryBoards(Story story, ProgressType progressType) {
        return switch (progressType) {
            case TODO -> story.getBoardsTodoStories();
            case IN_PROGRESS -> story.getBoardsInProgressStories();
            case IN_REVIEW -> story.getBoardsReviewStories();
            case DONE -> story.getBoardsDoneStories();
        };
    }

    private void setStoryBoards(Story story, List<Board> boards, ProgressType progressType) {
        switch (progressType) {
            case TODO -> story.setBoardsTodoStories(boards);
            case IN_PROGRESS -> story.setBoardsInProgressStories(boards);
            case IN_REVIEW -> story.setBoardsReviewStories(boards);
            case DONE -> story.setBoardsDoneStories(boards);
        }
    }

    private List<Story> getBoardStories(Board board, ProgressType progressType) {
        return switch (progressType) {
            case TODO -> board.getTodoStories();
            case IN_PROGRESS -> board.getInProgressStories();
            case IN_REVIEW -> board.getReviewStories();
            case DONE -> board.getDoneStories();
        };
    }

    private void updateBoardWithStories(Board board, List<Story> boardStories, ProgressType progressType) {
        switch (progressType) {
            case TODO -> board.setTodoStories(boardStories);
            case IN_PROGRESS -> board.setInProgressStories(boardStories);
            case IN_REVIEW -> board.setReviewStories(boardStories);
            case DONE -> board.setDoneStories(boardStories);
        }
    }
}
