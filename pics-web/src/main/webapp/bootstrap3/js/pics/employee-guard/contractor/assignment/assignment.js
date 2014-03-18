PICS.define('employee-guard.Assignment', {
    methods: (function () {
        var $selected_row,
            is_assigned;

        function init() {
            $('.table-assignment:not(.view-only)').on('click', 'tr', onAssignmentRowClick);
            $('.table-assignment a').on('click', function (event) {
                event.stopPropagation();
            });

            $('body .modal-footer .unassign').on('click', unassignEmployee);
        }

        function onAssignmentRowClick(event) {
            var $element = $(event.target);

            $selected_row = $element.closest('tr');
            is_assigned = $selected_row.hasClass('assigned');

            toggleEmployeeAssignment();
        }

        function toggleEmployeeAssignment() {
            if (is_assigned) {
                if ($('.unassignModal').length) {
                    $('.unassignModal').modal('show');
                } else {
                    unassignEmployee();
                }
            } else {
                assignEmployee();
            }
        }

        function assignEmployee() {
            var assign_url = $selected_row.attr('data-assign-url');

            PICS.ajax({
                url: assign_url,
                dataType: 'json',
                success: onEmployeeAssignmentRequestSuccess
            });
        }

        function unassignEmployee() {
            var unassign_url = $selected_row.attr('data-unassign-url');

            if ($('.unassignModal').length) {
                $('.unassignModal').modal('hide');
            }

            PICS.ajax({
                url: unassign_url,
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

            updateSkillStatusIconClass();
        }

        function updateSkillStatusIconClass() {
            var $skill_status_column = $selected_row.find('.skill-status-icon');

            $skill_status_column.each(function(index,element) {
                var $element = $(element),
                    $icon = $element.find('i');

                if ($icon.hasClass('icon-minus-sign-alt')) {
                    $element.addClass('danger');
                } else if ($icon.hasClass('icon-warning-sign')) {
                    $element.addClass('warning');
                } else if ($icon.hasClass('icon-ok-circle')) {
                    $element.addClass('success');
                } else if ($icon.hasClass('icon-ok-sign')) {
                    $element.addClass('success');
                }
            });
        }

        return {
            init: init
        };
    }())
});
