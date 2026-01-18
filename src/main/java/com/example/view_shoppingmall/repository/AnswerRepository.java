package com.example.view_shoppingmall.repository;

import com.example.view_shoppingmall.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // 질문 ID로 답변 조회
    Optional<Answer> findByQuestionId(Long questionId);

    // 질문 ID로 답변 존재 여부 확인
    boolean existsByQuestionId(Long questionId);
}
