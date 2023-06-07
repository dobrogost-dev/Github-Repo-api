package com.example.githubrepoapi.Services;

import com.example.githubrepoapi.JsonUtil;
import com.example.githubrepoapi.Models.DTOs.RepositoryDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubService {
    private static final String GITHUB_API_BASE_URL = "https://api.github.com";
    public boolean userNotFound(String username) {
        return false;
    }
    public boolean acceptHeaderIsNotJson(String acceptHeader) {
        return acceptHeader != null && !acceptHeader.contains("application/json");
    }
    public List<RepositoryDTO> getUserRepositories(String username) {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GITHUB_API_BASE_URL + "/users/" + username + "/repos"))
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response.statusCode() == HttpStatus.OK.value()) {
                List<RepositoryDTO> repositories = List.of(JsonUtil.fromJson(response.body(), RepositoryDTO[].class));

                // Filter out forked repositories
                return repositories.stream()
                        .filter(repo -> !repo.isFork())
                        .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
