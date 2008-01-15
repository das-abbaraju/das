package com.picsauditing.beans;

import java.util.ArrayList;
import java.util.List;

import org.richfaces.renderkit.html.SuggestionBoxRenderer;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.DAOFactory;
import com.picsauditing.jpa.entities.Account;

public class NameSuggestionBox {
	
	String property;
	private String rows;
    private String first;
    private String cellspacing;
    private String cellpadding;
    private String minchars;
    private String frequency;
    private String rules;
    private boolean check;
    private String border = "1";
    private String width = "200";
    private String height = "150";
    private String shadowOpacity = "4";
    private List<Account> contractors;
    private String persistenceCtx;
    private String shadowDepth = Integer.toString(SuggestionBoxRenderer.SHADOW_DEPTH);

	
	public NameSuggestionBox(){
		this.rows = "0";
        this.first = "0";
        this.cellspacing = "2";
        this.cellpadding = "2";
        this.minchars = "1";
        this.frequency = "0";
        this.rules = "none";

        setContractors(getAllData());

	}
	
	public List autocomplete(Object suggest) {
        String pref = (String)suggest;
        List result = new ArrayList();
        
        for(Account a : getAllData())
            if((a != null && a.getName().toLowerCase().indexOf(pref.toLowerCase()) == 0) || "".equals(pref))
                 result.add(a);    
       
        return result;
    }
	
	private List<Account> getAllData(){
		DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, getPersistenceCtx());
		AccountDAO dao = daof.getAccountDAO();
		return dao.executeQuery("select a.id, a.name from Account a where a.type='Contractor' and a.active='Y'", null);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getCellspacing() {
		return cellspacing;
	}

	public void setCellspacing(String cellspacing) {
		this.cellspacing = cellspacing;
	}

	public String getCellpadding() {
		return cellpadding;
	}

	public void setCellpadding(String cellpadding) {
		this.cellpadding = cellpadding;
	}

	public String getMinchars() {
		return minchars;
	}

	public void setMinchars(String minchars) {
		this.minchars = minchars;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getShadowOpacity() {
		return shadowOpacity;
	}

	public void setShadowOpacity(String shadowOpacity) {
		this.shadowOpacity = shadowOpacity;
	}

	public List<Account> getContractors() {
		return contractors;
	}

	public void setContractors(List<Account> contractors) {
		this.contractors = contractors;
	}	

	public String getPersistenceCtx() {
		return persistenceCtx;
	}

	public void setPersistenceCtx(String persistenceCtx) {
		this.persistenceCtx = persistenceCtx;
	}
	

}
