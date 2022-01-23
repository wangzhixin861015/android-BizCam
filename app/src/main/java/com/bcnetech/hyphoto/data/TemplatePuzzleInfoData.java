package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/27.
 */
public class TemplatePuzzleInfoData implements Serializable {

    public Data data;

    public class Data{
        //用户登录后的token
        public String token;
        //用户选择的模板id
        public String id;

        public String state;


        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }



}
