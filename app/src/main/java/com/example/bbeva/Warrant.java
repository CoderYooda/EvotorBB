package com.example.bbeva;

public class Warrant {
    private static final String TAG = "Warrant";

    private int warrantImg;
    private String warrantName;
    private String warrant_sum;

    public Warrant(int warrantImg, String warrantName,String warrant_sum) {
        super();
        this.setWarrantImg(warrantImg);
        this.setWarrantName(warrantName);
        this.setWarrantSum(warrant_sum);
    }

    public String getWarrantName() {
        return warrantName;
    }

    public void setWarrantName(String warrantName) {
        this.warrantName = warrantName;
    }

    public String getWarrantSum() {
        return warrant_sum.toString();
    }

    public void setWarrantImg(int warrantImg) {
        this.warrantImg = warrantImg;
    }

    public void setWarrantSum(String warrant_sum) {
        this.warrant_sum = warrant_sum;
    }

    public int getWarrantImg() {
        return warrantImg;
    }
}
