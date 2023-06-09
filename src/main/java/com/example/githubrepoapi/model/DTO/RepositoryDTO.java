package com.example.githubrepoapi.model.DTO;

import java.util.List;

public class RepositoryDTO {
    public String name;
    public String login;
    public List<BranchDTO> branches;
    public static RepositoryDTO generateFromRaw(RawRepositoryDTO rawRepository, List<BranchDTO> branches) {
        RepositoryDTO repository = new RepositoryDTO();
        repository.name = rawRepository.name;
        repository.login = rawRepository.owner.login;
        repository.branches = branches;
        return repository;
    }
}
