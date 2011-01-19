package com.intuit.developer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentRet;

public class QBSession {

	private String sessionId = null;
	private Date lastRequest = null;
	private boolean processingSomething = false;
	private QBIntegrationWorkFlow currentStep = null;
	private String lastError = null;
	private String currencyCode = null;
	private String qbID = null;

	private List<ContractorAccount> toInsert = null;
	private List<Invoice> invoicesToInsert = null;

	private List<ContractorAccount> possibleUpdates = new Vector<ContractorAccount>();
	private List<Invoice> possibleInvoiceUpdates = new Vector<Invoice>();
	private List<Payment> possiblePaymentUpdates = new Vector<Payment>();
	private Map<String, Map<String, Object>> toUpdate = new HashMap<String, Map<String, Object>>();
	private Map<String, ReceivePaymentRet> toUpdatePayment = new HashMap<String, ReceivePaymentRet>();

	private Map<String, String> currentBatch = new HashMap<String, String>();

	private List<String> errors = new Vector<String>();

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getLastRequest() {
		return lastRequest;
	}

	public void setLastRequest(Date lastRequest) {
		this.lastRequest = lastRequest;
	}

	public boolean isProcessingSomething() {
		return processingSomething;
	}

	public void setProcessingSomething(boolean processingSomething) {
		this.processingSomething = processingSomething;
	}

	public QBIntegrationWorkFlow getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(QBIntegrationWorkFlow currentStep) {
		this.currentStep = currentStep;
	}

	public List<ContractorAccount> getToInsert() {
		return toInsert;
	}

	public void setToInsert(List<ContractorAccount> toInsert) {
		this.toInsert = toInsert;
	}

	public Map<String, Map<String, Object>> getToUpdate() {
		return toUpdate;
	}

	public void setToUpdate(Map<String, Map<String, Object>> toUpdate) {
		this.toUpdate = toUpdate;
	}

	public List<ContractorAccount> getPossibleUpdates() {
		return possibleUpdates;
	}

	public void setPossibleUpdates(List<ContractorAccount> possibleUpdates) {
		this.possibleUpdates = possibleUpdates;
	}

	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}

	public Map<String, String> getCurrentBatch() {
		return currentBatch;
	}

	public void setCurrentBatch(Map<String, String> currentBatch) {
		this.currentBatch = currentBatch;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<Invoice> getPossibleInvoiceUpdates() {
		return possibleInvoiceUpdates;
	}

	public void setPossibleInvoiceUpdates(List<Invoice> possibleInvoiceUpdates) {
		this.possibleInvoiceUpdates = possibleInvoiceUpdates;
	}

	public List<Invoice> getInvoicesToInsert() {
		return invoicesToInsert;
	}

	public void setInvoicesToInsert(List<Invoice> invoicesToInsert) {
		this.invoicesToInsert = invoicesToInsert;
	}


	public List<Payment> getPossiblePaymentUpdates() {
		return possiblePaymentUpdates;
	}

	public void setPossiblePaymentUpdates(List<Payment> possiblePaymentUpdates) {
		this.possiblePaymentUpdates = possiblePaymentUpdates;
	}

	public Map<String, ReceivePaymentRet> getToUpdatePayment() {
		return toUpdatePayment;
	}

	public void setToUpdatePayment(Map<String, ReceivePaymentRet> toUpdatePayment) {
		this.toUpdatePayment = toUpdatePayment;
	}
	
	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getQbID() {
		return qbID;
	}

	public void setQbID(String qbID) {
		this.qbID = qbID;
	}

	public boolean isUS() {
		return "USD".equals(this.getCurrencyCode());
	}
	
	public boolean isCanada() {
		return "CAD".equals(this.getCurrencyCode());
	}
}
