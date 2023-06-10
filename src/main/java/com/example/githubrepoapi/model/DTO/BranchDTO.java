package com.example.githubrepoapi.model.DTO;

public class BranchDTO {
    public String name;
    public String sha;
    public BranchDTO(String name, String sha) {
        this.name = name;
        this.sha = sha;
    }
}
