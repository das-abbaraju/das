/**
 * Ajax Binding
 *
 * This re-intializes jQuery plugin elements for code returned in an ajax request.
 *
 */
PICS.define('employee-guard.BindjQueryElements', {
	methods: (function () {
	    function init() {
	    }

        function tooltips() {
            $('body').tooltip({
                selector:'[data-toggle=tooltip]'
            });
        }

        function datePicker() {
            var date_picker = PICS.getClass('employee-guard.DatePicker');

            date_picker.init();
        }

        function bindAll() {
        	tooltips();
        	datePicker();
        }

        return {
            init: init,
            tooltips: tooltips,
            datePicker: datePicker
        };

	}())
});

