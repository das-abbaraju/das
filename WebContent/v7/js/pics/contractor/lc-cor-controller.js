(function ($) {
    PICS.define('contractor.LCCoreController', {
        methods: {
            init: function () {
                if ($('#lc_cor_quote').length > 0) {
                    var that = this;

                    $('#lc_cor_quote').on('click', '.provCheckBox', function (event) {
                        that.toggleHiddenFields(event.currentTarget);
                    });

                    //show pre-selected fields
                    $('.provCheckBox').each(function (index, element) {
                        that.toggleHiddenFields(element);
                    });
                }
            },

            toggleHiddenFields: function (element) {
                var element = $(element),
                current_row = element.closest('tr');

                if (element.is(':checked')) {
                    current_row.find('.province-details').removeClass('hidden');
                } else {
                    current_row.find('.province-details').addClass('hidden');
                }
            }
        }
    });
})(jQuery);