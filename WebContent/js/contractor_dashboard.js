(function ($) {
	var _modal;
	
	PICS.define('contractor.Dashboard', {
		methods: {
			init: function () {
				$('#start_watch_link').live('click', {action: 'Add', method: 'start'}, this.controlWatch);
				$('#stop_watch_link').live('click', {action: 'Remove', method: 'stop'}, this.controlWatch);
				
				$('#contractor_operator_numbers a.add, #contractor_operator_numbers a.edit').live('click', this.openModalForNumbers);
				$('#contractor_operator_numbers_form input.closeButton').live('click', this.closeModalForNumbers);
				
				$('#contractor_operator_numbers_form').live('submit', this.updateContractorOperatorNumbers);
				$('#contractor_operator_numbers a.remove').live('click', this.deleteContractorOperatorNumber);
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
			
			openModalForNumbers: function(event) {
				event.preventDefault();
				
				var url = $(this).attr('href');
				var contractor = $(this).attr('data-contractor');
				var number = $(this).attr('data-number');
				var name = $('#contractor_operator_numbers_label').text();
				_modal = null;
				
				PICS.ajax({
					url: url,
					data: {
						contractor: contractor,
						number: number
					},
					success: function(data, textStatus, XMLHttpRequest) {
						_modal = PICS.modal({
		                    height: 550,
		                    width: 700,
		                    title: name,
		                    content: data
		                });
					},
					complete: function(data, textStatus, XMLHttpRequest) {
						if (_modal) {
							_modal.show();
						}
					}
				});
			},
			
			closeModalForNumbers: function(event) {
				event.preventDefault();
				
				if (_modal) {
					_modal.hide();
				}
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
							
							if (_modal) {
								_modal.hide();
							}
						}
					}
				});
			},
			
			deleteContractorOperatorNumber: function(event) {
				event.preventDefault();
				
				if (confirm(translate('JS.ManageContractorOperatorNumber.ConfirmDelete'))) {
					var contractor = $(this).attr('data-contractor');
					var number = $(this).attr('data-number');
					var url = $(this).attr('href');
					
					PICS.ajax({
						url: url,
						data: {
							contractor: contractor,
							number: number
						},
						success: function(data, textStatus, XMLHttpRequest) {
							$('#contractor_operator_numbers').html(data);
						}
					});
				}
			}
		}
	});
})(jQuery);