(function ($) {
	PICS.define('auditCategoryMatrix.Manage', {
		methods: {
			init: function () {
				$("#AuditCategoryMatrix #select_audit_type").live("change", this.selectAuditType);
				$("#AuditCategoryMatrix #search #update").live("click", this.updateTable);
				$("#AuditCategoryMatrix #search .filterBox").live("click", this.updateFilters);
				
				$("#AuditCategoryMatrix #search .allLink").live("click", {toggle: true}, this.toggleSelected);
				$("#AuditCategoryMatrix #search .clearLink").live("click", {toggle: false}, this.toggleSelected);
				
				$("#AuditCategoryMatrix table.report thead .edit").live("click", {editTable: true}, this.updateTable);
				$("#AuditCategoryMatrix table.report thead .preview").live("click", {editTable: false}, this.updateTable);
				
				$("#AuditCategoryMatrix table.report input[type=checkbox].toggle").live("click", this.updateAssociation);
			},
			
			selectAuditType: function(event) {
				$('#AuditCategoryMatrix #filterLoad').html('<img src="images/ajax_process.gif" alt="' + translate('JS.Loading') + '" />');
				
				PICS.ajax({
					url: "AuditCategoryMatrix!filters.action",
					data: {
						auditType: $(this).val()
					},
					success: function(data, textStatus, XMLHttpRequest) {
						$('#AuditCategoryMatrix #filterLoad').html(data);
					}
				});
			},
			
			updateTable: function(event) {
				event.preventDefault();
				
				var serializedData = $('#AuditCategoryMatrix #form1').serialize();
				
				if (event.data && event.data.editTable) {
					serializedData = serializedData + '&editTable=' + event.data.editTable;
				}
				
				$('#AuditCategoryMatrix #table').html('<img src="images/ajax_process2.gif" alt="' + translate('JS.Loading') + '" />');
				
				PICS.ajax({
					url: "AuditCategoryMatrix!table.action",
					data: serializedData,
					success: function(data, textStatus, XMLHttpRequest) {
						if (data.indexOf("error") > 0) {
							$('#AuditCategoryMatrix #messages').html(data);
						} else {
							$('#AuditCategoryMatrix #messages').empty();
							$('#AuditCategoryMatrix #table').html(data);
						}
					}
				});
			},
			
			updateFilters: function(event) {
				event.preventDefault();
				
				var name = $(this).attr('data-name');
				var select = $('#'+name+'_select');
				var result = $('#'+name+'_query');
				
				result.hide();
				select.toggle();
				
				if (select.is(':visible'))
					return;

				result.show();
				
				var container = $("#"+name);
				var queryText = '';
				container.find('option:selected').each(function(i,e){
					if (queryText != '') queryText += ", ";
					queryText += $(e).text();
				});
				
				if (queryText == '') {
					queryText = translate('JS.Filters.status.All');
				}
				
				result.text(queryText);
			},
			
			toggleSelected: function(event) {
				event.preventDefault();
				
				var name = $(this).attr('data-name');
				var toggle = event.data.toggle;
				
				$('#' + name + ' option').attr('selected', toggle);
			},
			
			updateAssociation: function(event) {
				var checked = $(this).is(':checked');
				var auditType = $(this).attr('data-audittype');
				var category = $(this).attr('data-category');
				var item = $(this).attr('data-item');
				var element = $(this);
				
				PICS.ajax({
					url: "AuditCategoryMatrix!manageAssociations.action",
					data: {
						checked: checked,
						auditType: auditType,
						category: category,
						itemID: item
					},
					success: function(data, textStatus, XMLHttpRequest) {
						if (data.indexOf('"error"') > 0) {
							element.attr('checked', !checked);
							$('#AuditCategoryMatrix #messages').html(data);
						} else {
							element.parent().effect('highlight', {color: '#FFFF11'}, 1000);
						}
					}
				});
			}
		}
	});
})(jQuery);