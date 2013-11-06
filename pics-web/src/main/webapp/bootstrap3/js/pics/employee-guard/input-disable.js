PICS.define('employee-guard.InputDisable', {
    methods: (function () {
        function init() {
            $('[data-toggle="form-input"]').on('click', setInputStateFromClick);

            setInputStateFromEach();
        }

        function setInputStateFromClick(event) {
        	setInputState($(event.target));
        }

        function setInputStateFromEach() {
        	var $form_input = $('[data-toggle="form-input"]');

        	$form_input.each(getElementFromEach);
        }

        function getElementFromEach(index, element) {
        	setInputState($(element));
        }

        function setInputState($element) {
        	var targetName = $element.attr('data-target'),
                $targetName = $(targetName);

			if ($element.is(':checked')) {
				disableInput($targetName);
			} else {
				enableInput($targetName);
			}
        }

	    function disableInput($target) {
	    	$target.attr('disabled', 'disabled');
	    	$target.find('select.select2').select2('enable', false);
	    }

	    function enableInput($target) {
			$target.removeAttr('disabled');
			$target.find('select.select2').select2('enable', true);
	    }
        return {
            init: init
        };
    }())
});