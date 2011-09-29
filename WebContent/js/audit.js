(function($) {
	
	if (!window.AUDIT) {
		AUDIT = {};
	}
	
	// audit question
	AUDIT.question = {
		init: function() {
			// reset answer
			$('#auditViewArea').delegate('input.resetAnswer', 'click', this.events.reset);
			
			// triggered save question on save cert / detach cert?
			$('#auditViewArea').delegate('div.question', 'saveQuestion', this.events.save);
			
			// every question that is not 'save-disabled' should have a auto save
			// give problems in IE 6,7,8 - sending double change events
			// $('#auditViewArea').delegate('div.question:not(.save-disable)', 'change', this.events.save);
			
			$('div.question:not(.save-disable)').live('change', this.events.save);
			
			// every verified question
			$('#auditViewArea').delegate('input.verify', 'click', this.events.verify);
			
			// question save trigger for "save-disable" questions
			$('#auditViewArea').delegate('.question-save', 'click', function(event) {
				AUDIT.question.events.save.apply($(this).closest('div.question'));
			});
		},
		
		// question events
		events: {
			reset: function(event) {
				var element = $(this).parents('div.question:first');
				var form = $('form.qform', element);
				var url = 'AuditDataSaveAjax.action';
				
				var data = $.map(form.serializeArray(), function(data, i) {
					if (data.name == 'auditData.answer') {
						data.value = '';
					}
					
					return data;
				});
				
				element.block({
					message : 'Clearing answer...'
				});
				
				AUDIT.question.execute(element, url, data);
			},
			save: function(event) {
				var element = $(this);
				var form = $('form.qform', element);
				var url = 'AuditDataSaveAjax.action';
				var data = form.serializeArray();
				
				element.block({
					message: 'Saving answer...'
				});
				
				AUDIT.question.execute(element, url, data);
			},
			verify: function(event) {
				var element = $(this).parents('div.question:first');
				var form = $('form.qform', element);
				var url = 'AuditDataSaveAjax.action';
				var data = form.serializeArray();
				
				data.push({
					name: 'toggleVerify',
					value: 'true'
				});
				
				element.block({
					message: $(this).val() + 'ing...'
				});
				
				AUDIT.question.execute(element, url, data);
			}
		},
		
		// question methods
		execute: function(element, url, data) {
			$.post(url, data, function(data, textStatus, XMLHttpRequest) {
				element.trigger('updateDependent');
				element.replaceWith(data);
			});
		}
	};
	
	
	AUDIT.esignature = {
		init: function() {
			$('#auditViewArea').delegate('.edit-esignature', 'click', this.events.edit);
		},
		
		events: {
			edit: function(event) {
				var view_element = $(this).closest('.view');
				var edit_element = view_element.siblings('.edit');
				
				if (view_element.is(':visible')) {
					view_element.hide();
					edit_element.show();
				} else {
					view_element.show();
					edit_element.hide();
				}
			}
		}
	};
	
})(jQuery);