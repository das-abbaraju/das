PICS.define('employee-guard.Assignment', {
    methods: (function () {
        var $selected_row,
            is_assigned;

        function init() {
            $('.table-assignment').on('click', 'tr', onAssignmentRowClick);
            $('.table-assignment a').on('click', function (event) {
                event.stopPropagation();
            });
            $('body .modal-footer .unassign').on('click', requestEmployeeAssignment);
        }

        function onAssignmentRowClick(event) {
            var $element = $(event.target);

            $selected_row = $element.closest('tr');
            is_assigned = $selected_row.hasClass('assigned');

            toggleEmployeeAssignment();
        }

        function toggleEmployeeAssignment() {
            if (is_assigned) {
                $('.unassignModal').modal('show');
            } else {
                requestEmployeeAssignment();
            }
        }

        function requestEmployeeAssignment() {
            var request_url = $selected_row.attr('data-assign-url'),
                unassign_url = $selected_row.attr('data-unassign-url');

            if (is_assigned) {
                $('.unassignModal').modal('hide');
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
                if ($selected_row.hasClass('site-level')){
                    location.reload();
                } else {
                    toggleAssignedStyle();
                }
            }
        }

        function toggleAssignedStyle() {
            if (is_assigned) {
                removeAssignedStyle();
            } else {
                addAssignedStyle();
            }
        }

        function removeAssignedStyle() {
            var $skill_status_column = $selected_row.find('.skill-status-icon');

            $selected_row.removeClass('assigned');

            $skill_status_column.removeClass('success danger warning');
        }

        function addAssignedStyle() {
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
