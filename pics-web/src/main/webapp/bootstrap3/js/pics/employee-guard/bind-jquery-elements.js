/**
 * Ajax Binding
 *
 * This re-intializes jQuery plugin elements for code returned in an ajax request.
 *
 * Note: If there is any binding to body in the init function of the class being reinitialized,
 *  it will cause duplicate binding
    Exmaple:
        change this:
        $('body').on('click', '[data-toggle="form-input"]', setInputStateFromClick);

        to this:
        $('[data-toggle="form-input"]').on('click', setInputStateFromClick);
 */
PICS.define('employee-guard.BindjQueryElements', {
	methods: (function () {
	    function init() {
	    }

        function tooltips() {
            var tooltips = PICS.getClass('employee-guard.Tooltip');

            tooltips.init();
        }

        function datePicker() {
            var date_picker = PICS.getClass('employee-guard.DatePicker');

            date_picker.init();
        }

        function select2() {
            var select2 = PICS.getClass('select2.Select2');

            select2.init();
        }

        function formDisable() {
            var input_disable = PICS.getClass('employee-guard.InputDisable');

            input_disable.init();
        }

        function bindAll() {
        	tooltips();
        	datePicker();
            select2();
            formDisable();
        }

        return {
            init: init,
            tooltips: tooltips,
            datePicker: datePicker,
            select2: select2,
            formDisable: formDisable,
            bindAll: bindAll
        };

	}())
});

