// Provides an alternative to Bootstrap 3's styling of table stripes,
// e.g., for ie8, because BS3 uses the unsupported nth-child selector.
// Accepts an optional selector so that it may be used independently of Bootstrap 3
// e.g., to stripe search results
PICS.define('widget.StripeCollection', {
    methods: (function () {
        function init(selector) {
            stripeBySelector(selector);
            stripeTable();
        }

        function stripeTable() {
            $('.table-striped').each(function() {
                var row_selector = $(this).find('tr'),
                    odd_rows = row_selector.filter(':odd'),
                    even_rows = row_selector.filter(':even');

                odd_rows.addClass('odd');
                even_rows.addClass('even');
            });
        }

        function stripeBySelector(selector) {
            var striped_rows = $(selector),
                odd_rows = striped_rows.filter(':odd'),
                even_rows = striped_rows.filter(':even');

            odd_rows.addClass('odd');
            even_rows.addClass('even');
        }

        return {
            init: init
        };
    }())
});