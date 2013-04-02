(function ($) {
    PICS.define('registration.Validation', {
        methods: (function () {
            var jqXHR,
                current_field_name;

            return {
                init: function () {
                    if ($('.Registration-page').length) {
                        var form_fields = $('.registration-form input[type=text], .registration-form input[type=password]'),
                            threshold = 250;

                        form_fields.on({
                            focusout: this.validate,
                            keypress: PICS.debounce(this.validate, threshold)
                        }, {
                            that: this
                        });
                    }
                },

                buildFieldError: function (field, field_errors) {
                    var field_name = field.attr('name'),
                        field_error = field.siblings('.error'),
                        html = [];

                    html.push('<ul class="errors">');

                    $.each(field_errors, function (key, value) {
                        html.push('<li>' + value + '</li>')
                    });

                    html.push('</ul>');

                    return html.join('');
                },

                clearFieldError: function (field) {
                    var field_error = field.siblings('.errors');

                    if (field_error.length > 0) {
                        field_error.remove();
                    }
                },

                displayFieldError: function (field, field_errors) {
                    var field_error = field.siblings('.errors'),
                        html = this.buildFieldError(field, field_errors);

                    if (field_error.length > 0) {
                        field_error.replaceWith(html);
                    } else {
                        field.after(html);
                    }
                },

                getFormData: function (form) {
                    var data = form.serializeArray();

                    // serialized form including json validator interceptors
                    data.push({
                        name: 'struts.enableJSONValidation',
                        value: true
                    }, {
                        name: 'struts.validateOnly',
                        value: true
                    });

                    return $.param(data);
                },

                hasDuplicateCompanyNameError: function (field_name, field_errors) {
                    if (!field_errors || field_name != 'contractor.name') {
                        return false;
                    }

                    // company name already exists error should triumph all other errors
                    return $.inArray(translate('JS.Validation.CompanyNameAlreadyExists'), field_errors) != -1;
                },

                validate: function (event) {
                    var that = event.data.that,
                        field = $(event.currentTarget),
                        field_name = field.attr('name'),
                        form = field.closest('form'),
                        data = that.getFormData(form);

                    // abort duplicate field validation
                    if (jqXHR && jqXHR.abort && current_field_name == field_name) {
                        jqXHR.abort();
                    }

                    // capture XHR to abort for duplicate validations
                    jqXHR = PICS.ajax({
                        url: form.attr('action'),
                        data: data,
                        dataType: 'json',
                        success: function (data, textStatus, jqXHR) {
                            var field_errors = data.fieldErrors && data.fieldErrors[field_name];

                            // special case for company name field
                            if (field_name == 'contractor.name') {
                                // giant duplicate name alert instead of a warning message
                                var duplicate_contractor_name_error = $('.contractor-name-duplicate');

                                if (duplicate_contractor_name_error.is(':visible')) {
                                    duplicate_contractor_name_error.hide();
                                }

                                that.clearFieldError(field);

                                if (that.hasDuplicateCompanyNameError(field_name, field_errors)) {
                                    duplicate_contractor_name_error.show();
                                } else if (field_errors) {
                                    that.displayFieldError(field, field_errors);
                                }
                            } else {
                                if (field_errors) {
                                    that.displayFieldError(field, field_errors);
                                } else {
                                    that.clearFieldError(field);
                                }
                            }
                        }
                    });

                    // capture current field name to abort duplicate validations
                    current_field_name = field_name;
                }
            };
        }())
    });
}(jQuery));