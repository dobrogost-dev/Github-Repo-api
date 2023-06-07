package com.example.githubrepoapi.Services;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GithubService {     HttpHeaders headers = new HttpHeaders();

    public boolean userNotFound(String username) {
        return false;
    }
    public boolean acceptHeaderIsNotJson(String acceptHeader) {
        return acceptHeader != null && !acceptHeader.contains("application/json");
    }
}
