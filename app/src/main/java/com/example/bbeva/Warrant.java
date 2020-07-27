package com.example.bbeva;

import android.content.Intent;
import android.util.SparseIntArray;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class Warrant implements Serializable {
    private static final String TAG = "Warrant";

    private String warrantImg;
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


    public Warrant(String warrantImg, String warrantName,String warrant_sum, String isIncoming, String date, Integer id, String created_at, String partner, Integer partner_id, String cashbox, Integer cashbox_id, String dds, Integer dds_id, String comment, String reason) {
        super();
        this.setWarrantImg(warrantImg);
        this.setWarrantIncoming(Boolean.parseBoolean(isIncoming));
        this.setWarrantName(warrantName);
        this.setWarrantSum(warrant_sum);
        this.setWarrantDate(date);
        this.setWarrantId(id);
        this.setWarrantCreatedAt(created_at);
        this.setWarrantPartner(partner);
        this.setWarrantPartnerId(partner_id);
        this.setWarrantCashbox(cashbox);
        this.setWarrantCashboxId(cashbox_id);
        this.setWarrantDds(dds);
        this.settWarrantDdsId(dds_id);
        this.setWarrantComment(comment);
        this.setWarrantReason(reason);
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
    public void setWarrantCreatedAt(String created_at) { this.created_at = created_at; }
    public void setWarrantPartner(String partner) { this.partner = partner; }
    public void setWarrantPartnerId(Integer partner_id) { this.partner_id = partner_id; }
    public void setWarrantCashbox(String cashbox) { this.cashbox = cashbox; }
    public void setWarrantCashboxId(Integer cashbox_id) { this.cashbox_id = cashbox_id; }
    public void setWarrantDds(String dds) { this.dds = dds; }
    public void settWarrantDdsId(Integer dds_id) { this.dds_id = dds_id; }
    public void setWarrantComment(String comment) { this.comment = comment; }
    public void setWarrantReason(String reason) { this.reason = reason; }


    public String getWarrantSum() {
        DecimalFormat formatter = new DecimalFormat();
        DecimalFormatSymbols symbols=DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        String s =formatter.format(parseInt(warrant_sum)).toString();
        return s + " ₽";
    }
    public String getWarrantImg()           { return warrantImg; }
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
}
