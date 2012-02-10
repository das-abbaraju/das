(function ($) {
    PICS.define('translation.Manage', {
        methods: {
            init: function () {
            	// bad js
            	$('#doneButton').bind('click', function () {
            	    self.close();
            	});
            	
            	$('.translation-list').delegate('.view-mode a.edit', 'click', this.showEditMode);
            	$('.translation-list').delegate('.edit-mode .actions button.cancel', 'click', this.showViewMode);
            	$('.translation-list').delegate('.edit-mode button.save', 'click', this.saveTranslation);
            	$('.translation-list').delegate('.edit-mode input[type=checkbox], .edit-mode input[type=radio]', 'click', this.saveTranslationParametersThroughAjax);
            	$('.translation-list').delegate('.is-applicable', 'click', this.toggleQualityRating);
            	$('.translation-list').delegate('.view-mode.view-more', 'click', this.toggleViewModeShowAll);
            	$('.translation-list').delegate('.suggestTranslation', 'click', this.suggestTranslation);
            	
            	(function addMoreArrows() {
            	    setTimeout(function () {
            	        var views = $('.view').filter(function () {
            	            var element = $(this);
            	            
            	            return element.find('div.text').height() > 18 && !element.find('.see-more').length;
            	        }).slice(0, 10);
            	        
            	        if (views.length) {
            	            views.each(function (key, value) {
                                if ($(this).find('div.text').height() > 18) {
                                    $(this).closest('.content').addClass('view-more');
                                    
                                    $(this).append('<a href="javascript:;" class="see-more"><img src="images/arrow_down.gif" /></a>');
                                }
                            });
                            
                            addMoreArrows();
            	        }
                    }, 50);
            	}());
            },
            
            showEditMode: function (event) {
                var element = $(this);
                var form = element.closest('form');
                
                form.find('.content').addClass('edit-mode');
                form.find('.content').removeClass('view-mode');
            },
            
            showViewMode: function (event) {
                var element = $(this);
                var form = element.closest('form');
                
                form.find('.content').addClass('view-mode');
                form.find('.content').removeClass('edit-mode');
            },
            
            saveTranslation: function (event) {
                var element = $(this);
                var form = element.closest('form');

            	PICS.ajax({
            		url: 'ManageTranslationsAjax.action',
            		data: form.serialize(),
            		dataType: "json",
            		success: function(data, textStatus, XMLHttpRequest) {
            		    // update view text
            		    form.find('.view .text').html(form.find('textarea').val());
            		    
            		    // update to view mode
            		    form.find('.content').attr('class', 'content view-mode');
            		},
            		error: function (XMLHttpRequest, textStatus, errorThrown) {
            		    alert(result.reason);
            		}
            	});
            },
            
            saveTranslationParametersThroughAjax: function () {
                var element = $(this);
            	var form = element.closest("form");
            	
            	var value = element.val();
                
                if ($(this).attr('type') == 'checkbox') {
                    value = element.is(':checked');
                }
                
            	var data = {};
            	data.translation = form.find('input[name=translation]').val();
            	data[element.attr('name')] = value;
            	
            	
            	PICS.ajax({
            		url: "ManageTranslationsAjax!update.action",
            		data: data,
            		success: function() {
            			element.closest('td').effect('highlight', {color: '#FFFF11'}, 1000);
            		}
            	});
            },
            
            // http://code.google.com/p/jquery-translate/
            suggestTranslation: function (event) {
                event.preventDefault();
                
                var element = $(this);
                var form = element.closest('form');
                
                var locale_from = form.find('input[name="localeFrom"]').val();
                var locale_to = form.find('input[name="localeTo"]').val();
                
                var td_translation_to = element.closest('td');
                var td_translation_from = td_translation_to.prev('td');
                var textarea_translation_to = td_translation_to.find('textarea');
                var textarea_translation_from = td_translation_from.find('textarea');
                
                textarea_translation_to.val(textarea_translation_from.val());
                textarea_translation_to.translate(locale_from, locale_to);
                
                element.hide();
            },
            
            toggleQualityRating: function (event) {
                var element = $(this);
                var applicable = element.closest('.applicable');
                
                if (element.is(':checked')) {
                    applicable.siblings('.quality').show();
                } else {
                    applicable.siblings('.quality').hide();
                }
            },
            
            toggleViewModeShowAll: function (event) {
                var element = $(this);
                var view = element.find('.view');
                
                if (view.length) {
                    if (view.hasClass('all')) {
                        view.removeClass('all')
                    } else {
                        view.addClass('all');
                    }
                }
            }
        }
    });
})(jQuery);