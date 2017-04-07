package me.jarkimzhu.libs.protocol.json.test;

import me.jarkimzhu.libs.utils.annotation.NumberFormat;

/**
 * Created by dev on 2016/7/27.
 */
public class PaymentAccountBean {
    private String paymentAccountId;
    /**
     * <pre>
     * 可用余额
     * 表字段 : payment_account.balance
     * </pre>
     *
     */
    @NumberFormat()
    private Double balance = 0d;

    /**
     * <pre>
     * 冻结金额
     * 表字段 : payment_account.freeze
     * </pre>
     *
     */
    @NumberFormat()
    private Double freeze = 0d;

    public PaymentAccountBean(){

    }

    public PaymentAccountBean(String paymentAccountId, Double balance, Double freeze) {
        this.paymentAccountId = paymentAccountId;
        this.balance = balance;
        this.freeze = freeze;
    }

    public PaymentAccountBean(String paymentAccountId) {
        this.paymentAccountId = paymentAccountId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getFreeze() {
        return freeze;
    }

    public void setFreeze(Double freeze) {
        this.freeze = freeze;
    }

    public String getPaymentAccountId() {
        return paymentAccountId;
    }

    public void setPaymentAccountId(String paymentAccountId) {
        this.paymentAccountId = paymentAccountId;
    }
}
