package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.MailDto;
import com.tjeoun.newssearch.dto.SignUpDto;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.entity.PasswordToken;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.repository.MemberRepository;
import com.tjeoun.newssearch.repository.PasswordTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordTokenRepository passwordTokenRepository;

    public void save(SignUpDto signUpDto) throws IllegalStateException {
        validateDto(signUpDto);
        memberRepository.save(Member.createMember(signUpDto, passwordEncoder));
    }

    private void validateDto(SignUpDto signUpDto) throws IllegalStateException {
        if(memberRepository.existsByName(signUpDto.getName())) throw new IllegalStateException("이미 사용중인 닉네임입니다.");
        if(memberRepository.existsByEmail(signUpDto.getEmail())) throw new IllegalStateException("이미 사용중인 이메일입니다.");
    }
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("이메일과 연결된 계정이 없습니다."));
    }
    public void update(SignUpDto signUpDto) {
        Member member = findByEmail(signUpDto.getEmail());
        member.setName(signUpDto.getName());
        member.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        memberRepository.save(member);
    }
    public void sendEmailForPasswordReset(String email) throws MessagingException, UsernameNotFoundException {
        if(memberRepository.existsByEmail(email)) {
            String token = UUID.randomUUID().toString().replace("-", "");
            MailDto mailDto = MailDto.createMailDtoForResetPassword(email, token);
            mailService.sendMail(mailDto);
            createPasswordTokenAndSave(email, token);
        } else throw new UsernameNotFoundException("이메일과 연결된 계정이 없습니다.");
    }
    private void createPasswordTokenAndSave(String email, String token) throws UsernameNotFoundException {
        PasswordToken passwordToken = PasswordToken.builder()
                .token(token)
                .member(memberRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("이메일과 연결된 계정이 없습니다.")))
                .expiryDate(LocalDateTime.now().plusMinutes(30L))
                .build();
        passwordTokenRepository.save(passwordToken);
    }
    public Member getMemberFromToken(String token) throws IllegalStateException {
        PasswordToken passwordToken = getAndValidatePasswordToken(token);
        return memberRepository.findById(passwordToken.getMember().getId())
                .orElseThrow(() -> new IllegalStateException("계정을 찾을 수 없습니다."));
    }
    private PasswordToken getAndValidatePasswordToken(String token) throws IllegalStateException {
        PasswordToken passwordToken = passwordTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("토큰을 찾울 수 없습니다."));
        if(passwordToken.isExpired()) throw new IllegalStateException("토큰이 만료되었습니다.");
        return passwordToken;
    }
    public void updatePassword(SignUpDto dto, String token) {
        Member memberFromToken = getMemberFromToken(token);
        Member memberFromDto = memberRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalStateException("계정을 찾을 수 없습니다."));
        if(Objects.equals(memberFromDto.getId(), memberFromToken.getId())) {
            memberFromDto.setPassword(passwordEncoder.encode(dto.getPassword()));
            memberRepository.save(memberFromDto);
            passwordTokenRepository.deleteByToken(token);
        } else throw new IllegalStateException("계정이 일치하지 않습니다. 다시 시도해주세요.");
    }
    public void withdrawl(String email, String password) {
        Member member = findByEmail(email);
        if(passwordEncoder.matches(password, member.getPassword())) {
            member.setRole(UserRole.WITHDRAWAL);
            memberRepository.save(member);
        } else throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
    }
}
