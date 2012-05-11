(function ($) {
    PICS.define('translation.Manage', {
        methods: {
            init: function () {
                var that = this;
                
                // toggle see more text
                $('.translation-list').delegate('.view-mode.view-more', 'click', this.toggleViewModeShowAll);
                
                // toggle view of quality rating options (only show if is applicable)
                $('.translation-list').delegate('.is-applicable', 'click', this.toggleQuality);
                $('.translation-list').delegate('.rate', 'mouseenter', this.toggleQualityRatingOn);
                $('.translation-list').delegate('.rate', 'mouseleave', this.toggleQualityRatingOff);
                
                // auto save applicable + quality rating
                $('.translation-list').delegate('.rate input[type=checkbox], .rate input[type=radio]', 'click', this.saveTranslationParametersThroughAjax);
                
                // enter edit mode
            	$('.translation-list').delegate('.view-mode a.edit', 'click', this.showEditMode);
            	$('.translation-list').delegate('.view-mode a.preview-translation', 'click', this.showPreview);
            	
            	// ajax translation save
            	$('.translation-list').delegate('.edit-mode button.save', 'click', this.saveTranslation);
            	
                // enter view mode
                $('.translation-list').delegate('.edit-mode .actions button.cancel', 'click', this.showViewMode);
                
            	this.addSeeMoreArrows.apply(this);
            	
            	this.adjustViewTextWidth.apply(this);
            	
            	$(window).bind('resize', PICS.throttle(function () {
            	    that.adjustViewTextWidth.apply(that);
                }, 250));
            },
            
            addSeeMoreArrows: function () {
                var that = this;
                
                setTimeout(function () {
                    var views = $('.view').filter(function () {
                        var element = $(this);
                        var text = element.find('div.text');
                        
                        return text.height() > 18 && !element.find('.see-more').length;
                    }).slice(0, 10);
                    
                    if (views.length) {
                        views.each(function (key, value) {
                            var element = $(this);
                            var text = element.find('div.text');
                            
                            if (text.height() > 18) {
                                element.closest('.content').addClass('view-more');
                                
                                text.append('<a href="javascript:;" class="see-more"><img src="images/arrow_down.gif" /></a>');
                            }
                        });
                        
                        that.addSeeMoreArrows();
                    }
                }, 50);
            },
            
            adjustViewTextWidth: function () {
                var that = this;
                
                var width = $('.rate:first').position().left - 30;
                
                $('.translation-list').addClass('dirty');
                
                $('.view .text').css('width', width);
                
                $('.translation-list').removeClass('dirty');
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
                
                function updateOtherLocales() {
                    var checkbox = form.find('input[name=updateOtherLocales]');
                    
                    if (checkbox.is(':checked')) {
                        var translation_from = form.closest('.translation-from');
                        var translation_to = translation_from.siblings('.translation-to');
                        
                        if (translation_to.length) {
                            var radio = translation_to.find('.quality-rating input[value=Questionable]');
                            
                            radio.attr('checked', 'checked');
                        }
                        
                        checkbox.attr('checked', 'checked');
                    }
                }

            	PICS.ajax({
            		url: 'ManageTranslationsAjax.action',
            		data: form.serialize(),
            		dataType: "json",
            		success: function(data, textStatus, XMLHttpRequest) {
            		    // update view text
            		    form.find('.view .text').html(form.find('textarea').val());
            		    
            		    // update to view mode
            		    form.find('.content').attr('class', 'content view-mode');
            		    
            		    updateOtherLocales();
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
            
            toggleQuality: function (event) {
                var element = $(this);
                var applicable = element.closest('.applicable');
                
                if (element.is(':checked')) {
                    applicable.siblings('.quality').show();
                } else {
                    applicable.siblings('.quality').hide();
                }
            },
            
            toggleQualityRatingOn: function (event) {
                var element = $(this);
                var quality_rating = element.find('.quality-rating');
                var is_applicable = element.find('.is-applicable');
                
                if (is_applicable.is(':checked')) {
                    element.find('.quality-rating').show();
                    element.closest('.view').addClass('all');
                }
            },
            
            toggleQualityRatingOff: function (event) {
                var element = $(this);
                var quality_rating = element.find('.quality-rating');
                var is_applicable = element.find('.is-applicable');
                
                if (is_applicable.is(':checked')) {
                    element.find('.quality-rating').hide();
                    element.closest('.view').removeClass('all');
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
            },
            
            showPreview: function (event) {
                event.stopPropagation();
                
                var url = $(this).attr('data-url');
                var key = $(this).attr('data-key');
                var localeTo = $(this).attr('data-localeto');
                
                var preview_modal = PICS.modal({
                   width: 600,
                   title: 'Preview',
                   content: '<iframe src="' + url + '?key=' + key + '&localeTo=' + localeTo
                       + '" width="550" height="480"></iframe>'
                });
                
                preview_modal.show();
            }
        }
    });
})(jQuery);