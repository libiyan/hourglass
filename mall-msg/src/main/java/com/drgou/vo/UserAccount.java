package com.drgou.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zz on 2017/11/6.
 * 花生日记用户账户表
 */

public class UserAccount implements Serializable {

    private Long accountId;	//主键
    private Long userId;	//用户ID
    private Double balance; //余额
    private Integer bonusPoints;	//结算处理状态 0：不需要处理、2 本次结算初始、3本次结算处理中、 1：本次最终结算
    private Long uniteId;	//运行商id
    private Double proxyCommission;	//代理佣金
    private Double consumeCommission;	//消费佣金
    private Date updateTime;	//更新时间
    private Double freezeSum;   //冻结金额
    private Double dealBalance; //结佣临时余额
    private Double orderBalance; // 订单收益
    private Double otherBalance; // 其他收益

    public Double getDealBalance() {
        return dealBalance;
    }

    public void setDealBalance(Double dealBalance) {
        this.dealBalance = dealBalance;
    }

    public Double getFreezeSum() {
        return freezeSum;
    }

    public void setFreezeSum(Double freezeSum) {
        this.freezeSum = freezeSum;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Integer bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public Long getUniteId() {
        return uniteId;
    }

    public void setUniteId(Long uniteId) {
        this.uniteId = uniteId;
    }

    public Double getProxyCommission() {
        return proxyCommission;
    }

    public void setProxyCommission(Double proxyCommission) {
        this.proxyCommission = proxyCommission;
    }

    public Double getConsumeCommission() {
        return consumeCommission;
    }

    public void setConsumeCommission(Double consumeCommission) {
        this.consumeCommission = consumeCommission;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Double getOrderBalance() {
        return orderBalance;
    }

    public void setOrderBalance(Double orderBalance) {
        this.orderBalance = orderBalance;
    }

    public Double getOtherBalance() {
        return otherBalance;
    }

    public void setOtherBalance(Double otherBalance) {
        this.otherBalance = otherBalance;
    }
}
