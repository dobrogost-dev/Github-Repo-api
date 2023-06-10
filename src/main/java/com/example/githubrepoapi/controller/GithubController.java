package com.example.githubrepoapi.controller;

import com.example.githubrepoapi.exception.UserNotFoundException;
import com.example.githubrepoapi.model.DTO.RepositoryDTO;
import com.example.githubrepoapi.service.GithubService;
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
        return githubService.getUserRepositories(username, acceptHeader);
    }
}
