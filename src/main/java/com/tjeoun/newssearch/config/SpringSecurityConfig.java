package com.tjeoun.newssearch.config;

import com.tjeoun.newssearch.config.principal.PrincipalDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    private final PrincipalDetailsService principalDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final String[] whitelist = new String[] {
            "/member/login",
            "/member/register",
            "/css/**",
            "/js/**",
            "/images/**",
            "/member/findpassword",
            "/member/resetpassword"
    };
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf-> csrf

                .ignoringRequestMatchers("/api/Ytn/test", "/api/khan/collect", "/api/donga/collect", "/api/v1/comment/**")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whitelist).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/member/**", "/board/**").authenticated()
                        .requestMatchers("/images/**").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/member/login")
                        .loginProcessingUrl("/member/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/member/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(principalDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}