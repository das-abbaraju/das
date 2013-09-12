(function($) {

	/* ajax history - bbq */
	var lastState, catXHR;

	$('a.hist-category, a.modeset').live('click', function () {
		$.bbq.pushState(this.href);
		$.bbq.removeState('onlyReq');
		$.bbq.removeState('subCat');
		$.bbq.removeState('viewBlanks');

		var state = $.bbq.getState();

		if (state.categoryID == lastState.categoryID && state.mode == lastState.mode) {
			$.bbq.pushState({
			    "_": (new Date()).getTime()
		    });
		} else {
			$.bbq.removeState("_");
		}

		if (state.mode == 'ViewQ' || state.viewBlanks == "false") {
			$.bbq.removeState('mode');
		}

		return false;
	});

	$('ul.subcat-list li a').live('click', function () {
		$.bbq.pushState(this.href);
		$.bbq.removeState('viewBlanks');
		$.bbq.removeState('onlyReq');
		$.bbq.removeState("_");

		return false;
	});

	$('ul.vert-toolbar a.preview').live('click', function () {
		$.bbq.pushState(this.href);
		$.bbq.removeState('viewBlanks');
		$.bbq.removeState('onlyReq');
		$.bbq.removeState('_');
		$.bbq.removeState('subCat');

		return false;
	});
	/* end ajax history - bbq */

	if (!window.AUDIT) {
		AUDIT = {};
	}

	// audit load category
	AUDIT.load_category = {
		init: function () {
			if ($('#audit-layout').length) {
				var messageLoadingRequirements = translate('JS.Audit.LoadingRequirements');
				var messageLoadingCategory = translate('JS.Audit.LoadingCategory');
				var messageLoadingAllCategories = translate('JS.Audit.LoadingAllCategories');
				var messageLoadingAnsweredQuestions = translate('JS.Audit.LoadingAnsweredQuestions');
				var messageLoadingPreview = translate('JS.Audit.LoadingPreview');

				$(window).bind('hashchange', function () {
					var state = $.bbq.getState();

					if (state.subCat !== undefined) {
						$.scrollTo('#cathead_' + state.subCat, 800, {
							axis: 'y'
						});
					} else {
						// default request parameters
						var data = $.deparam.querystring($.param.querystring(location.href, state));

						// default load operation
						data.button = 'load';

						// default loading message
						var message = messageLoadingCategory;

						if(state.onlyReq !== undefined){
							data.button = 'PrintReq';

							$('#auditViewArea').block({
								message: messageLoadingRequirements,
								centerY: false,
								css: {
									top: '20px'
								}
							}).load('AuditAjax.action', data, function () {
								$('ul.catUL li.current').removeClass('current');
								$(this).unblock();
							});

							$('#printReqButton').show();
						} else if (state.mode == 'ViewQ') {
							message = messageLoadingPreview;
						} else if (state.viewBlanks == "false") {
							message = messageLoadingAnsweredQuestions;
						} else if (state.mode == "ViewAll") {
							message = messageLoadingAllCategories;
						} else if (state.categoryID === undefined) {
							var options = {};

							if (!lastState || lastState.categoryID === undefined) {
								options = $.deparam.fragment($('a.hist-category:first').attr('href'));
							}

							$.extend(options, $.deparam.fragment(location.href));

							var data = $.deparam.querystring($.param.querystring(location.href, options));

							data.button = 'load';

							message = messageLoadingCategory;
						} else if (!lastState || !lastState.categoryID || state.categoryID != lastState.categoryID || state.mode != lastState.mode || state["_"]) {
							$('#printReqButton').hide();

							if ($(window).scrollTop() > $('#auditViewArea').offset().top) {
								$.scrollTo('#auditViewArea', 800, {
									axis: 'y'
								});
							}
						}

						AUDIT.load_category.reload(data, message);
					}

					lastState = state;
				});
			}
		},

		reload: function(data, msg) {
			var categoryID = data.categoryID;

			catXHR && catXHR.abort();

			$('#auditViewArea').block({
				message: msg,
				centerY: false,
				css: {
					top: '20px'
				}
			});

			catXHR = $.ajax({
				url: 'AuditAjax.action',
				data: data,
				success: function(html, status, XMLHttpRequest) {
					var state = $.bbq.getState();

					$('li.current').removeClass('current');
					$('#auditViewArea').html(html).unblock();

					var subCatScroll = $('#cathead_' + state.subCat);

					if (subCatScroll.length)
						$.scrollTo(subCatScroll, 800, {axis: 'y'});

					if (state.categoryID !== undefined) {
						highlight_category(state.categoryID);
					} else if (data.categoryID !== undefined) {
						highlight_category(data.categoryID);
					}

					// auto scroll to question based on question id
					if (state.questionID !== undefined) {
					    var question = $('#node_' + state.questionID);

					    if (question.length) {
					        $(window).scrollTop(question.offset().top);
					    }
					}

					if (state.mode == 'ViewQ') {
						$('a.preview').closest('li').addClass('current');
					}

					if (state.viewBlanks == "false") {
						$('#viewBlanks').closest('li').addClass('current');
					}

					showNavButtons();
					AUDIT.question.updateLinks();

					$('a.filter').cluetip({
						sticky: true,
						showTitle: false,
						dropShadow: false,
						mouseOutClose: true,
						clickThrough: false
					});

					// enable ambest questions on audit category reload
					AUDIT.am_best_suggest.autocomplete($('#auditViewArea #ambest'));

					AUDIT.question.initTagit();
				}
			});
		}
	};

	// audit question
	AUDIT.question = {
		init: function () {
			// reset answer
			$('#auditViewArea').delegate('.reset-answer', 'click', this.events.reset);

			// give the ability for questions to manually trigger save question
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
				event.preventDefault();

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
					message : translate('JS.Audit.ClearingAnswer')
                });

				AUDIT.question.execute(element, url, data);
			},
			save: function(event) {
			    var element = $(this),
			        form = $('form.qform', element),
			        radio_button = element.find('input:radio'),
			        url = 'AuditDataSaveAjax.action';

			    var data = form.serializeArray();

			    //Allow save only if an answer has been given for radio button.
			    if ((radio_button.length > 0) && (!radio_button.is(':checked'))) {
			        return false;
			    }

				element.block({
					message: translate('JS.Audit.SavingAnswer')
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

				data.push({
					name: 'button',
					value: 'verify'
				});

				element.block({
					message: $(this).val() + 'ing...'
				});

				AUDIT.question.execute(element, url, data);
			}
		},

		updateLinks: function () {
			if (hasPermissionsToSeeAuditLinks == 'true') {
				$('div.question a.passAudit').each(function () {
					var question_query_param = '';
					var question_container = $(this).closest('div.question');

					if (question_container.length) {
						var question_id = $(this).closest('div.question').attr('id');
						question_id = question_id.substring(5);

						question_query_param = '&questionId=' + question_id;
					}

					$(this).attr('href', $(this).attr('href') + "?audit=" + auditID + question_query_param);
				});
			} else if (operatorCorporate) {
				$('div.question a.passAudit').not('.operatorViewable').each(function () {
					var text = $(this).text();
					$(this).replaceWith(text);
				});

				$('div.question a.operatorViewable').each(function () {
					$(this).attr('href', $(this).attr('href') + "?account=" + conID);
				});
			} else {
				$('div.question a.passAudit, div.question a.operatorViewable').each(function () {
					var text = $(this).text();

					$(this).replaceWith(text);
				});
			}
		},

		// question methods
		execute: function(element, url, data) {
			$.post(url, data, function(data, textStatus, XMLHttpRequest) {
				var element_id = element.attr('id');

				element.trigger('updateDependent');
				element.replaceWith(data);

				// re-enable ambest questions on audit category reload
				AUDIT.am_best_suggest.autocomplete($('#' + element_id + ' ' + '#ambest'));

				AUDIT.question.initTagit();
			});
		},

		initTagit: function () {
		    var audit_controller = PICS.getClass('audit.AuditController');

		    audit_controller.createSelect2();
		}
	};

	// Esignature questions
	AUDIT.esignature = {
		init: function () {
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

	// AM BEST SUGGEST
	AUDIT.am_best_suggest = {

		// jquery.autocomplete.plugin 1.1
		autocomplete: function(element) {
			if (element.length) {
				element.autocomplete('AmBestSuggestAjax.action', {
					minChars: 3,
					formatResult: function(data,i,count) {
						return data[1];
					}
				}).change(function(event) {
					// must stop propogation - otherwise this event will bubble and fire other change events
					event.stopPropagation();

					// if the ambest value changes and there are no values then remove the ID saved in the comment field
					if ($(this).blank()) {
						var form = $(this).closest('form');
						var comment = form.find('[name="auditData.comment"]');

						comment.val('');
					}
				}).result(function(event, data, formatted) {
					// data[0] - full name (id)
					// data[1] - full name
					// data[2] - id

					var form = $(this).closest('form');
					var comment = form.find('[name="auditData.comment"]');

					// if there is an ID available - place the ID in the comments
					if (data[2] != "UNKNOWN") {
						comment.val(data[2]);
					} else {
						comment.val('');
					}

					$(this).trigger('saveQuestion');
				});
			}
		}
	};

})(jQuery);