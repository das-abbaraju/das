(function ($) {
    PICS.define('billing.InvoiceDetail', {
        methods: (function () {
            function init() {
                if ('.InvoiceDetail-page') {
                    get$itemsTable().on('click', '#revenue_recognition_button', toggleRevRecColumn);
                }
            }

            function get$itemsTable() {
                if (!get$itemsTable.cache) {
                    get$itemsTable.cache = $('.invoice_items');
                }

                return get$itemsTable.cache;
            }

            function get$revRec() {
                if (!get$revRec.cache) {
                    get$revRec.cache = get$itemsTable().find('.revenue-recognition');
                }

                return get$revRec.cache;
            }

            function get$totals() {
                if (!get$totals.cache) {
                    get$totals.cache = get$itemsTable().find('.totals');
                }

                return get$totals.cache;
            }

            function get$taxItemDescription() {
                if (!get$taxItemDescription.cache) {
                    get$taxItemDescription.cache = get$itemsTable().find('.tax-item-description');
                }

                return get$taxItemDescription.cache;
            }

            function toggleRevRecColumn(event) {
                $button = $(event.target);

                if ($button.hasClass('hide-revrec')) {
                    $button.removeClass('hide-revrec');
                    $button.addClass('show-revrec');
                    $button.val('Hide Rev Rec');

                    showRevRecColumns();
                } else {
                    $button.removeClass('show-revrec');
                    $button.addClass('hide-revrec');
                    $button.val('Show Rev Rec');

                    hideRevRecColumns();
                }
            }

            function showRevRecColumns() {
                var $table = get$itemsTable();

                get$revRec().show();
                get$totals().attr('colspan', 4);
                get$taxItemDescription().attr('colspan', 3);
            }

            function hideRevRecColumns() {
                var $table = get$itemsTable();

                get$revRec().hide();
                get$totals().attr('colspan', 2);
                get$taxItemDescription().attr('colspan', 1);
            }
            return {
                init: init
            };
        }())
    });
})(jQuery);