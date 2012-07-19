(function ($) {
	if (!window.REGISTRATION) {
		REGISTRATION = {};
	}

	// autofill email to username field
	REGISTRATION.autofill_username = {
		init: function () {
			if ($('.Registration-page').length) {
				$('#Registration_user_email').bind('blur', this.events.autofill);
			}
		},

		events: {
			autofill: function (event) {
				var element = $(this);
				var target = $('#Registration_user_username');

				if (!target.val()) {
					target.val(element.val());
				}
			}
		}
	};

	// contractor create account country / state toggle
	REGISTRATION.contractor_country = {
		init: function () {
			if ($('.Registration-page').length) {
				$('.contractor-country').bind('change', this.events.update_state_list);

				//autofill state list
				if ($('.contractor-country').length) {
				    this.events.update_state_list();
				}
			}
		},

		events: {
			update_state_list: function () {
				var country_select = $('.contractor-country') || $(this);
				var country_string = country_select.val();

				AJAX.request({
					url: 'StateListAjax!registration.action',
					data: {
						countryString: country_string,
						prefix: 'contractor.'
					},
					success: function (data, textStatus, XMLHttpRequest) {
						var state_element = $('.registration-form li.state');
						var zip_element = $('.registration-form li.zip');

						if ($.trim(data) == '') {
							state_element.slideUp(400);
						} else {
							state_element.html(data);
							state_element.slideDown(400);
						}

						if (country_string == 'AE') {
							zip_element.slideUp(400);
						} else {
							zip_element.slideDown(400);
						}

						if (country_string == 'GB') {
							$('.Registration-page header .phone').text(translate("JS.RegistrationSuperEliteSquadronPhone.GB"));
						} else {
							$('.Registration-page header .phone').text(translate("JS.RegistrationSuperEliteSquadronPhone"));
						}
					}
				});
			}
		}
	};

	// registration - create account - inline field validation
	REGISTRATION.field_validate = {
		init: function () {
			if ($('.Registration-page').length) {
				// add event handlers to all input / password fields
				var element = $('.registration-form input[type=text], .registration-form input[type=password]');

				// Prevent the re-triggering of the event within 250 ms of its initial triggering.
				element.bind('keyup', UTILITY.debounce(this.events.field_validate, 250));

				// add event upon leaving the field
				element.bind('blur', this.events.field_validate);
			}
		},

		events: {
			// ajax field validation - using jsonValidate intercepter
			field_validate: function (event) {
				var element = $(this);

				// parent form
				var form = element.closest('form');
				// serialized form including json validator interceptors
				var data = form.serialize() + '&method%3AcreateAccount="Get Started"' + '&struts.enableJSONValidation=true' + '&struts.validateOnly=true';

				// ajax request to submit form
				AJAX.request({
					url: form.attr('action'),
					data: data,
					complete: function (XMLHttpRequest, textStatus) {
						// obtain errors - field errors returned in the json request
						var errors = StrutsUtils.getValidationErrors(XMLHttpRequest.responseText);

						// obtain any errors that are currently attached to the field
						var error_element = element.siblings('.errors');

						if (errors.fieldErrors != undefined) {
							var field_errors = errors.fieldErrors[element.attr('name')];

							// if there are errors - add them to the dom
							if (field_errors != undefined) {
								var html = '<ul class="errors">';

								$.each(field_errors, function (i, value) {
									html += '<li>' + value + '</li>';
								})

								html += '</ul>';

								if (error_element.length) {
									error_element.replaceWith(html);
								} else {
									element.after(html);
								}

								var is_legal_name_field = element.attr('id') == 'Registration_contractor_name';

								// conditions show hide duplicate contractor name message
								$.each(field_errors, function (i, value) {
								    if (is_legal_name_field && value == translate('JS.Validation.CompanyNameAlreadyExists')) {
								        $('.contractor-name-duplicate').show();
								    } else if (is_legal_name_field) {
								        $('.contractor-name-duplicate').hide();
								    }
								});

							// clear out any errors upon correct field validation
							} else {
								if (error_element.length) {
									error_element.remove();

									// conditions hide duplicate contractor name message
                                    if (element.attr('id') == 'Registration_contractor_name') {
                                        $('.contractor-name-duplicate').hide();
                                    }
								}
							}
						} else {
							// clear out any errors upon correct field validation
							error_element.remove();
						}
					}
				});
			}
		}
	};

	// contractor create account help text displayed on input focus
	REGISTRATION.help_text = {
		init: function () {
			if ($('.Registration-page').length) {
				var element = $('.help-text');

				element.each(function (key, value) {
					var html = $(this).html();
					var label = $(this).siblings('label');
					var input = $(this).siblings('input[type=text], input[type=password], select');

					label.attr('title', label.html().replace(':', ''));
					label.attr('data-content', html.replace('"', "'"));

					label.popover({
						placement: 'bottom',
						trigger: 'manual'
					});

					input.bind('focus', function (event) {
                        label.popover('show');
                    });

					input.bind('blur', function (event) {
                        label.popover('hide');
                    });

					// ie specific js to shim select menus
					if ($.browser.msie && $.browser.version == 6) {
						input.bind('focus', function () {
							var container = $('.popover:last');
							var element = $('.inner', container);
							var offset = element.offset();
							var shim = $('<iframe class="shim" frameborder="0" scrolling="no"></iframe>');

							// paste shim
							shim.css({
								'height': element.height(),
								'left': offset.left,
								'position': 'absolute',
								'top': offset.top,
								'width': element.width()
							}).prependTo('body');
						});

						// add blur event to destroy iframe
						input.blur('blur', function () {
							$('iframe.shim:first').remove();
						});
					}
				});
			}
		}
	};

	REGISTRATION.language_dropdown = {
		init: function () {
			$('select[name=request_locale]').bind('change', this.events.change);
		},

		events: {
			change: function (event) {
				var element = $(this);

				element.closest('form').submit();
			}
		}
	};

	// make payment help text displayed on hover of ? tips
	REGISTRATION.membership_help = {
		init: function () {
			$('.invoice').delegate('td:has(a.help)', 'mouseover', this.events.show);
		},

		events: {
			show: function (event) {
				var element = $('.membership-help');

				element.find('h1').html($('a.help', this).attr('data-title'));

				element.find('p').html($('a.help', this).attr('data-content'));

				if (element.is(':hidden')) {
					element.show();
				}
			}
		}
	};

	// make payment form choose check
	REGISTRATION.payment_check = {
		init: function () {
			if ($('.RegistrationMakePayment-page').length) {
				$('#transact_ccName').bind('change', this.events.change);
			}
		},

		events: {
			change: function (event) {
				var element = $(this);

				if (element.val() == 'Check') {
					$('.make-payment-form .creditcard').slideUp(400);
					$('.make-payment-form .expiration-date').slideUp(400);
					$('.make-payment-form .cc-note').slideUp(400);
					$('.make-payment-form .check-note').slideDown(400);
				} else {
					$('.make-payment-form .creditcard').slideDown(400);
					$('.make-payment-form .expiration-date').slideDown(400);
					$('.make-payment-form .cc-note').slideDown(400);
					$('.make-payment-form .check-note').slideUp(400);
				}
			}
		}
	};

	// make payment form submission hook to modified ccexp date format
	REGISTRATION.payment_submision = {
		init: function () {
			if ($('.RegistrationMakePayment-page').length) {
				$('.make-payment-form').bind('submit', this.events.submit);
			}
		},

		events: {
			submit: function (event) {
				var element = $(this);
				var ccName = $('#transact_ccName', element);
				var submit_element = $('#transact_button_SubmitPayment');

				// prevent double submit by "disabling" the submit button and returning false if processing
				if (!submit_element.hasClass('disabled')) {
				    submit_element.addClass('disabled');
				} else {
				    return false;
				}

				element.find('.processing').show();

				if (ccName.val() == 'Check') {
					element.attr('action', 'RegistrationMakePayment.action');
				}

				$('#ccexp').val($('#expMonth').val() + $('#expYear').val());
			}
		}
	};

	// toggle between the sections that are displayed on the service evaluation page
	REGISTRATION.services_performed_toggle = {
		init: function () {
			if ($('.RegistrationServiceEvaluation-page').length) {
				$('.services-list input[type=checkbox]').bind('click', this.events.toggle);
			}
		},

		events: {
			toggle: function (event) {
				var services = $('.services-list input[type=checkbox]');
				var service_safety_evaluation = $('.service-safety-evaluation');
				var product_safety_evaluation = $('.product-safety-evaluation');

				var service_safety_evaluation_display = false;
				var product_safety_evaluation_display = false;

				$.each(services, function (key, value) {
					var element = $(value);
					var element_id = element.attr('id');
					var is_checked = element.is(':checked');

					if ($.inArray(element_id, ['onSite', 'offSite']) != -1 && is_checked) {
						service_safety_evaluation_display = true;
					} else if($.inArray(element_id, ['materialSupplier', 'transportation']) != -1 && is_checked) {
						product_safety_evaluation_display = true;
					}
				});

				if (service_safety_evaluation_display) {
					service_safety_evaluation.slideDown(400);
				} else {
					service_safety_evaluation.slideUp(400);
				}

				if (product_safety_evaluation_display) {
					product_safety_evaluation.slideDown(400);
				} else {
					product_safety_evaluation.slideUp(400);
				}
			}
		}
	};

	PICS.define('registration.Registration', {
	    methods: {
	        init: function () {
	            $('.Registration-page .contractor-agreement.modal-link').bind('click', this.showContractorAgreementModal);
	            $('.RegistrationMakePayment-page .contractor-agreement.modal-link').bind('click', this.showContractorAgreementModal);
	            $('.RegistrationMakePayment-page .modal-link:not(.contractor-agreement)').bind('click', this.showBasicModal);
	        },

	        showBasicModal: function (event) {
	            var element = $(this);

	            PICS.ajax({
	                url: element.attr('data-url'),
                    success: function (data, textStatus, XMLHttpRequest) {
                        var modal = PICS.modal({
                            height: 550,
                            width: 700,
                            title: element.text(),
                            content: data
                        });

                        modal.show();
                    }
	            });
	        },

	        showContractorAgreementModal: function (event) {
	            var element = $(this);

                PICS.ajax({
                    url: element.attr('data-url'),
                    success: function (data, textStatus, XMLHttpRequest) {
                        var modal = PICS.modal({
                            height: 550,
                            width: 700,
                            title: element.text(),
                            content: data,
                            buttons: [{
                                html: '<a href="ContractorAgreement!print.action" class="btn info" target="_blank">' + translate('JS.global.print') + '</a>'
                            }]
                        });

                        modal.show();
                    }
                });
	        }
	    }
	});
})(jQuery);




