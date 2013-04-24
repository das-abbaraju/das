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

	// contractor create account country / country subdivision toggle
	REGISTRATION.contractor_country = {
		init: function () {
			if ($('.Registration-page').length) {
			    var that = this;

				$('.contractor-country').on('change', function (event) {
				    that.events.update_countrySubdivision_list.call(that, event);
				});

				var selectedSubdivision = null;
				
				if ($('#requested_contractor').length) {
			    	var countrySubdivision_element = $('.registration-form li.countrySubdivision');

			    	selectedSubdivision = countrySubdivision_element.find('select option:selected').val();
			    }
			}
		},

		events: {
			update_countrySubdivision_list: function (selectedSubdivision) {
				var country_select = $('.contractor-country') || $(event.currentTarget),
				    country_string = country_select.val(),
				    that = this;

				PICS.ajax({
					url: 'CountrySubdivisionListAjax!registration.action',
					data: {
						countryString: country_string,
						prefix: 'contractor.'
					},
					success: function (data, textStatus, XMLHttpRequest) {
						var countrySubdivision_element = $('.registration-form li.countrySubdivision'),
						    zip_element = $('.registration-form li.zip');

						countrySubdivision_element.html(data);

						if ($.trim(data) == '') {
							countrySubdivision_element.slideUp(400);
						} else {
							countrySubdivision_element.slideDown(400);
						}

						if (country_string == 'AE') {
							zip_element.slideUp(400);
						} else {
							zip_element.slideDown(400);
						}
						
						if (selectedSubdivision) {
							countrySubdivision_element.find("select").val(selectedSubdivision);
						}

						that.events.updatePicsPhone(country_string);
					}
				});
			},

			updatePicsPhone: function (country_string) {
                PICS.ajax({
                    url: 'CountrySubdivisionListAjax!phone.action',
                    dataType: 'json',
                    data: {
                        countryString: country_string
                    },
                    success: function (data, textStatus, XMLHttpRequest) {
                        var pics_phone = $('#pics_phone_number');

                        if (pics_phone.length > 0) {
                            pics_phone.html(data.picsPhoneNumber);
                            pics_phone.attr("title", data.country);
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
		    var that = this,
		        registration_page = $('.Registration-page');

		    if (registration_page.length > 0) {
                registration_page.on('change', '#dialect_selection', function (event) {
                    that.setDialect.call(that, event);
                });

                registration_page.on('change', '#registration_language', function (event) {
                    that.getDialectList.call(that, event);
                });
		    }
		},

        getDialectList: function (event) {
            var element = $(event.currentTarget),
                language = element.val(),
                request_locale = $("#request_locale");

            PICS.ajax({
                url: 'RegistrationAjax.action',
                data: {
                    language: language
                },
                success: function (data, textStatus, jqXHR) {
                    var dialect_dropdown = $.trim(data),  //trim carriage returns
                        dialect_container_element = $('#registration_dialect');

                    dialect_container_element.html(dialect_dropdown);

                    //if no dialects, refresh page
                    if (dialect_dropdown == '') {
                        request_locale.val(language);
                        element.closest('form').submit();
                    }
                }
            });
        },

        setDialect: function (event, base_language) {
            var element = $(event.currentTarget),
                dialect = element.val(),
                language = $('#registration_language').val(),
                request_locale = $("#request_locale");

            request_locale.val(language + "_" + dialect);

            element.closest('form').submit();
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
				var product_safety_evaluation = $('.product-safety-evaluation');
                var business_interruption_evaluation = $('.business_interruption_evaluation');
                var transportation_evaluation = $('.transportation_evaluation');

                var request_to_provide_ssip_details_display = false;
                var ssip_evaluation_display = false;
				var product_safety_evaluation_display = false;
                var business_interruption_evaluation_display = false;
                var transportation_evaluation_display = false;

				$.each(services, function (key, value) {
					var element = $(value);
					var element_id = element.attr('id');
					var is_checked = element.is(':checked');

					if($.inArray(element_id, ['materialSupplier']) != -1 && is_checked) {
						product_safety_evaluation_display = true;
						business_interruption_evaluation_display = true;
					}
					if($.inArray(element_id, ['transportation']) != -1 && is_checked) {
					    transportation_evaluation_display = true;
					}
				});

				if (product_safety_evaluation_display) {
					product_safety_evaluation.slideDown(400);
				} else {
					product_safety_evaluation.slideUp(400);
				}

                if (business_interruption_evaluation_display) {
                    business_interruption_evaluation.slideDown(400);
                } else {
                    business_interruption_evaluation.slideUp(400);
                }
                
                if (transportation_evaluation_display) {
                    transportation_evaluation.slideDown(400);
                } else {
                    transportation_evaluation.slideUp(400);
                }
			}
		}
	};

	PICS.define('registration.Registration', {
	    methods: {
	        init: function () {
                var company_information = $('.company-information');

                $('.registered-with-ssip-member-scheme-input').bind('click', this.toggleRequestToProvideSsipDetailsDisplay);
                $('.request-to-provide-ssip-details-input').bind('click', this.toggleSsipDetailsDisplay);

                $('.Registration-page .contractor-agreement.modal-link').bind('click', this.showContractorAgreementModal);
                $('.RegistrationMakePayment-page .contractor-agreement.modal-link').bind('click', this.showContractorAgreementModal);
                $('.RegistrationMakePayment-page .modal-link:not(.contractor-agreement)').bind('click', this.showBasicModal);
                company_information.delegate('#Registration_contractor_country_isoCode', 'change', this.checkVatRequired);
                // Show or hide the vat id field based on the Country default value.
                company_information.find('#Registration_contractor_country_isoCode').trigger('change');
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
	        },

	        checkVatRequired: function (event) {
                var iso_code = $('#Registration_contractor_country_isoCode').val();

                PICS.ajax({
                    url: 'VATCountryAJAX.action',
                    data: {
                        iso_code: iso_code
                    },
                    dataType: 'json',
                    success: function (data, textStatus, XMLHttpRequest) {
                        var vat_element = $('#vat_id');

                        if (data.vat_required) {
                            vat_element.slideDown(400);
                        } else {
                            vat_element.slideUp(400);
                        }
                    }
                });
            },

            toggleRequestToProvideSsipDetailsDisplay: function (event) {
                var val = $('input[name=registeredWithSsipMemberScheme]:checked').val();

                if (val == "true") {
                    $('.request-to-provide-ssip-details-container').slideDown(400);
                } else {
                    $('.request-to-provide-ssip-details-container').slideUp(400, function () {
                        $('input[name=requestToProvideSsipDetails]').each(function () {
                            $(this).prop('checked', false);
                        });
                    });
                }

                $('.ssip-details-container').slideUp(400);
                $('.provide-ssip-details-later-message').slideUp(400);
            },

            toggleSsipDetailsDisplay: function (event) {
                var val = $('input[name=requestToProvideSsipDetails]:checked').val();

                if (val == "true") {
                    $('.provide-ssip-details-later-message').slideUp(400);
                    $('.ssip-details-container').slideDown(400);
                } else {
                    $('.ssip-details-container').slideUp(400);
                    $('.provide-ssip-details-later-message').slideDown(400);
                }
            }
	    }
	});
})(jQuery);
