package com.example.githubrepoapi.Models.DTOs;

import java.util.List;

public class RawRepositoryDTO {
    public String name;
    public OwnerDTO owner;
    public boolean fork;
    public String branches_url;
    public boolean isFork() {
        return fork;
    }
}
