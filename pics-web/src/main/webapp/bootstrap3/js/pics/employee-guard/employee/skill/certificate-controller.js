PICS.define('employee-guard.employee.certificate.CertificateController', {
    methods: (function () {
        function init() {
        	var $certificate_page = $('.employee_guard_employee_skills_certificate-page');

            if ($certificate_page.length > 0) {
				$certificate_page.on('click', '.checkbox .no-expiration', toggleExpirationFields)
            }
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
