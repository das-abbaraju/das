PICS.define('employee-guard.Tooltip', {
    methods: (function () {
        function init() {
            enableToolTips();
        }

        function enableToolTips() {
            $('[data-toggle="tooltip"]').tooltip();
        }

        return {
            init: init
        };
    }())
});


