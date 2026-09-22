package com.clase;

public class Provincia {
    public int id;
    public String nm;

    Provincia(int id, String nm) {
        this.id = id;
        this.nm = nm;
    }

    public int getId() {
        return id;
    }

    public String getNm() {
        return nm;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }
}
