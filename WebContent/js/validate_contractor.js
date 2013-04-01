(function ($) {
    PICS.define('audit.VerifyController', {
        methods: {
            init: function () {
                if ($('#VerifyView__page').length > 0) {
                    var that = this;

                    $('.verify-audit').on('click', function (event) {
                        that.loadAudit.call(that, event);
                    });
                }
            },

            loadAudit: function (event) {
                var element = $(event.currentTarget),
                    auditID = element.attr('data-id'),
                    that = this;

                startThinking({div:'verification_audit', type:'large', message: 'Retrieving form...'});                
                $(window).scrollTo('#verification_audit',0,0);

                PICS.ajax({
                    url: 'VerifyAuditAjax.action',
                    data: {
                        auditID: auditID
                    },
                    success: function (data, textStatus, jqXHR) {
                        var element = $('#verification_audit');

                        element.html(data);

                        that.validateOSHANumber();
                    }
                });
            },

            validateOSHANumber: function () {
                $('.oshanum').on('change', function () {
                    var element = $(this),
                        error_container = element.siblings('.error'),
                        error_message = "<div class='error'>Please enter a whole number.</div>",
                        pattern = /^\d+$/g,
                        isInteger = pattern.test(element.val());

                    if (!isInteger) {
                        element.after(error_message);
                    } else {
                        error_container.remove();
                    }
                });
            }
        }
    });
}(jQuery));

