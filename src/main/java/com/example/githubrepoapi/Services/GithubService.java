package com.example.githubrepoapi.Services;

import com.example.githubrepoapi.Models.DTOs.BranchDTO;
import com.example.githubrepoapi.Models.DTOs.RawRepositoryDTO;
import com.example.githubrepoapi.Models.DTOs.RepositoryDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubService {
    private static final Gson gson = new Gson();
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
                List<RawRepositoryDTO> rawRepositories = List.of(gson.fromJson(response.body(), RawRepositoryDTO[].class));
                System.out.println(response.body());
                rawRepositories.stream()
                            .filter(rawRepository -> !rawRepository.isFork())
                            .collect(Collectors.toList());
                List<RepositoryDTO> repositories = new ArrayList<>();
                for (RawRepositoryDTO rawRepository : rawRepositories) {
                    RepositoryDTO repository = RepositoryDTO.convert(rawRepository);
                    repository.setBranches(getBranches(rawRepository.branches_url));
                    repositories.add(repository);
                }
                return repositories;
        }
        return Collections.emptyList();
    }
    public List<BranchDTO> getBranches(String branches_url) {
        branches_url = branches_url.replace("{/branch}", "");
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(branches_url))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<BranchDTO>>() {}.getType());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }

        return Collections.emptyList();
    }
}
