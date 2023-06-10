package com.example.githubrepoapi.service;

import com.example.githubrepoapi.exception.UserNotFoundException;
import com.example.githubrepoapi.model.DTO.RepositoryDTO;
import com.example.githubrepoapi.webclient.GithubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
    public List<RepositoryDTO> getUserRepositories(String username) throws UserNotFoundException {
        return githubClient.getUserRepositories(username);
        }
}
