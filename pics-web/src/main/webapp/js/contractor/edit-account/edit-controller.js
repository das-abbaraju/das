(function ($) {
    PICS.define('contractor.ContractorEdit', {
        methods: {
            init: function () {
                if ($('.ContractorEdit-page').length) {
                    var that = this;

                    $('.ContractorEdit-page').delegate('#save_contractor_hasVatId', 'change', this.toggleVatField);
                    this.hasVat() ? this.showVat() : this.hideVat();
                }
            },

            hasVat: function() {
                var vat_field = $('#save_contractor_vatId');

                return (vat_field.val() == '') ? false : true;
            },

            hideVat: function() {
                var vat = {
                        wrapper: $('#vat_wrapper'),
                        checkbox: $('#save_contractor_hasVatId')
                }

                vat.checkbox.removeAttr('checked');
                vat.wrapper.hide();
            },

            showVat: function() {
                var vat = {
                        wrapper: $('#vat_wrapper'),
                        checkbox: $('#save_contractor_hasVatId')
                }

                vat.checkbox.attr('checked', 'checked')
                vat.wrapper.show();
            },

            toggleVatField: function (event) {
                var vat_checkbox = $(this),
                    vat_wrapper = $('#vat_wrapper');

                if (vat_checkbox.prop('checked')) {
                    vat_wrapper.slideDown(400);
                } else {
                    $('#save_contractor_vatId').val('');
                    vat_wrapper.slideUp(400);
                }
            }
        }
    });
})(jQuery);