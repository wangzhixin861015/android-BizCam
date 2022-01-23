package com.bcnetech.hyphoto.data;

import java.util.List;

/**
 * Created by a1234 on 17/2/27.
 *
 *   "data": {
 "lastPage": 1,
 "navigatepageNums": [
 1
 ],
 "startRow": 1,
 "hasNextPage": false,
 "prePage": 0,
 "nextPage": 0,
 "endRow": 4,
 "orderBy": null,
 "pageSize": 10,
 "list": [
 */

public class UserlistData {
    private int lastPage;
    private int startRow;
    private int prePage;
    private int nextPage;
    private int endRow;
    private int pageSize;
    private List<UserTemplateData>list;

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<UserTemplateData> getList() {
        return list;
    }

    public void setList(List<UserTemplateData> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "MarketlistData{" +
                "lastPage=" + lastPage +
                ", startRow=" + startRow +
                ", prePage=" + prePage +
                ", nextPage=" + nextPage +
                ", endRow=" + endRow +
                ", pageSize=" + pageSize +
                ", list=" + list +
                '}';
    }
}
