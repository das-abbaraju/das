// Provides an alternative to Bootstrap 3's styling of table stripes,
// e.g., for ie8, because BS3 uses the unsupported nth-child selector.
// Accepts an optional selector so that it may be used independently of Bootstrap 3
// e.g., to stripe search results
PICS.define('widget.TableStripe', {
    methods: (function () {
        function init(selector) {
            var row_selector = selector || '.table-striped tr';

            var table_striped_rows = $(row_selector),
                odd_rows = table_striped_rows.filter(':odd'),
                even_rows = table_striped_rows.filter(':even');

            odd_rows.addClass('odd');
            even_rows.addClass('even');
        }

        return {
            init: init
        };
    }())
});