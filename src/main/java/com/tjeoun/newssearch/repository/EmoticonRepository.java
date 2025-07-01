package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.Emoticon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmoticonRepository extends JpaRepository<Emoticon, Long> {
    List<Emoticon> findByNewsId(Long newsId);
    Optional<Emoticon> findByMemberIdAndNewsId(Long memberId, Long newsId);
}
