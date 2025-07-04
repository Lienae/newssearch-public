package com.tjeoun.newssearch.config.principal;

import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class PrincipalDetails implements UserDetails {
    private final Member member;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return member.getRoleList().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !member.getRole().equals(UserRole.WITHDRAWAL);
    }

}
