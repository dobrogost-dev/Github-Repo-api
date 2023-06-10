package com.example.githubrepoapi.webclient;

import com.example.githubrepoapi.exception.UserNotFoundException;
import com.example.githubrepoapi.model.DTO.*;
import com.google.gson.Gson;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class GithubClient {
    private static final String GITHUB_API_BASE_URL = "https://api.github.com";
    private static final String token = "put token here";
    private RestTemplate restTemplate;
    private final HttpHeaders headers;
    public GithubClient(Gson gson, RestTemplate restTemplate, HttpHeaders httpHeaders) {
        this.restTemplate = restTemplate;
        this.headers = httpHeaders;
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Bearer " + token);
    }
    public List<RepositoryDTO> getUserRepositories(String username) throws UserNotFoundException {
        String url = GITHUB_API_BASE_URL + "/users/" + username + "/repos";

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<List<RawRepositoryDTO>> responseEntity = restTemplate.exchange(requestEntity,
                new ParameterizedTypeReference<>() {});
        List<RawRepositoryDTO> rawRepositories = responseEntity.getBody();

        if (responseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            throw new UserNotFoundException("User not found");
        }
        if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return Collections.emptyList();
        }
        //Filtering out forked repositories
        rawRepositories = rawRepositories.stream()
                .filter(rawRepository -> !rawRepository.isFork())
                .collect(Collectors.toList());
        List<RepositoryDTO> repositories = new ArrayList<>();
        /*
         * Going through every raw repository and generating raw branches for them, then generating
         * branches out of raw branches, then generating repository out of raw repository and branches
         */
        rawRepositories.forEach(rawRepository -> {
            List<RawBranchDTO> rawBranches = getRawBranches(rawRepository.branches_url);
            List<BranchDTO> branches = rawBranches.stream()
                    .map(rawBranch -> new BranchDTO(rawBranch.name, rawBranch.commit.sha))
                    .collect(Collectors.toList());
            RepositoryDTO repository = RepositoryDTO.generateFromRaw(rawRepository, branches);
            repositories.add(repository);
        });
        return repositories;
    }
    public List<RawBranchDTO> getRawBranches(String branches_url) {
        branches_url = branches_url.replace("{/branch}", "");
        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(branches_url));
        ResponseEntity<List<RawBranchDTO>> responseEntity = restTemplate.exchange(requestEntity,
                new ParameterizedTypeReference<>() {});
        return responseEntity.getBody();
    }
}
