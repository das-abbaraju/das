(function ($) {
    PICS.define('translation.Manage', {
        methods: {
            init: function () {
            	$('table.report .showEdit').bind('click', this.addEditMode);
            	$('table.report button.cancel').bind('click', this.removeEditMode);
            	$('table.report .suggestTranslation').bind('click', this.suggestTranslation);
            	$('table.report form button.save').bind('click', this.saveTranslation);
            	$('table.report form input[type=checkbox], table.report form input[type=radio]').bind('click', this.saveQualityRating);
            	$('#doneButton').bind('click', this.closeWindow);
            },
            
            addEditMode: function (event) {
            	event.preventDefault();
            	$(this).closest("td").addClass("editMode");
            },
            
            removeEditMode: function (event) {
            	$(this).closest("td").removeClass("editMode");
            },
            
            suggestTranslation: function (event) {
            	// http://code.google.com/p/jquery-translate/
            	var element = $(this);
            	event.preventDefault();
            
            	var textarea = element.closest("td").find("textarea");
            	textarea.val(element.closest("td").prev().find("textarea").val() );
            	textarea.translate('<s:property value="localeFrom"/>', '<s:property value="localeTo"/>');
            	element.closest("td").addClass("editMode");
            	element.hide();
            },
            
            saveTranslation: function () {
            	var that = $(this).closest("td");
            	that.addClass("saving");

            	var params = $(this).closest("form").serialize();
            	
            	PICS.ajax({
            		url: 'ManageTranslationsAjax.action',
            		data: params,
            		dataType: "json",
            		success: function(data, textStatus, XMLHttpRequest) {
            			that.find("input[name|='translation']").val(data.id);
            			that.find("span").html(that.find("textarea").val());
            			that.removeClass("editMode");
            		},
            		error: function () {
            			alert(result.reason);
            		},
            		complete: function () {
            			that.removeClass("saving");
            		}
            	});
            },
            
            saveQualityRating: function () {
            	var form = $(this).closest("form");
            	var cell = $(this).closest("td");
            	
            	var fields = {};
            	fields['translation'] = form.find('input[name=translation]').val();
            	fields[$(this).attr('name')] = $(this).val();
            	
            	PICS.ajax({
            		url: "ManageTranslationsAjax!update.action",
            		data: fields,
            		success: function() {
            			cell.effect('highlight', {color: '#FFFF11'}, 1000);
            		}
            	});
            },
            
            closeWindow: function () {
            	self.close();
            }
        }
    });
})(jQuery);