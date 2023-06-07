package com.example.githubrepoapi.Models.DTOs;

import java.util.List;

public class RepositoryDTO {
    public String name;
    public String login;
    public List<BranchDTO> branches;
    public static RepositoryDTO convert(RawRepositoryDTO rawRepository) {
        RepositoryDTO repository = new RepositoryDTO();
        repository.name = rawRepository.name;
        repository.login = rawRepository.owner.login;
        return repository;
    }
    public void setBranches(List<BranchDTO> branches) {
        this.branches = branches;
    }
}
