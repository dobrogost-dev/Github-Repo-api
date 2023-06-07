package com.example.githubrepoapi.Controllers;

import com.example.githubrepoapi.Services.GithubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/repos")
public class GithubController {
    GithubService githubService;
    @Autowired
    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }
    @GetMapping("/{username}")
    public ResponseEntity<Object> getUserRepositories(
            @PathVariable("username") String username,
            @RequestHeader("Accept") String acceptHeader
            ) {
        if (githubService.userNotFound(username)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("status", HttpStatus.NOT_FOUND.value(), "Message", "User not found")
            );
        }
        if (githubService.acceptHeaderIsNotJson(acceptHeader)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .headers(headers)
                    .body(Map.of("status", HttpStatus.NOT_ACCEPTABLE.value(), "Message", "This header is not acceptable"));
        }


        return ResponseEntity.accepted().body(username);
    }
}
