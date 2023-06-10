package com.example.githubrepoapi.service;

import com.example.githubrepoapi.exception.UserNotFoundException;
import com.example.githubrepoapi.model.DTO.RepositoryDTO;
import com.example.githubrepoapi.webclient.GithubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GithubService {
    @Autowired
    private final GithubClient githubClient;
    public GithubService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    public boolean acceptHeaderIsNotJson(String acceptHeader) {
        //I wasn't sure if i shouldn't use .equals()
        return acceptHeader != null && !acceptHeader.contains("application/json");
    }
    public ResponseEntity<Object> getUserRepositories(String username, String acceptHeader) {
        if (acceptHeaderIsNotJson(acceptHeader)) {
            //Setting the header to Json in order to send error message in a proper Json format
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .headers(headers)
                    .body(Map.of("status", HttpStatus.NOT_ACCEPTABLE.value(), "Message", "This header is not acceptable"));
        }
        List<RepositoryDTO> repositories;
        try {
            repositories = githubClient.getUserRepositories(username);
        } catch (UserNotFoundException e) {
            System.out.println("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", HttpStatus.NOT_FOUND.value(), "Message", "User not Found"));

        }
        return ResponseEntity.ok(repositories);
    }
}
