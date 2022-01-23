package com.bcnetech.hyphoto.data;

/**
 * Created by a1234 on 2018/6/4.
 */

public class PopSelectData {
    String seletContent;
    boolean isSelect;

    public PopSelectData(String seletContent, boolean isSelect) {
        this.seletContent = seletContent;
        this.isSelect = isSelect;
    }

    public String getSeletContent() {
        return seletContent;
    }

    public void setSeletContent(String seletContent) {
        this.seletContent = seletContent;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
