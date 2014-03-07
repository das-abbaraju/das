package com.picsauditing.actions.billing;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;

public class MockBrainTreeTransact extends PicsActionSupport {
    static String RESPONSE_TEXT_SUCCESS = "Customer+Added";
    static String RESPONSE_SUCCESS = "1";
    static String RESPONSE_CODE_SUCCESS = "100";
    private String redirect;
    private String hash;
    private Integer key_id;
    private String orderid;
    private String amount;
    private String time;
    private String company;
    private Integer customer_vault_id;
    private String customer_vault;

    @Anonymous
    @Override
    public String execute() throws Exception {
        return completeRegistration();
    }

    private String redirectURL() {
        StringBuffer url = new StringBuffer(redirect)
                .append("&response=").append(RESPONSE_SUCCESS)
                .append("&responsetext=").append(RESPONSE_TEXT_SUCCESS)
                .append("&authcode=")
                .append("&transactionid=")
                .append("&avsresponse=")
                .append("&cvvresponse=")
                .append("&orderid=")
                .append("&type=")
                .append("&response_code=").append(RESPONSE_CODE_SUCCESS)
                .append("&customer_vault_id=").append(customer_vault_id)
                .append("&username=1884502")
                .append("&time=").append(time.toString())
                .append("&amount=")
                .append("&hash=").append(hash);
        return url.toString();
    }

    @Anonymous
    public String completeRegistration() throws Exception {
        return setUrlForRedirect(redirectURL());
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getKey_id() {
        return key_id;
    }

    public void setKey_id(Integer key_id) {
        this.key_id = key_id;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getCustomer_vault_id() {
        return customer_vault_id;
    }

    public void setCustomer_vault_id(Integer customer_vault_id) {
        this.customer_vault_id = customer_vault_id;
    }

    public String getCustomer_vault() {
        return customer_vault;
    }

    public void setCustomer_vault(String customer_vault) {
        this.customer_vault = customer_vault;
    }
}