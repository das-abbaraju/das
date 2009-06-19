function isAutoApply() {
	return $('autoapply').checked;
}

function setInvoiceApply(invoiceID) {
	// when we click the line populate the applied amount
	// / from the balance
	// run the total
	$('invoice_apply_' + invoiceID).value = $('invoice_balance_' + invoiceID).innerHTML;
	updateSingleAppliedAmount(invoiceID);
}

function updateSingleAppliedAmount(invoiceID) {
	// change the autoapply = false
	$('autoapply').checked = false;
	var amount = $('invoice_apply_'+invoiceID).value;
	if (amount.blank())
		amount = 0.00;
	else
		amount = parseFloat(amount);

	if (amount > parseFloat($('invoice_balance_'+invoiceID).innerHTML)) {
		alert("That amount is greater than the balance of the invoice");
		amount = 0.00;
	}
	
	if (isNaN(amount)) {
		alert("That value is not a number");
		amount = 0.00;
	}

	$('invoice_apply_'+invoiceID).value = amount.toFixed(2);
	calculateApplied();
	updateRemainder();
}

function calculateApplied() {
	// sum up all the invoice applied amounts
	// Update the payment_amountApplied
	var total = 0.00;
	$$('input.amountApply').each( function(ele) {
		total += parseFloat(ele.value);
	}.bind(total));
	$('payment_amountApplied').innerHTML = total.toFixed(2);
	updateRemainder()
}

function calculateTotalFromApplied() {
	// sum up all the invoice applied amounts
	// Update the payment_amountApplied
	$('payment_totalAmount').value = parseFloat($('payment_amountApplied').innerHTML).toFixed(2);
	updateRemainder();
}

function autoApply() {
	// (on totalAmount change)
	// if autoapply is checked
	// then take payment.totalAmount and apply to the invoices
	if (isAutoApply()) {
		var total = parseFloat($('payment_totalAmount').value);
		$$('span.invoiceID').each(
			function(ele) {
				var id = parseFloat(ele.innerHTML);
				if (total > 0) {
					var apply = Math.min(parseFloat($('invoice_balance_' + id).innerHTML), total);
					$('invoice_apply_'+id).value = apply.toFixed(2);
					total -= apply;
				} else {
					$('invoice_apply_'+id).value = '0.00';
				}
			});
	}
	calculateApplied();
}

function updateRemainder() {
	// update the remainder (Payment Total - Invoice Total)
	// hide save button if remainder < 0
	var remainder = parseFloat($('payment_totalAmount').value) - parseFloat($('payment_amountApplied').innerHTML);
	if (remainder < 0 || $('payment_totalAmount').value <= 0)
		$('button_div').hide();
	else
		$('button_div').show();

	$('payment_balance').innerHTML = remainder.toFixed(2);
}

function changeTotal() {
	if($('payment_totalAmount').value.blank()) 
		$('payment_totalAmount').value = '0.00';
	
	$('payment_totalAmount').value = parseFloat($('payment_totalAmount').value).toFixed(2);
	autoApply();
}

function applyAll() {
	$$('.invoiceID').each(
		function(ele){
			var invoiceID = ele.innerHTML;
			setInvoiceApply(invoiceID);
		});
		
	calculateApplied();
	calculateTotalFromApplied();
}

function clearAll() {
	$$('.invoiceID').each(
		function(ele){
			var invoiceID = ele.innerHTML;
			$('invoice_apply_'+invoiceID).value = '0.00';
		});
	
	calculateApplied();
	calculateTotalFromApplied();
}