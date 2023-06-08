package com.example.githubrepoapi.Controllers;

import com.example.githubrepoapi.Exceptions.UserNotFoundException;
import com.example.githubrepoapi.Models.DTOs.RepositoryDTO;
import com.example.githubrepoapi.Services.GithubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        if (githubService.acceptHeaderIsNotJson(acceptHeader)) {
            //Setting the header to Json in order to give error message a proper Json format
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .headers(headers)
                    .body(Map.of("status", HttpStatus.NOT_ACCEPTABLE.value(), "Message", "This header is not acceptable"));
        }
        List<RepositoryDTO> repositories;
        try {
            repositories = githubService.getUserRepositories(username);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("status", HttpStatus.NOT_FOUND.value(), "Message", "User not found")
            );
        }
        return ResponseEntity.ok(repositories);
    }
}
