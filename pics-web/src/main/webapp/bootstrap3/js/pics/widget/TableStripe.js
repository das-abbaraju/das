// Provides an alternative to Bootstrap 3's styling of table stripes,
// e.g., for ie8, because BS3 uses the unsupported nth-child selector.
// Accepts an optional selector so that it may be used independently of Bootstrap 3
// e.g., to stripe search results
PICS.define('widget.TableStripe', {
    methods: (function () {
        function init() {
            $('.table-striped').each(function() {
                var row_selector = $(this).find('tr'),
                    odd_rows = row_selector.filter(':odd'),
                    even_rows = row_selector.filter(':even');

                odd_rows.addClass('odd');
                even_rows.addClass('even');
            });
        }

        return {
            init: init
        };
    }())
});