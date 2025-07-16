package com.tjeoun.newssearch.config;

import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockMemberFactory {


    @Autowired
    MemberRepository memberRepository;

    public Member createUser(String email) {
        return memberRepository.save(
                Member.builder().email(email).password("test").role(UserRole.USER).name("testuser").build()
        );
    }

    public Member createAdmin(String email) {
        return memberRepository.save(
                Member.builder().email(email).password("test").role(UserRole.ADMIN).name("testadmin").build()
        );
    }

}
