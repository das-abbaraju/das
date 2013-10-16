PICS.define('employee-guard.EmployeeGUARD', {
    methods: (function () {
        function init() {
        	enableIconToolTips();
        }

        function enableIconToolTips() {
			$('i').tooltip();
        }

        return {
            init: init
        };
    }())
});