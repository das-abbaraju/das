function submitPayment(option) {
	$('button').value = option;
	cleanPaymentMethods();
	$('PaymentDetail').submit();
}

function cleanPaymentMethods() {
	function cleanUp(e) {
		if (!e.visible())
			e.remove();
	}
	$$('.method_check').each(cleanUp);
	$$('.method_cc').each(cleanUp);
	$$('.method_ccNew').each(cleanUp);
}

function isAutoApply() {
	return $('autoapply').checked;
}

function otherCreditCard() {
	if($('newCreditCard').checked) {
		$$('.method_check').invoke('hide');
		$$('.method_cc').invoke('hide');
		$$('.method_ccNew').invoke('show');
	
		for (var i=0; i<$('PaymentDetail')['method'].length; i++)
			if($('PaymentDetail')['method'][i].value == 'CreditCard')
				$('PaymentDetail')['method'][i].checked = true;
	} else {
		$$('.method_cc').invoke('show');
		$$('.method_ccNew').invoke('hide');
	}
}

function changePaymentMethod(method) {
	if ('Check' == method) {
		$$('.method_cc').invoke('hide');
		$$('.method_check').invoke('show');
		//$('newCreditCard').checked = false;
	}
	else if ('CreditCard' == method) {
		$$('.method_cc').invoke('show');
		$$('.method_check').invoke('hide');
	}
	$$('.method_ccNew').invoke('hide');
}

function setInvoiceApply(invoiceID) {
	// when we click the line populate the applied amount
	// / from the balance
	// run the total
	if (totalLocked)
		$('invoice_apply_' + invoiceID).value = Math.min(parseFloat($('invoice_balance_' + invoiceID).innerHTML), 
				parseFloat($('payment_balance').innerHTML) + parseFloat($('invoice_apply_'+invoiceID).value));
	else
		$('invoice_apply_' + invoiceID).value = $('invoice_balance_' + invoiceID).innerHTML;
	updateSingleAppliedAmount(invoiceID);
}

function updateSingleAppliedAmount(invoiceID) {
	// change the autoapply = false
	if ($('autoapply') != null)
		$('autoapply').checked = false;
	
	var amount = parseFloat($('invoice_apply_'+invoiceID).value);
	
	if (isNaN(amount)) 
		amount = 0.00;
	else if (amount < 0) 
		amount = 0.00;

	if (amount > parseFloat($('invoice_balance_'+invoiceID).innerHTML)) {
		alert("That amount is greater than the balance of the invoice");
		amount = 0.00;
	}

	$('invoice_apply_'+invoiceID).value = amount.toFixed(2);
	calculateApplied();
	updateRemainder();
}

function calculateApplied() {
	// sum up all the invoice applied amounts
	// Update the payment_amountApplied
	var total = payment_amountApplied;
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
		$('toolbox').hide();
	else
		$('toolbox').show();

	$('payment_balance').innerHTML = remainder.toFixed(2);
}

function changeTotal() {
	if($('payment_totalAmount').value.blank()) 
		$('payment_totalAmount').value = '0.00';
	
	if (isNaN($('payment_totalAmount').value)) {
		alert("That value is not a number");
		$('payment_totalAmount').value = '0.00';
	}
	
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
}