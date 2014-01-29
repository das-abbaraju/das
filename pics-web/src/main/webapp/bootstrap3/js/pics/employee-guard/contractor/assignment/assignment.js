PICS.define('employee-guard.Assignment', {
    methods: (function () {
        var $selected_row,
            is_assigned;

        function init() {
            $('.table-assignment').on('click', 'tr', onAssignIconClick);
            $('.disable-assignment').on('click', function (event) {
                event.stopPropagation();
            });
        }

        function onAssignIconClick(event) {
            var $element = $(event.target);

            event.preventDefault();

            $selected_row = $element.closest('tr');
            is_assigned = $selected_row.hasClass('assigned');

            requestEmployeeAssignment();
        }

        function requestEmployeeAssignment() {
            var request_url = $selected_row.attr('data-assign-url'),
                unassign_url = $selected_row.attr('data-unassign-url');

            if (is_assigned) {
                request_url = unassign_url;
            }

            PICS.ajax({
                url: request_url,
                dataType: 'json',
                success: onEmployeeAssignmentRequestSuccess
            });
        }

        function onEmployeeAssignmentRequestSuccess(data) {
            if (data.status == "SUCCESS") {
                toggleAssignedState();
            }
        }

        function toggleAssignedState() {
            if (is_assigned) {
                removeAssignedState();
            } else {
                addAssignedState();
            }
        }

        function removeAssignedState() {
            var $skill_status_column = $selected_row.find('.skill-status-icon');

            $selected_row.removeClass('assigned');

            $skill_status_column.removeClass('success danger warning');
        }

        function addAssignedState() {
            $selected_row.addClass('assigned');

            updateSkillStatusIcon();
        }

        function updateSkillStatusIcon() {
            var $skill_status_column = $selected_row.find('.skill-status-icon'),
                $icon = $skill_status_column.find('i');

            if ($icon.hasClass('icon-minus-sign-alt')) {
                $skill_status_column.addClass('danger');
            } else if ($icon.hasClass('icon-warning-sign')) {
                $skill_status_column.addClass('warning');
            } else if ($icon.hasClass('icon-ok-circle')) {
                $skill_status_column.addClass('success');
            } else if ($icon.hasClass('icon-ok-sign')) {
                $skill_status_column.addClass('success');
            }
        }

        return {
            init: init
        };
    }())
});