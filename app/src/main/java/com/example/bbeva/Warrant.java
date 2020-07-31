package com.example.bbeva;

import android.content.Intent;
import android.util.SparseIntArray;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class Warrant implements Serializable {
    private static final String TAG = "Warrant";

    private String warrantImg;
    private Integer payment;
    private Double discount;
    private Boolean isIncoming;
    private String warrantName;
    private String warrant_sum;
    private String date;
    private Integer id;
    private String created_at;
    private String partner;
    private Integer partner_id;
    private String cashbox;
    private Integer cashbox_id;
    private String dds;
    private Integer dds_id;
    private String comment;
    private String reason;
    private JSONArray warrant_items;


    public Warrant(JSONObject warrant) throws JSONException {
        super();
        this.setWarrantImg("ic_up");
        this.setWarrantDiscount(Double.parseDouble(warrant.getString("disc")));
        this.setWarrantPayment(Integer.parseInt(warrant.getString("payment")));
        this.setWarrantIncoming(Boolean.parseBoolean(warrant.getString("isIncoming")));
        this.setWarrantName(warrant.getString("name"));
        this.setWarrantSum(warrant.getString("summ"));
        this.setWarrantDate(warrant.getString("date"));
        this.setWarrantId(Integer.parseInt(warrant.getString("id")));
        this.setWarrantCreatedAt(warrant.getString("created_at"));
        this.setWarrantPartner(warrant.getString("name"));
        this.setWarrantPartnerId(Integer.parseInt(warrant.getString("partner_id")));
        this.setWarrantCashbox(warrant.getString("cashbox"));
        this.setWarrantCashboxId(Integer.parseInt(warrant.getString("cashbox_id")));
        this.setWarrantDds(warrant.getString("name"));
        this.settWarrantDdsId(Integer.parseInt(warrant.getString("dds_id")));
        this.setWarrantComment(warrant.getString("comment"));
        this.setWarrantReason(warrant.getString("reason"));
        this.setWarrantItems(warrant.getJSONArray("items"));
    }

    public String getWarrantName() {
        return warrantName;
    }
    public Boolean getWarrantIncoming() { return isIncoming; }

    public void setWarrantName(String warrantName) { this.warrantName = warrantName; }
    public void setWarrantIncoming(Boolean isIncoming) { this.isIncoming = isIncoming; }
    public void setWarrantImg(String warrantImg) {
        this.warrantImg = warrantImg;
    }
    public void setWarrantSum(String warrant_sum) { this.warrant_sum = warrant_sum; }
    public void setWarrantDate(String date) { this.date = date; }

    public void setWarrantId(Integer id) { this.id = id; }
    public void setWarrantDiscount(Double discount) { this.discount = discount; }
    public void setWarrantPayment(Integer payment) { this.payment = payment; }
    public void setWarrantCreatedAt(String created_at) { this.created_at = created_at; }
    public void setWarrantPartner(String partner) { this.partner = partner; }
    public void setWarrantPartnerId(Integer partner_id) { this.partner_id = partner_id; }
    public void setWarrantCashbox(String cashbox) { this.cashbox = cashbox; }
    public void setWarrantCashboxId(Integer cashbox_id) { this.cashbox_id = cashbox_id; }
    public void setWarrantDds(String dds) { this.dds = dds; }
    public void settWarrantDdsId(Integer dds_id) { this.dds_id = dds_id; }
    public void setWarrantComment(String comment) { this.comment = comment; }
    public void setWarrantReason(String reason) { this.reason = reason; }
    public void setWarrantItems(JSONArray warrant_items) {
        this.warrant_items = warrant_items;
    }


    public String getWarrantSum() {
        DecimalFormat formatter = new DecimalFormat();
        DecimalFormatSymbols symbols=DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        String s =formatter.format(parseInt(warrant_sum)).toString();
        return s + " ₽";
    }
    public String getWarrantImg()           { return warrantImg; }
    public Integer getWarrantPayment()      { return payment; }
    public Double getWarrantDiscount()      { return discount; }
    public String getWarrantDate()          { return date; }
    public Integer getWarrantId()           { return id; }
    public String getWarrantCreatedAt()     { return created_at; }
    public String getWarrantPartner()       { return partner; }
    public Integer getWarrantPartnerId()    { return partner_id; }
    public String getWarrantCashbox()       { return cashbox; }
    public Integer getWarrantCashboxId()    { return cashbox_id; }
    public String getWarrantDds()           { return dds; }
    public Integer getWarrantDdsId()        { return dds_id; }
    public String getWarrantComment()       { return comment; }
    public String getWarrantReason()        { return reason; }
    public JSONArray getWarrantItems()      { return warrant_items; }
}
