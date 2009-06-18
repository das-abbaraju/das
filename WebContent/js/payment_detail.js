
function setInvoiceApply(invoiceID) {
	// when we click the line populate the applied amount
	/// from the balance
	// run the total
	// $('invoice_apply_<s:property value="id" />').value = $('invoice_balance_<s:property value="id" />').innerHTML
	calculateTotalFromApplied();
}

function updateSingleAppliedAmount() {
	// change the autoapply = false
	calculateTotalFromApplied();
}

function calculateTotalFromApplied() {
	// sum up all the invoice applied amounts
	// Update the payment_amountApplied
}

function autoApply() {
	// (on totalAmount change)
	// if autoapply is checked
	// then take payment.totalAmount and apply to the invoices
}

function updateRemainder() {
	// update the remainder (Payment Total - Invoice Total)
	// hide save button if remainder < 0
}
