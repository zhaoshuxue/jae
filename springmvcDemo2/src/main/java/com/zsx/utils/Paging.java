package com.zsx.utils;

/**
 * Created by QDHL on 2017/6/6.
 */
public class Paging {
    private Integer page;
    private Integer pageSize;
    public Paging(){

    }
    public Paging(Integer page, Integer pageSize){
        setPage(page);
        setPageSize(pageSize);
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
