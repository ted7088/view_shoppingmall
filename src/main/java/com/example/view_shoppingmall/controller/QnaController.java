package com.example.view_shoppingmall.controller;

import com.example.view_shoppingmall.dto.*;
import com.example.view_shoppingmall.entity.User;
import com.example.view_shoppingmall.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QnaController {

    private final QnaService qnaService;

    // 질문 목록 조회 (페이지네이션)
    @GetMapping
    public ResponseEntity<Page<QuestionDTO>> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<QuestionDTO> questions = qnaService.getQuestions(page, size);
        return ResponseEntity.ok(questions);
    }

    // 질문 검색
    @GetMapping("/search")
    public ResponseEntity<Page<QuestionDTO>> searchQuestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<QuestionDTO> questions = qnaService.searchQuestions(keyword, page, size);
        return ResponseEntity.ok(questions);
    }

    // 질문 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable("id") Long id) {
        try {
            QuestionDTO question = qnaService.getQuestionById(id);
            return ResponseEntity.ok(question);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 내 질문 목록 조회
    @GetMapping("/my")
    public ResponseEntity<?> getMyQuestions(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }
        List<QuestionDTO> questions = qnaService.getMyQuestions(user.getId());
        return ResponseEntity.ok(questions);
    }

    // 질문 생성
    @PostMapping
    public ResponseEntity<?> createQuestion(
            @RequestBody QuestionRequestDTO requestDTO,
            @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            QuestionDTO createdQuestion = qnaService.createQuestion(requestDTO, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 질문 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            qnaService.deleteQuestion(id, user.getId());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 답변 생성 (관리자만)
    @PostMapping("/{id}/answer")
    public ResponseEntity<?> createAnswer(
            @PathVariable("id") Long id,
            @RequestBody AnswerRequestDTO requestDTO,
            @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            AnswerDTO createdAnswer = qnaService.createAnswer(id, requestDTO, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAnswer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
