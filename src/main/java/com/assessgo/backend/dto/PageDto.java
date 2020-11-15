package com.assessgo.backend.dto;

import java.util.List;

public class PageDto<T>{
    private int totalPages;
    private List<T> content;

    public PageDto() {
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
