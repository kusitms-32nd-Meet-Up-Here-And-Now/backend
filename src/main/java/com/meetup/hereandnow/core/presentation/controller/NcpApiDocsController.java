package com.meetup.hereandnow.core.presentation.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@Hidden
public class NcpApiDocsController {

    @Value("classpath:secret/ncp-api.json")
    private Resource apiDocs;

    @GetMapping("/docs/ncp-api.json")
    public ResponseEntity<String> getApiDocs() throws IOException {
        String jsonContent = new String(Files.readAllBytes(apiDocs.getFile().toPath()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.ok().headers(headers).body(jsonContent);
    }
}
