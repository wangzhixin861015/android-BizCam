package com.bcnetech.bcnetchhttp.bean.response;

import java.util.List;

/**
 * Created by a1234 on 17/2/28.
 *   "lastPage": 1,
 "navigatepageNums": [
 1
 ],
 "startRow": 1,
 "hasNextPage": false,
 "prePage": 0,
 "nextPage": 0,
 "endRow": 6,
 "orderBy": null,
 "pageSize": 1000,
 "list": [
 */

public class MarketParamsIndexListData {
    private int startRow;
    private int endRow;
    private int lastPage;

    public class PresetParmIndexManageItem{
        private String id;   //分类ID
        private String name;  //分类名字
        private String fileId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }
    }


    private List<PresetParmIndexManageItem> list;

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public List<PresetParmIndexManageItem> getList() {
        return list;
    }

    public void setList(List<PresetParmIndexManageItem> list) {
        this.list = list;
    }
}
