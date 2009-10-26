function findCreditCard(id) {
	startThinking({div:'braintree', 'message':'Retrieving Credit Card Information'});

	var data = {'id':id, 'button':'findcc'};
	$.post('PaymentDetailAjax.action', data, function(text, status) {
		if (status=='success') {
			$('#paymentMethodList').append(text);
			stopThinking( {div: 'braintree' } );
		}
	});
}

function cleanPaymentMethods() {
	function cleanUp(i, e) {
		if ($(e).is(':hidden'))
			$(e).remove();
	}
	$('.method_check').each(cleanUp);
	$('.method_cc').each(cleanUp);
	$('.method_ccNew').each(cleanUp);
}

function isAutoApply() {
	return $('#autoapply').is(':checked');
}

function changePaymentMethod(method) {
	if ('Check' == method) {
		$('.method_cc').slideUp();
		$('.method_check').slideDown();
		//$('newCreditCard').checked = false;
	}
	else if ('CreditCard' == method) {
		$('.method_cc').slideDown();
		$('.method_check').slideUp();
	}
	$('.method_ccNew').slideUp();
}

function setInvoiceApply(invoiceID) {
	// when we click the line populate the applied amount
	// / from the balance
	// run the total
	if (totalLocked)
		$('#invoice_apply_' + invoiceID).val(Math.min(parseFloat($('#invoice_balance_' + invoiceID).text()), 
				parseFloat($('#payment_balance').text()) + parseFloat($('#invoice_apply_'+invoiceID).val())));
	else
		$('#invoice_apply_' + invoiceID).val($('#invoice_balance_' + invoiceID).text());
	updateSingleAppliedAmount(invoiceID);
}

function updateSingleAppliedAmount(invoiceID) {
	// change the autoapply = false
	if ($('#autoapply') != null)
		$('#autoapply').attr({checked: false});
	
	var amount = parseFloat($('#invoice_apply_'+invoiceID).val());
	
	if (isNaN(amount)) 
		amount = 0.00;
	else if (amount < 0) 
		amount = 0.00;

	if (amount > parseFloat($('#invoice_balance_'+invoiceID).text())) {
		alert("That amount is greater than the balance of the invoice");
		amount = 0.00;
	}

	$('#invoice_apply_'+invoiceID).val(amount.toFixed(2));
	calculateApplied();
	updateRemainder();
}

function calculateApplied() {
	// sum up all the invoice applied amounts
	// Update the payment_amountApplied
	var total = payment_amountApplied;
	$('input.amountApply').each( function(i, ele) {
		total += parseFloat(ele.value);
	});
	$('#payment_amountApplied').html(total.toFixed(2));
	updateRemainder();
}

function calculateTotalFromApplied() {
	// sum up all the invoice applied amounts
	// Update the payment_amountApplied
	$('#payment_totalAmount').val(parseFloat($('#payment_amountApplied').text()).toFixed(2));
	updateRemainder();
}

function autoApply() {
	// (on totalAmount change)
	// if autoapply is checked
	// then take payment.totalAmount and apply to the invoices
	if (isAutoApply()) {
		var total = parseFloat($('#payment_totalAmount').val());
		$('span.invoiceID').each(
			function(i, ele) {
				var id = parseFloat($(ele).text());
				if (total > 0) {
					var apply = Math.min(parseFloat($('#invoice_balance_' + id).text()), total);
					$('#invoice_apply_'+id).val(apply.toFixed(2));
					total -= apply;
				} else {
					$('#invoice_apply_'+id).val('0.00');
				}
			});
	}
	calculateApplied();
}

function updateRemainder() {
	// update the remainder (Payment Total - Invoice Total)
	// hide save button if remainder < 0
	var remainder = parseFloat($('#payment_totalAmount').val()) - parseFloat($('#payment_amountApplied').text());
	if (remainder < 0 || $('#payment_totalAmount').val() <= 0) {
		$('#save_button').hide();
	}
	else {
		$('#save_button').show();
	}

	$('#payment_balance').text(remainder.toFixed(2));
}

function changeTotal() {
	if($('#payment_totalAmount').blank()) 
		$('#payment_totalAmount').val('0.00');
	
	if (isNaN($('#payment_totalAmount').val())) {
		alert("That value is not a number");
		$('#payment_totalAmount').val('0.00');
	}
	
	$('#payment_totalAmount').val(parseFloat($('#payment_totalAmount').val()).toFixed(2));
	autoApply();
}

function applyAll() {
	$('.invoiceID').each(
		function(i, ele){
			var invoiceID = $(ele).text();
			setInvoiceApply(invoiceID);
		});
		
	calculateApplied();
	calculateTotalFromApplied();
}

function clearAll() {
	$('.amountApply').val('0.00');
	
	calculateApplied();
}