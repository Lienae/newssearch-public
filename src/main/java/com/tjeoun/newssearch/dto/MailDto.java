package com.tjeoun.newssearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MailDto {
    private String to;
    private String subject;
    private String body;
    private List<MultipartFile> attachments;
    public static MailDto createMailDtoForResetPassword(String email, String token) {
        return MailDto.builder()
                .to(email)
                .subject("비밀번호 변경을 요청하셨습니다.")
                .body("비밀번호 변경을 위해서 다음 링크에 접속해주세요.<br>" +
                        "http://tjoeun.3rdproject.o-r.kr/member/resetpassword?token=" + token + "<br>" +
                        "링크는 30분동안만 유지됩니다.")
                .build();
    }
}
