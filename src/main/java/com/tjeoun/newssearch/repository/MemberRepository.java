package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
  @Query("SELECT m FROM Member m WHERE m.is_blind = false")
  Page<Member> findByIs_blindFalse(Pageable pageable);

  Page<Member> findAll(Pageable pageable);

}
