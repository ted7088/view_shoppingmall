package com.example.view_shoppingmall.repository;

import com.example.view_shoppingmall.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 사용자 ID로 질문 목록 조회
    List<Question> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 페이지네이션 적용 전체 목록 조회 (최신순)
    Page<Question> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 제목 또는 내용으로 검색 (페이지네이션)
    Page<Question> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(
            String title, String content, Pageable pageable);
}
