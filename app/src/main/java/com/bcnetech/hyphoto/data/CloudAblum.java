package com.bcnetech.hyphoto.data;

import java.util.ArrayList;

/**
 * Created by a1234 on 2017/12/11.
 */

public class CloudAblum {
    public ArrayList<CatalogFileCountRes> catalogFileCountRes;
    public ArrayList<CloudPicture> bimageCatalogList;

    public ArrayList<CatalogFileCountRes> getCatalogFileCountRes() {
        return catalogFileCountRes;
    }

    public void setCatalogFileCountRes(ArrayList<CatalogFileCountRes> catalogFileCountRes) {
        this.catalogFileCountRes = catalogFileCountRes;
    }

    public ArrayList<CloudPicture> getBimageCatalogList() {
        return bimageCatalogList;
    }

    public void setBimageCatalogList(ArrayList<CloudPicture> bimageCatalogList) {
        this.bimageCatalogList = bimageCatalogList;
    }

    public class CatalogFileCountRes {
        public String count;
        public String catalogId;
        public String format;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getCatalogId() {
            return catalogId;
        }

        public void setCatalogId(String catalogId) {
            this.catalogId = catalogId;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }

    public void setCount() {
        if (catalogFileCountRes != null && catalogFileCountRes.size() != 0 && bimageCatalogList != null && bimageCatalogList.size() != 0) {
            for (int i = 0; i < catalogFileCountRes.size(); i++) {
                for (int j = 0; j < bimageCatalogList.size(); j++) {
                    if (catalogFileCountRes.get(i).getCatalogId().equals(bimageCatalogList.get(j).getId())) {
                        bimageCatalogList.get(j).setCount(Integer.parseInt(catalogFileCountRes.get(i).getCount()));
                    }

                }
            }
        }
    }
}
