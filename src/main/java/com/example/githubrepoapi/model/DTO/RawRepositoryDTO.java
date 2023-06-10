package com.example.githubrepoapi.model.DTO;

public class RawRepositoryDTO {
    public String name;
    public OwnerDTO owner;
    public boolean fork;
    public String branches_url;
    public boolean isFork() {
        return fork;
    }
}
