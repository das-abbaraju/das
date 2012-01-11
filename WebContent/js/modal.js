(function($) {
	if (!window.MODAL) {
		MODAL = {};
	}
	
	MODAL.createModal = function(options) {
		return new MODAL.Modal(options);
	};
	
	// MODAL CLASS
	MODAL.Modal = function(options) {
		// default configuration
		var defaults = {
			modal_id: 'bootstrap_modal',
			modal_class: 'modal',
			modal_link_class: 'modal-link',
			backdrop: true,
			height: 'auto',
			keyboard: true,
			show: false,
			width: 560
		};
		
		// extend configuration
		var config = {};
		$.extend(config, defaults, options);
		
		// public configuration
		this.config = config;
		// initialize modal object
		this._createModal(this.config);
		// modal element
		this.element = $('#' + this.config.modal_id);
	};
	
	MODAL.Modal.prototype = {
		// create modal
		_createModal: function() {
			var config = this.config;
			
			var modal = $('<div id="' + config.modal_id + '" class="' + config.modal_class + '" style="display: none;">');
			var modal_header = $('<div class="modal-header"><a href="#" class="close">&#215;</a><h3></h3></div>');
			var modal_body = $('<div class="modal-body">');
			var modal_footer = $('<div class="modal-footer">');
			var html = modal.append(modal_header, modal_body, modal_footer);

			if (!$('#' + config.modal_id).length) {
				$('body').append(html);
				
				$('#' + config.modal_id).modal(config);
				
				// bind custom init event to the modal element
				$('#' + config.modal_id).bind('init', this, function(event, options) {
					var modal = options.modal;
					var modal_header = modal.find('.modal-header');
					var modal_body = modal.find('.modal-body');
					
					
					var title = options.title;
					var content = options.content;
					
					modal_header.find('h3').html(title);
					modal_body.html(content);
					
					modal.css({
					    marginLeft: '-' + (config.width / 2) + 'px',
					    marginTop: '-' + ((config.height / 2) + 50) + 'px',
					    width: config.width
					});
					
					modal_body.css({
					    height: config.height,
					    overflowY: 'auto'
					});
					
					modal.modal('show');
				});
			}
		},
		
		// modal content initialization + modal event trigger
		init: function(options) {
			var defaults = {
				modal: this.element,
				title: 'TITLE',
				content: 'CONTENT'
			};
			
			var config = {};
			
			$.extend(config, defaults, options);
			
			this.element.trigger('init', config);
		}
	};
	
})(jQuery);