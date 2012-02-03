(function ($) {
    PICS.define('contractor.Dashboard', {
        methods: {
            init: function () {
            	this.initializeTable();
            	$('#contractor_operator_numbers_add').bind('click', this.addContractorOperatorNumber);
            	$('#start_watch_link').live('click', this.startWatch);
            	$('#stop_watch_link').live('click', this.stopWatch);
            },
            
            initializeTable: function() {
            	PICS.ajax({
            		url: "ManageContractorOperatorNumber.action",
            		data: {
            			contractor : conID
            		},
            		success: function(data, textStatus, XMLHttpRequest) {
            			$('#contractor_operator_numbers').html(data);
            		}
            	});            	
            },
            
            addContractorOperatorNumber: function(event) {
            	event.preventDefault();
            	PICS.ajax({
            		url: "ManageContractorOperatorNumber!save.action",
            		data: $('#contractor_operator_numbers_form').serialize(),
            		success: function(data, textStatus, XMLHttpRequest) {
            			$('#contractor_operator_numbers').html(data);
            		}
            	});
            },
            
            startWatch: function(event) {
            	event.preventDefault();
            	var conID = $(this).attr('data-conid');
            	
            	$('#contractorWatch').html('<img src="images/ajax_process.gif" alt="Loading" />' + translate('JS.ContractorView.AddWatch'));
            	
            	PICS.ajax({
            		url: 'ContractorView!startWatch.action',
            		data: {
            			contractor: conID
            		},
            		success: function(data, textStatus, XMLHttpRequest) {
            			$('#contractorWatch .watched').toggle().effect('highlight', {color: '#FFFF11'}, 1000);
            			$('#contractorWatch .not.watched').attr('data-watch', data);
            		},
            	});
            },
            
            stopWatch: function(event) {
            	event.preventDefault();
            	var watch = $(this).attr('data-watch');
            	
            	$('#contractorWatch').html('<img src="images/ajax_process.gif" alt="Loading" />' + translate('JS.ContractorView.RemoveWatch'));
            	
            	PICS.ajax({
            		url: 'ContractorView!stopWatch.action',
            		data: {
            			watch: watch
            		},
            		success: function(data, textStatus, XMLHttpRequest) {
            			$('#contractorWatch watch').toggle().effect('highlight', {color: '#FFFF11'}, 1000);
            		},
            	});
            }
        }
    });
})(jQuery);