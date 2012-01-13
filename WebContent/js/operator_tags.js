$(function() {
	$('#operatorTagForm .actions .modal-link').bind('click', function() {
	    var element = $(this);
	    
	    AJAX.request({
	        url: element.attr('data-url'),
	        success: function (data, textStatus, XMLHttpRequest) {
                var modal = MODAL.Modal({
                    modal_class: 'modal operator-tag-modal',
                    height: 550,
                    width: 960,
                    title: element.text(),
                    content: data
                });
                
                modal.bind('hide', function() {
                    window.location.reload();
                });
                
                modal.show();
	        }
	    });
	});
});