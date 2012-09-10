package com.picsauditing.util.braintree;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import com.braintreegateway.exceptions.BraintreeException;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.util.braintree.BrainTreeResponse;
import com.picsauditing.util.braintree.BrainTreeResponse.BrainTreeRequest;
import com.picsauditing.util.braintree.CreditCard;

public class BrainTreeService {

	@Autowired
	BrainTreeRequest brainTree;

	public CreditCard getCreditCard(int contractorId) throws Exception {

		BrainTreeResponse response = brainTree.creditCardFor(contractorId);
		try {
			return new CreditCard(response);
		} catch (BraintreeException e) {
			return null;
		}
	}

	public void deleteCreditCard(int contractorId) throws Exception {
		brainTree.deleteCreditCardFor(contractorId);
	}

	public String getTransactionCondition(String transactionID) throws Exception {
		BrainTreeResponse response = brainTree.transactionConditionFor(transactionID);
		return response.get("condition");
	}

	public boolean processPayment(Payment payment, Invoice invoice) throws Exception {
		BrainTreeResponse response = brainTree.processPayment(payment, invoice);

		if (response.get("response").equals("1")) {
			payment.setTransactionID(response.get("transactionid"));
			return true;
		} else if (response.get("response").equals("2")) {
			String responseCode = response.get("response_code");
			throw new BrainTreeCardDeclinedException(response.get("responsetext") + " "
					+ BrainTreeCodes.getTransactionResponse(responseCode));
		} else {
			String responseCode = response.get("response_code");
			throw new BrainTreeServiceErrorResponseException(response.get("responsetext") + " "
					+ BrainTreeCodes.getTransactionResponse(responseCode));
		}
	}

	public boolean processRefund(String transactionID, BigDecimal amount) throws Exception {
		BrainTreeResponse response = brainTree.processRefund(transactionID, amount);
		return processed(response);
	}

	public boolean processCancellation(String transactionID) throws Exception {
		BrainTreeResponse response = brainTree.processCancellation(transactionID);
		return processed(response);
	}

	/**
	 * Voiding a transaction will cancel an existing sale or captured authorization from actually charging the card. In
	 * addition, non-captured authorizations can be voided to prevent any future capture. Note however, that the amount
	 * is still reserved on the card and will take a few days to expire. You will have to call the issuing bank to
	 * request that the authorization be removed if the customer does not want to wait for it to expire on its own.
	 * Voids can only occur if the transaction has not been settled; settled transactions should be refunded.
	 *
	 * @param transactionid
	 * @throws BrainTreeLoginException
	 *             , BrainTreeServiceErrorResponseException, IOException
	 */

	public boolean voidTransaction(String transactionID) throws Exception {
		BrainTreeResponse response = brainTree.voidTransaction(transactionID);
		return processed(response);
	}

	private boolean processed(BrainTreeResponse response) throws Exception {
		if (response.get("response").equals("1"))
			return true;

		throw new BraintreeException(response.get("responsetext"));
	}
}
