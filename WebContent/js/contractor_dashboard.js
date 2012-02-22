(function ($) {
    PICS.define('contractor.Dashboard', {
        methods: {
            init: function () {
            	$('#start_watch_link').live('click', {action: 'Add', method: 'start'}, this.controlWatch);
            	$('#stop_watch_link').live('click', {action: 'Remove', method: 'stop'}, this.controlWatch);
            	$('#contractor_operator_numbers_form').live('submit', this.updateContractorOperatorNumbers);
            },
            
            controlWatch: function(event) {
            	event.preventDefault();
            	
            	var action = event.data.action;
            	var method = event.data.method;
            	
            	var conID = $(this).attr('data-conid');
            	var previouslyVisible = $('#contractorWatch .watch:visible');
            	var oldText = previouslyVisible.html();
            	
            	previouslyVisible.html(
            		'<img src="images/ajax_process.gif" alt="Loading" />' + translate('JS.ContractorView.' + action + 'Watch')
            	);
            	
            	PICS.ajax({
            		url: 'ContractorView!' + method + 'Watch.action',
            		data: {
            			contractor: conID
            		},
            		success: function(data, textStatus, XMLHttpRequest) {
            			var visibleNow = $('#contractorWatch .watch').not(':visible');
            			visibleNow.show();
            			visibleNow.effect('highlight', {color: '#FFFF11'}, 1000);
            		},
            		complete: function(data, textStatus, XMLHttpRequest) {
            			previouslyVisible.html(oldText);
            			previouslyVisible.hide();
            		}
            	});
            },
            
            updateContractorOperatorNumbers: function(event) {
            	event.preventDefault();
            	var element = $(this);
            	var data = element.serialize();
            	var url = element.attr('data-url');
            	
            	PICS.ajax({
            		url: url,
            		data: data,
            		success: function(data, textStatus, XMLHttpRequest) {
            			if (data.indexOf('error') > 0) {
            				element.parent().parent().html(data);
            			} else {
            				$('#contractor_operator_numbers').html(data);
            				
            				$('#facebox input[type=button].close').trigger('click');
            				$('#contractor_operator_numbers a[rel*="facebox"]').facebox({
            					loading_image : 'loading.gif',
            					close_image : 'closelabel.gif'
            				});
            			}
            		}
            	});
            }
        }
    });
})(jQuery);