package com.example.githubrepoapi.Models.DTOs;

import java.util.List;

public class RepositoryDTO {
    public String name;
    public String login;
    public List<BranchDTO> branches;
    public boolean isFork() {
        return false;
    }
}
