PICS.define('employee-guard.Assignment', {
    methods: (function () {
        function init() {
            $('#employee_assignment').on('click', 'tr', assignEmployee);
            $('.disable-assignment').on('click', function (event) {
                event.stopPropagation();
            });
        }

        function assignEmployee(event) {
            var $element = $(event.target),
                $container = $element.closest('tr'),
                request_url = $container.attr('data-assign-url'),
                unassign_url = $container.attr('data-unassign-url');

            event.preventDefault();

            if ($container.hasClass('assigned')) {
                request_url = unassign_url;
            }

            if (request_url !== '') {
                PICS.ajax({
                    url: request_url,
                    dataType: 'json',
                    context: $container,
                    success: checkAssignStatus
                });
            }
        }

        function checkAssignStatus(data) {
            var $container = $(this);

            if (data.status == "SUCCESS") {
                toggleAssignedState($container);
            }
        }

        function toggleAssignedState($container) {
            if ($container.hasClass('assigned')) {
                $container.removeClass('assigned');
            } else {
                $container.addClass('assigned');
            }
        }

        return {
            init: init
        };
    }())
});