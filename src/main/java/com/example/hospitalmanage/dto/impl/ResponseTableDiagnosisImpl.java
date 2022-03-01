package com.example.hospitalmanage.dto.impl;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.model.AnalyzeICDDate;
import lombok.Data;

import javax.persistence.Tuple;
import java.util.List;

public class ResponseTableDiagnosisImpl implements ResponseTable<AnalyzeICDDate> {
    private int allItemsSize;
    private int page;
    private int totalPages;
    private int size;
    private List<AnalyzeICDDate> content;
    private String sort;
    private String columnSort;

    public ResponseTableDiagnosisImpl(RequestTabel request) {
        init(request);
    }

    public ResponseTable init(RequestTabel request) {
        this.setPage(request.getPage());
        this.setSize(request.getSize());
        this.setSort(request.getSort());
        return this;
    }

    @Override
    public int getAllItemsSize() {
        return allItemsSize;
    }

    @Override
    public void setAllItemsSize(int allItemsSize) {
        this.allItemsSize = allItemsSize;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public List<AnalyzeICDDate> getContent() {
        return content;
    }

    @Override
    public void setContent(List<AnalyzeICDDate> content) {
        this.content = content;
    }

    @Override
    public String getSort() {
        return sort;
    }

    @Override
    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public String getColumnSort() {
        return columnSort;
    }

    @Override
    public void setColumnSort(String columnSort) {
        this.columnSort = columnSort;
    }
}
