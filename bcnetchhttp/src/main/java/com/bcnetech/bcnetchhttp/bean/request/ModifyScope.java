package com.bcnetech.bcnetchhttp.bean.request;

/**
 * author: wsBai
 * date: 2018/12/10
 * ids:文件Id
 * scope：授权范围（1：私有，2：公开化，3：protected）
 */
public class ModifyScope {
   String[] ids;
   String scope;

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
