package com.example.githubrepoapi.Services;

import com.example.githubrepoapi.Exceptions.UserNotFoundException;
import com.example.githubrepoapi.Models.DTOs.BranchDTO;
import com.example.githubrepoapi.Models.DTOs.RawBranchDTO;
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
    private static final String token = "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";
    public boolean userNotFound(String username) {
        return false;
    }
    public boolean acceptHeaderIsNotJson(String acceptHeader) {
        return acceptHeader != null && !acceptHeader.contains("application/json");
    }
    public List<RepositoryDTO> getUserRepositories(String username) throws UserNotFoundException {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GITHUB_API_BASE_URL + "/users/" + username + "/repos"))
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (response.statusCode() == 404) {
            throw new UserNotFoundException("User not found");
        }
        if (response.statusCode() == HttpStatus.OK.value()) {
                List<RawRepositoryDTO> rawRepositories = List.of(gson.fromJson(response.body(), RawRepositoryDTO[].class));
                rawRepositories = rawRepositories.stream()
                            .filter(rawRepository -> !rawRepository.isFork())
                            .collect(Collectors.toList());
                List<RepositoryDTO> repositories = new ArrayList<>();
                for (RawRepositoryDTO rawRepository : rawRepositories) {
                    RepositoryDTO repository = RepositoryDTO.generateFromRaw(rawRepository);
                    List<RawBranchDTO> rawBranches = getRawBranches(rawRepository.branches_url);
                    List<BranchDTO> branches = new ArrayList<>();
                    for (RawBranchDTO rawBranch : rawBranches) {
                        branches.add(new BranchDTO(rawBranch.name, rawBranch.commit.sha));
                    }
                    repository.setBranches(branches);
                    repositories.add(repository);
                }
                return repositories;
        }
        return Collections.emptyList();
    }
    public List<RawBranchDTO> getRawBranches(String branches_url) {
        branches_url = branches_url.replace("{/branch}", "");
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(branches_url))
                .header("Authorization", "Bearer " + token)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return List.of(gson.fromJson(response.body(), RawBranchDTO[].class));
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
        return Collections.emptyList();
    }
}
