$(function() {
	$('a[rel*="facebox"]').facebox({
 		loading_image : 'loading.gif',
 		close_image : 'closelabel.gif'
 	});
	
	$('#operatorTagForm').delegate('.checkRemove', 'click', function(e) {
		var id = $(this).attr('rel');
		
		$.get('ContractorTagsAjax.action', {
			tagID: id, button: 'removeNum'
		} , function(data) {
			$.facebox(data);
		});
	});
});