package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.SignUpDto;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(SignUpDto signUpDto) {
        memberRepository.save(Member.createMember(signUpDto, passwordEncoder));
    }

}
