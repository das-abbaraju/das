package com.picsauditing.PICS;

import java.util.HashMap;

public class Email {
	private String fromAddress = "";
	private String toAddress = "";
	private String ccAddress;
	private String bccAddress;
	private String subject;
	private String body;
	private AppPropertiesBean props;
	private HashMap<String, String> tokens = new HashMap<String, String>();

	private AccountBean aBean;
	private ContractorBean cBean;
	
	/////////////// GETTERS/SETTERS //////////////////////
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public String getCcAddress() {
		return ccAddress;
	}
	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}
	public String getBccAddress() {
		return bccAddress;
	}
	public void setBccAddress(String bccAddress) {
		this.bccAddress = bccAddress;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public HashMap<String, String> getTokens() {
		return tokens;
	}
	public void addTokens(String key, String value) {
		this.tokens.put(key, value);
	}
	
	public AccountBean getAccount() {
		return aBean;
	}
	public AccountBean getAccount(String id) throws Exception {
		if (aBean != null && aBean.id.equals(id))
			return aBean;
		aBean = new AccountBean();
		aBean.setFromDB(id);
		return aBean;
	}
	public ContractorBean getContractor() {
		return cBean;
	}
	public ContractorBean getContractor(String id) throws Exception {
		if (cBean != null && cBean.id.equals(id))
			return cBean;
		cBean = new ContractorBean();
		cBean.setFromDB(id);
		return cBean;
	}
	private void setUpProps() {
		if (this.props == null)
			this.props = new AppPropertiesBean();
	}
	
	/////////////////////////
	
	public void setEmailTypeProperty(String property) throws Exception {
		if (property == null) return;
		
		this.setUpProps();
		props.setTokens(this.tokens);
		
		this.body = props.get("email_" + property + "_body");
		this.subject = props.get("email_" + property + "_subject");
	}
	
	public void useDefaultFooter() {
		tokens.put("email", "${main_email}");
		tokens.put("fax", "${main_fax}");
		tokens.put("ext", "");
	}
}
