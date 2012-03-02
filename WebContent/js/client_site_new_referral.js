$(function() {
	$('#phoneContact').click(function() {
        $.blockUI({
        	message: $('#phoneSubmit')
        });
 
        $('.blockOverlay').attr('title', translate('JS.RequestNewContractor.ClickToUnblock')).click($.unblockUI);
    });
	
	$('#emailContact').click(function() {
		$.blockUI({
			message: $('#emailSubmit')
		});
		
		$('.blockOverlay').attr('title', translate('JS.RequestNewContractor.ClickToUnblock')).click($.unblockUI);              
    });
	
	$('.fancybox').fancybox();
	
	$('.cluetip').cluetip({
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		clickThrough: false
	});
	
	$('#saveContractorForm').delegate('#operatorForms', 'click', function(e) {
		e.preventDefault();
	}).delegate('#addToNotes', 'keyup', function() {
		var d = new Date();
		var dateString = (d.getMonth() + 1 < 10 ? "0" : "") + (d.getMonth() + 1) + "/" + (d.getDate() < 10 ? "0" : "") + (d.getDate()) + "/" + d.getFullYear();
		$('#addHere').html(dateString + " - " + name + " - " + $(this).val() + "\n\n");

		if ($('#addToNotes').val() == '')
			$('#addHere').text('');
	});
});