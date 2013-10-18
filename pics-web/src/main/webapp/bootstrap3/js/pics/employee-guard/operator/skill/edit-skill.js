PICS.define('employee-guard.operator.skill.Edit', {
	methods: (function () {
	    function init() {
	    	var $skill_page = $('.employee_guard_operator_skill-page');

			if ($skill_page.length > 0) {
				$skill_page.on('click', '.checkbox .required', toggleEmployeeGroups);
				$skill_page.on('change', '.skillType', changeSkillType)
				$skill_page.on('click', '.checkbox .no-expiration', toggleExpirationFields)
				if ($('.checkbox .required').is(':checked')) {
					disableEmployeeGroups();
				}
			}
	    }

	    function changeSkillType(event) {
            var $element = $(event.target),
                $form = $element.closest('form'),
                $section = $element.closest('section');
                url = $section.attr('data-url');

            PICS.ajax({
                url: url,
                type: 'GET',
                data: $form.serialize(),
                context: $form,
                success: updateSkillForm
            });

	    }

	    function updateSkillForm(data) {
	    	var $form = this;

	    	$form.replaceWith(data);
	    	PICS.getClass('employee-guard.BindjQueryElements').tooltips();
	    }

	    function toggleEmployeeGroups(event) {
	    	var $element = $(event.target),
	    		selected = $element.is(':checked');

	    	if (selected) {
				disableEmployeeGroups();
	    	} else {
	    		enableEmployeeGroups();
	    	}
	    }

	    function disableEmployeeGroups() {
	    	$('.operator-skill-employee-groups').attr('disabled', 'disabled');
	    }

	    function enableEmployeeGroups() {
			$('.operator-skill-employee-groups').removeAttr('disabled');
	    }

	    function toggleExpirationFields(event) {
	    	var $element = $(event.target),
	    		selected = $element.is(':checked');

	    	if (selected) {
				disableExpiration();
	    	} else {
	    		enableExpiration();
	    	}
	    }

	    function disableExpiration() {
	    	$('.expiration-date').attr('disabled', 'disabled');
	    }

	    function enableExpiration() {
			$('.expiration-date').removeAttr('disabled');
	    }

        return {
            init: init
        };

	}())
});