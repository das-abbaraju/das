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
                            var container = $('.popover:last'),element = $('.inner', container);
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
                registration_page.on('change', '#registration_language', function (event) {
                    that.getDialectList.call(that, event);
                });

                registration_page.on('change', '#dialect_selection', function (event) {
                    that.updatePageLanguageBasedOnSelectedLanguageAndDialect();
                });
		    }
		},

        getDialectList: function (event) {
            var element = $(event.currentTarget),
                language = element.val(),
                that = this;

            PICS.ajax({
                url: 'RegistrationAjax.action',
                data: {
                    language: language
                },
                success: function (data, textStatus, jqXHR) {
                    var dialect_dropdown = $.trim(data),  //trim carriage returns
                        dialect_container_element = $('#registration_dialect');

                    dialect_container_element.html(dialect_dropdown);

                    if (dialect_dropdown == '') {
                         that.updatePageLanguageBasedOnSelectedLanguageAndDialect();
                    }

                     $('#dialect_selection').select2({
                        minimumResultsForSearch: -1
                    });
                }
            });
        },

        getRequestLocale: function () {
            var language = $('[name=language]').val(),
                dialect = $('[name=dialect]').val();

            return dialect ? language + '_' + dialect : language;
        },

        updatePageLanguageBasedOnSelectedLanguageAndDialect: function (event) {
            var $input = $(document.createElement('input'));

                $input.attr({
                    type: 'hidden',
                    name: 'request_locale',
                    value: this.getRequestLocale()
                });

                $('.registration-form')
                    .append($input)
                    .submit()
                    .remove();
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
                var company_information = $('.company-information'),
                    $country_select = $('.country select');

                $('.registered-with-ssip-member-scheme-input').bind('click', this.toggleReadyToProvideSsipDetailsDisplay);
                $('.request-to-provide-ssip-details-input').bind('click', this.toggleSsipDetailsDisplay);

                if ($('.Registration-page').length) {
                    $('.registration').on('click', '#autofill', this.autofillRegistrationFormForDev);

                    $('.contractor-agreement.modal-link').on('click', this.showContractorAgreementModal);

                    this.updateTimezonesByCountry($country_select.val());

                    this.bindSelect2EventstoPopover();

                    $country_select.on('change', $.proxy(this.updateCountryFields, this));

                    this.updateCountryFields();

                } else if ($('.RegistrationServiceEvaluation-page').length) {
                    $('.service-evaluation').on('click', '#autofill', this.autofillRegistrationServiceEvaluationFormForDev);
                } else if ($('.RegistrationMakePayment-page').length) {
                    $('.modal-link:not(.contractor-agreement)').on('click', this.showBasicModal);
                    $('.contractor-agreement.modal-link').on('click', this.showContractorAgreementModal);
                }
	        },

            autofillRegistrationFormForDev: function (event) {
                var email = 'my.email' + new Date().getTime() + '@test.com';

                // Company Info
                $('[name=language]').children().first().attr('selected','selected');
                $('[name=dialect]').children().last().attr('selected','selected');
                $('[name="contractor.country.isoCode"]').children().first().attr('selected','selected');
                $('[name="contractor.timezone"]').select2('val', 'America/Los_Angeles')
                $('[name="contractor.name"]').val("My Company" +  new Date().getTime() );
                $('[name="contractor.address"]').val("123 Anywhere St");
                $('[name="contractor.city"]').val("Springfield");
                $('[name="countrySubdivision"]').select2('val', 'US-CA')
                $('[name="contractor.zip"]').val("12345");

                // Contact Info
                $('[name="user.firstName"]').val('John');
                $('[name="user.lastName"]').val('Doe');
                $('[name="user.email"]').val(email);
                $('[name="user.phone"]').val('555-555-5555');

                // Account Info
                $('[name="user.username"]').val(email);
                $('[name="user.password"]').val('password1');
                $('[name="confirmPassword"]').val('password1');
            },

            autofillRegistrationServiceEvaluationFormForDev: function (event) {
                $('input:not([value=No])').prop('checked','checked');

                $('input.month, input.day').val(12);
                $('input.year').val(2015);

                $('[name="ssipAnswerMap[16948].answer"]').children().last().attr('selected','selected');
            },

            bindSelect2EventstoPopover: function () {
                var $country = $('li.country'),
                    label = $country.find('label'),
                    select = $country.find('select');

                select.on('select2-open', function () {
                    label.popover('show');
                });

                select.on('select2-close', function (event) {
                    label.popover('hide');
                });
            },

            prefillTimezoneFromRegistrationRequestValue: function () {
                var $registration_request_timezone = $('#registration_requested_timezone'),
                    prefilled_timezone = $registration_request_timezone.val();

                if (prefilled_timezone) {
                    $('input.timezone_input').val(prefilled_timezone);
                }

                //remove prefilled timezone value to prevent submission of duplicate values
                $registration_request_timezone.remove();
            },

            renderSubdivision: function() {
                var $subdivision_container = $('.countrySubdivision'),
                    $subdivision_list = $subdivision_container.find('select option');

                if ($subdivision_list.length > 1) {
                    $subdivision_container.find('select').select2();
                }
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
	            var element = $(event.target);

                PICS.ajax({
                    url: element.attr('data-url'),
                    success: function (data, textStatus, XMLHttpRequest) {
                        var pics_phone = $('span.pics_phone_number').html();
                        var pics_display_name = $('span.pics_display_name').html();
                        var pics_address = $('span.pics_address').html();

                        var country = $('#Registration_contractor_country_isoCode').select2('val');
//                        console.log(country)

                        var modal = PICS.modal({
                            height: 550,
                            width: 700,
                            title: element.text(),
                            content: data,
                            buttons: [{
                                html: '<a href="ContractorAgreement!print.action?country='+country+'" class="btn info" target="_blank">' + translate('JS.global.print') + '</a>'
                            }]
                        });

                        var $agreement_phone = $('.modal-body').find('.pics_phone_number');
                        var $agreement_display_name = $('.modal-body').find('.pics_display_name');
                        var $agreement_address = $('.modal-body').find('.pics_address');

                        $agreement_phone.html(pics_phone);
                        $agreement_display_name.html(pics_display_name);
                        $agreement_address.html(pics_address);

                        modal.show();
                    }
                });
	        },

            toggleReadyToProvideSsipDetailsDisplay: function (event) {
                var val = $('#registeredWithSsipMemberScheme:checked').val();

                if (val == "Yes") {
                    $('.request-to-provide-ssip-details-container').slideDown(400);
                } else {
                    $('.request-to-provide-ssip-details-container').slideUp(400, function () {
                        $('input[name=readyToProvideSsipDetails]').each(function () {
                            $(this).prop('checked', false);
                        });
                    });
                }

                $('.ssip-details-container').slideUp(400);
                $('.provide-ssip-details-later-message').slideUp(400);
            },

            toggleSsipDetailsDisplay: function (event) {
                var val = $('input[name=readyToProvideSsipDetails]:checked').val();

                if (val == "Yes") {
                    $('.provide-ssip-details-later-message').slideUp(400);
                    $('.ssip-details-container').slideDown(400);
                } else {
                    $('.ssip-details-container').slideUp(400);
                    $('.provide-ssip-details-later-message').slideDown(400);
                }
            },

            updateCountryFields: function () {
                var $country = $('.country select'),
                    selected_country = $country.val() || '';

                this.updateAddressFields(selected_country);

                this.updatePhoneNumber(selected_country);

                this.updateTimezonesByCountry(selected_country);

                this.updateZipcode(selected_country);

                this.updateTaxIdLabel(selected_country);
            },

            // HACK to pull custom address fields for the UK
            updateAddressFields: function (selected_country) {
                var that = this;

                PICS.ajax({
                    url: 'Registration!getCompanyAddressFields.action',
                    data: {
                        'contractor.country.isoCode': selected_country,
                        country_iso_code: selected_country
                    },
                    success: function (data, textStatus, jqXHR) {
                        $('#company_address_fields').html(data);
                        that.renderSubdivision();
                    }
                });
            },

            updatePhoneNumber: function (selected_country) {
                PICS.ajax({
                    url: 'CountrySubdivisionListAjax!phone.action',
                    dataType: 'json',
                    data: {
                        countryString: selected_country
                    },
                    success: function (data, textStatus, XMLHttpRequest) {
                        var $pics_phone = $('.pics_phone_number');

                        $pics_phone.each(function (index, element) {
                            $(element).html(data.picsPhoneNumber);
                            $(element).attr("title", data.country);
                        });
                    }
                });
            },

            updateTimezonesByCountry: function (selected_country) {
                var Country = PICS.getClass('country.Country');

                this.prefillTimezoneFromRegistrationRequestValue();

                Country.getTimezones(selected_country);
            },

            updateZipcode: function (selected_country) {
                var Country = PICS.getClass('country.Country');

                Country.modifyZipcodeDisplay(selected_country);
            },

            updateTaxIdLabel: function (selected_country) {
                PICS.ajax({
                    url: 'TaxIdCountryAJAX.action',
                    dataType: 'json',
                    data: {
                        iso_code: selected_country,
                        locale: REGISTRATION.language_dropdown.getRequestLocale()
                    },
                    success: function (data) {
                        var tax_id_required = data.tax_id_required,
                            label_text = data.label,
                            tax_id_item_el =$('#tax_id'),
                            tax_id_label_el;

                        if (tax_id_required) {
                            tax_id_label_el = tax_id_item_el.find('label');

                            tax_id_label_el.text(label_text);

                            tax_id_item_el.slideDown(400);
                        } else {
                            tax_id_item_el.slideUp(400);
                        }
                    }
                });
            }
	    }
	});
})(jQuery);
