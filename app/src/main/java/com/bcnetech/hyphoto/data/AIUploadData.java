package com.bcnetech.hyphoto.data;

import java.util.List;

/**
 * Created by yhf on 2018/12/4.
 */

public class AIUploadData {

    private String id = "1";
    private String name = "迷你包";
    private float ration = 0.5f;

    private Content content;

    public static class Content {

        private List<A> a;

        public static class A {
            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }

        public List<A> getA() {
            return a;
        }

        public void setA(List<A> a) {
            this.a = a;
        }
    }

    public float getRation() {
        return ration;
    }

    public void setRation(float ration) {
        this.ration = ration;
    }

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

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
