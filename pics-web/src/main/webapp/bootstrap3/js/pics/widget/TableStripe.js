// Provides an alternative to Bootstrap 3's styling of table stripes,
// e.g., for ie8, because BS3 uses the unsupported nth-child selector.
PICS.define('widget.TableStripe', {
    methods: (function () {
        function init() {
            var table_striped_rows = $('.table-striped tr'),
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