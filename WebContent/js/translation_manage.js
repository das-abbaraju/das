(function ($) {
    PICS.define('translation.Manage', {
        methods: {
            init: function () {
                var that = this;

                // toggle see more text
                $('.translation-list').delegate('.view-mode.view-more', 'click', this.toggleViewModeShowAll);

                // auto save applicable + quality rating
                $('.translation-list').on('click', '.rate input[type=checkbox]', this.saveTranslationParametersThroughAjax);
                $('.translation-list').on('change', '.quality-rating', this.saveTranslationParametersThroughAjax);

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
                var that = this,
                    rate = $('.rate:first');

                $('.translation-list').addClass('dirty');

                if (rate.length) {
                    var width = rate.position().left - 30;
                    $('.view .text').css('width', width);
                }

                $('.translation-list').removeClass('dirty');
            },

            saveTranslation: function (event) {
                var element = $(this),
                    form = element.closest('form');

                function updateOtherLocales() {
                    var translation_from = form.closest('.translation-from'),
                        translation_to = translation_from.siblings('.translation-to'),
                        quality_rating = translation_to.find('.quality-rating'),
                        update_locales = form.find('input[name=updateOtherLocales]');

                    if (update_locales.is(':checked')) {
                        if (translation_to.length) {
                            quality_rating.val('Questionable');
                        }
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
            	data.locale = form.find('input[name="translation.locale"]').val();
            	data.key2 = form.find('input[name="translation.key"]').val();

            	PICS.ajax({
            		url: "ManageTranslationsAjax!update.action",
            		data: data,
            		success: function() {
            			element.closest('td').effect('highlight', {color: '#FFFF11'}, 1000);
            		}
            	});
            },

            showEditMode: function (event) {
                var element = $(this),
                    form = element.closest('form'),
                    translation = form.find('textarea');

                form.find('.content').addClass('edit-mode');
                form.find('.content').removeClass('view-mode');

                translation.data('previous', translation.val());
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
            },

            showViewMode: function (event) {
                var element = $(this),
                    form = element.closest('form'),
                    translation = form.find('textarea');

                form.find('.content').addClass('view-mode');
                form.find('.content').removeClass('edit-mode');

                //revert translation edit on cancel
                translation.val(translation.data('previous'))
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