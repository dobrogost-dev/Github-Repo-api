package com.example.githubrepoapi.Services;

import com.example.githubrepoapi.Exceptions.UserNotFoundException;
import com.example.githubrepoapi.Models.DTOs.BranchDTO;
import com.example.githubrepoapi.Models.DTOs.RawBranchDTO;
import com.example.githubrepoapi.Models.DTOs.RawRepositoryDTO;
import com.example.githubrepoapi.Models.DTOs.RepositoryDTO;
import com.google.gson.Gson;
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
    public boolean acceptHeaderIsNotJson(String acceptHeader) {
        //I wasn't sure if i shouldn't use .equals()
        return acceptHeader != null && !acceptHeader.contains("application/json");
    }
    public List<RepositoryDTO> getUserRepositories(String username) throws UserNotFoundException {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GITHUB_API_BASE_URL + "/users/" + username + "/repos"))
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (response.statusCode() == HttpStatus.NOT_FOUND.value()) {
            throw new UserNotFoundException("User not found");
        }
        if (response.statusCode() == HttpStatus.OK.value()) {
                //Getting raw repositories from the response body
                List<RawRepositoryDTO> rawRepositories = List.of(gson.fromJson(response.body(), RawRepositoryDTO[].class));
                //Filtering out forked repositories
                rawRepositories = rawRepositories.stream()
                            .filter(rawRepository -> !rawRepository.isFork())
                            .collect(Collectors.toList());

                List<RepositoryDTO> repositories = new ArrayList<>();
                /*
                * Going through every raw repository and generating raw branches for them, then generating
                * branches out of raw branches, then generating repository out of raw repository and branches
                 */
                for (RawRepositoryDTO rawRepository : rawRepositories) {
                    List<RawBranchDTO> rawBranches = getRawBranches(rawRepository.branches_url);
                    List<BranchDTO> branches = new ArrayList<>();
                    for (RawBranchDTO rawBranch : rawBranches) {
                        branches.add(new BranchDTO(rawBranch.name, rawBranch.commit.sha));
                    }
                    RepositoryDTO repository = RepositoryDTO.generateFromRaw(rawRepository, branches);
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
