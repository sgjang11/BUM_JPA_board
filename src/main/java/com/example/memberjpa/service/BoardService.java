package com.example.memberjpa.service;

import com.example.memberjpa.dto.BoardDTO;
import com.example.memberjpa.entity.Board;
import com.example.memberjpa.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    private BoardRepository boardRepository;

    private static final int BLOCK_PAGE_NUM_COUNT = 5;  //  블럭에 존재하는 페이지 수
    private static final int PAGE_POST_COUNT = 4;       //  한 페이지에 존재하는 게시글 수

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional
    public Long savePost(BoardDTO boardDTO) {
        return boardRepository.save(boardDTO.toEntity()).getId();
    }

    @Transactional
    public List<BoardDTO> getBoardList(Integer pageNum) {

        Page<Board> page = boardRepository
                .findAll(PageRequest.of(pageNum -1, PAGE_POST_COUNT, Sort.by(Sort.Direction.ASC,
                        "createdDate")));

        List<Board> boards = page.getContent();
        List<BoardDTO> boardDTOList = new ArrayList<>();

        for (Board board : boards) {
            boardDTOList.add(this.convertEntityToDTO(board));
        }

        return boardDTOList;
    }

    public Integer[] getPageList(Integer curPageNum) {
        Integer[] pageList = new Integer[BLOCK_PAGE_NUM_COUNT];

        //  총 게시글 수
        Double postsTotalCount = Double.valueOf(this.getBoardCount());

        //  총 게시글 수를 기준으로 계산한 마지막 페이지 번호 계산
        Integer totalLastPageNum = (int)(Math.ceil(postsTotalCount/PAGE_POST_COUNT));

        //  현재 페이지를 기준으로 블럭의 마지막 페이지 번호 계산
        Integer blockLastPageNum = (totalLastPageNum > curPageNum + BLOCK_PAGE_NUM_COUNT)
                ? curPageNum + BLOCK_PAGE_NUM_COUNT : totalLastPageNum;

        //  페이지 시작 번호 조정
        curPageNum = (curPageNum <= 3) ? 1 : curPageNum -2;

        //  페이지 번호 할당
        for (int val = curPageNum, i = 0; val <= blockLastPageNum; val++, i++) {
            pageList[i] = val;
        }

        return pageList;
    }

    @Transactional
    public Long getBoardCount() {
        return boardRepository.count();
    }

    @Transactional
    public BoardDTO getPost(Long id) {
        Optional<Board> boardWrapper = boardRepository.findById(id);
        Board board = boardWrapper.get();

        BoardDTO boardDTO = BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .createdDate(board.getCreatedDate())
                .build();

        return boardDTO;
    }

    @Transactional
    public void deletePost(Long id) {
        boardRepository.deleteById(id);
    }

    @Transactional
    public List<BoardDTO> searchPosts(String keyword) {
        List<Board> boards = boardRepository.findByTitleContaining(keyword);
        List<BoardDTO> boardDTOList = new ArrayList<>();

        if (boards.isEmpty()) return boardDTOList;

        for(Board board : boards) {
            boardDTOList.add(this.convertEntityToDTO(board));
        }

        return boardDTOList;
    }

    private BoardDTO convertEntityToDTO(Board board) {
        return BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .createdDate(board.getCreatedDate())
                .build();
    }


}
