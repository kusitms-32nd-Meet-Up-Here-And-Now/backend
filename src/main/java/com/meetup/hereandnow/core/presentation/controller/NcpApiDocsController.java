package com.meetup.hereandnow.core.presentation.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@RestController
@Hidden
public class NcpApiDocsController {

    @Value("classpath:secret/ncp-api.json")
    private Resource apiDocs;

    @GetMapping("/docs/ncp-api.json")
    public ResponseEntity<String> getApiDocs() {
        try (Reader reader = new InputStreamReader(apiDocs.getInputStream(), StandardCharsets.UTF_8)) {
            String jsonContent = FileCopyUtils.copyToString(reader);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.ok().headers(headers).body(jsonContent);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("API 문서를 로드할 수 없습니다.");
        }
    }
}
