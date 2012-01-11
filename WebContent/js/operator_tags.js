$(function() {
	$('#operatorTagForm').delegate('.checkRemove', 'click', function(e) {
		var id = $(this).attr('rel');
		
		$.get('ContractorTagsAjax.action', {
			tagID: id, button: 'removeNum'
		} , function(data) {
			$.facebox(data);
		});
	});
	
	$('#operatorTagForm .actions .modal-link').bind('click', function() {
	    var element = $(this);
	    
	    AJAX.request({
	        url: element.attr('data-url'),
	        success: function (data, textStatus, XMLHttpRequest) {
	            var modal = MODAL.createModal({
	                modal_class: 'modal operator-tag-modal',
	                height: 550,
	                width: 960
	            });
                
                modal.init({
                    title: element.text(),
                    content: data
                });
	        }
	    });
	});
});