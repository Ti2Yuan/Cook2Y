package org.crazyit.cook2y.Bean;

/**
 * Created by chenti on 2016/4/23.
 */
public class FoodBean {

    private String status;
    private int total;
    private FoodItemBean[] tngou;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public FoodItemBean[] getTngou() {
        return tngou;
    }

    public void setTngou(FoodItemBean[] tngou) {
        this.tngou = tngou;
    }
}
