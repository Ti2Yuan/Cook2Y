package org.crazyit.cook2y.Bean;

/**
 * Created by chenti on 2016/5/16.
 */
public class SearchBean {

    private String status;
    private SearchItemBean[] tngou;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SearchItemBean[] getTngou() {
        return tngou;
    }

    public void setTngou(SearchItemBean[] tngou) {
        this.tngou = tngou;
    }
}
