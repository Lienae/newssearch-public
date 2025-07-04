package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.SignUpDto;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(SignUpDto signUpDto) throws IllegalStateException {
        validateDto(signUpDto);
        memberRepository.save(Member.createMember(signUpDto, passwordEncoder));
    }

    private void validateDto(SignUpDto signUpDto) throws IllegalStateException {
        if(memberRepository.existsByName(signUpDto.getName())) throw new IllegalStateException("이미 사용중인 닉네임입니다.");
        if(memberRepository.existsByEmail(signUpDto.getEmail())) throw new IllegalStateException("이미 사용중인 이메일입니다.");
    }
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + "user not found"));
    }
    public void update(SignUpDto signUpDto) {
        Member member = findByEmail(signUpDto.getEmail());
        member.setName(signUpDto.getName());
        member.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        memberRepository.save(member);
    }
}
