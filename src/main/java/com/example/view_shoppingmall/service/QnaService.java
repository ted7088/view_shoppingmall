package com.example.view_shoppingmall.service;

import com.example.view_shoppingmall.dto.*;
import com.example.view_shoppingmall.entity.Answer;
import com.example.view_shoppingmall.entity.Question;
import com.example.view_shoppingmall.entity.User;
import com.example.view_shoppingmall.repository.AnswerRepository;
import com.example.view_shoppingmall.repository.QuestionRepository;
import com.example.view_shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    // 질문 목록 조회 (페이지네이션)
    public Page<QuestionDTO> getQuestions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questions = questionRepository.findAllByOrderByCreatedAtDesc(pageable);
        return questions.map(this::convertToQuestionDTO);
    }

    // 질문 검색 (페이지네이션)
    public Page<QuestionDTO> searchQuestions(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questions = questionRepository
                .findByTitleContainingOrContentContainingOrderByCreatedAtDesc(keyword, keyword, pageable);
        return questions.map(this::convertToQuestionDTO);
    }

    // 질문 상세 조회
    public QuestionDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다."));
        return convertToQuestionDTOWithAnswer(question);
    }

    // 내 질문 목록 조회
    public List<QuestionDTO> getMyQuestions(Long userId) {
        List<Question> questions = questionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return questions.stream()
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList());
    }

    // 질문 생성
    @Transactional
    public QuestionDTO createQuestion(QuestionRequestDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Question question = new Question();
        question.setUser(user);
        question.setTitle(requestDTO.getTitle());
        question.setContent(requestDTO.getContent());
        question.setIsAnswered(false);

        Question savedQuestion = questionRepository.save(question);
        return convertToQuestionDTO(savedQuestion);
    }

    // 질문 삭제
    @Transactional
    public void deleteQuestion(Long id, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다."));

        if (!question.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 질문만 삭제할 수 있습니다.");
        }

        // 답변이 있으면 먼저 삭제
        answerRepository.findByQuestionId(id).ifPresent(answerRepository::delete);
        questionRepository.delete(question);
    }

    // 답변 생성 (관리자만)
    @Transactional
    public AnswerDTO createAnswer(Long questionId, AnswerRequestDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 관리자 권한 확인
        if (!"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("관리자만 답변을 작성할 수 있습니다.");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다."));

        // 이미 답변이 있는지 확인
        if (answerRepository.existsByQuestionId(questionId)) {
            throw new RuntimeException("이미 답변이 등록된 질문입니다.");
        }

        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setUser(user);
        answer.setContent(requestDTO.getContent());

        Answer savedAnswer = answerRepository.save(answer);

        // 질문의 답변 상태 업데이트
        question.setIsAnswered(true);
        questionRepository.save(question);

        return convertToAnswerDTO(savedAnswer);
    }

    // Question -> QuestionDTO 변환
    private QuestionDTO convertToQuestionDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setUserId(question.getUser().getId());
        dto.setUsername(question.getUser().getUsername());
        dto.setTitle(question.getTitle());
        dto.setContent(question.getContent());
        dto.setIsAnswered(question.getIsAnswered());
        dto.setCreatedAt(question.getCreatedAt());
        return dto;
    }

    // Question -> QuestionDTO 변환 (답변 포함)
    private QuestionDTO convertToQuestionDTOWithAnswer(Question question) {
        QuestionDTO dto = convertToQuestionDTO(question);
        answerRepository.findByQuestionId(question.getId())
                .ifPresent(answer -> dto.setAnswer(convertToAnswerDTO(answer)));
        return dto;
    }

    // Answer -> AnswerDTO 변환
    private AnswerDTO convertToAnswerDTO(Answer answer) {
        AnswerDTO dto = new AnswerDTO();
        dto.setId(answer.getId());
        dto.setQuestionId(answer.getQuestion().getId());
        dto.setUserId(answer.getUser().getId());
        dto.setUsername(answer.getUser().getUsername());
        dto.setContent(answer.getContent());
        dto.setCreatedAt(answer.getCreatedAt());
        return dto;
    }
}
