$(function() {
	$('#operatorTagForm').delegate('.checkRemove', 'click', function(e) {
		var id = $(this).attr('rel');
		
		$.get('ContractorTagsAjax.action', {
			tagID: id, button: 'removeNum'
		} , function(data) {
			$.facebox(data);
		});
	});
});