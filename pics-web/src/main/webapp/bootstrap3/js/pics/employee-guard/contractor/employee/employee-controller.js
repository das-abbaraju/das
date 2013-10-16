PICS.define('employee-guard.contractor.employee.EmployeeController', {
    methods: (function () {
        function init() {
            if ($('#employee_guard_contractor_employee_import_export_importExport_page').length > 0) {
                $('#contractor_employee_import_form input[type="file"]').on('change', saveAndSubmitFileUploadForm);
            }
        }

        function saveAndSubmitFileUploadForm(event) {
            var $file_input = $(event.target),
                $form = $file_input.closest('form');

            $form.submit();
        }

        return {
            init: init
        };

    }())
});
