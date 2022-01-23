package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by a1234 on 17/1/4.
 */

public class CloudPicArray implements Serializable{
    String list;
    int firstPage;
    int nextPage;
    int pageNum;

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    @Override
    public String toString() {
        return "CloudPicArray{" +
                "list='" + list + '\'' +
                ", firstPage=" + firstPage +
                ", nextPage=" + nextPage +
                ", pageNum=" + pageNum +
                '}';
    }
}
