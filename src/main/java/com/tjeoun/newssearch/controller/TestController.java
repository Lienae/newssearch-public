package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.MailDto;
import com.tjeoun.newssearch.service.MailService;
import com.tjeoun.newssearch.util.CustomMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

@Controller
public class TestController {
    private final MailService mailService;

    public TestController(MailService mailService) {
        this.mailService = mailService;
    }
//
//    @GetMapping("/")
//    public String index() {
//        return "main";
//    }

    // 이메일 전송 (첨부파일 포함) 예제
    @GetMapping("/mailtest")
    @ResponseBody
    public String mailtest()  {
        try {
            // 하드디스크의 파일을 multipartfile로 변환하는 과정
            // 만일 이미 multipartfile인 경우(form action 등)일 경우에는 필요없음

            File file = new File("c:/img.jpg");
            InputStream is = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedImage image = ImageIO.read(is);
            ImageIO.write(image, "jpg", bos);
            byte[] bytes = bos.toByteArray();
            MultipartFile multipartFile = new CustomMultipartFile(bytes, "img.jpg");

            MailDto mailDto = MailDto.builder()
                    .to("받는사람 이메일주소")
                    .subject("이메일 제목")
                    .body("이메일 본문")
                    .attachments(
                            // 첨부파일을 List<MultipartFile> 형식으로 넣을 것
                            new ArrayList<>() {{
                                add(multipartFile);
                            }})
                    .build();
            mailService.sendMail(mailDto);
        } catch(Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        }
        return "success";
    }
}
