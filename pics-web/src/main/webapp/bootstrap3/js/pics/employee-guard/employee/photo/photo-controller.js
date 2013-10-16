PICS.define('employee-guard.employee.photo.PhotoController', {
    methods: (function () {
        function init() {
            $('.employee-image .overlay-container').on('click', changeEmployeePhoto);
            $('#edit_employee_photo input[type="file"]').on('change', saveAndSubmitFileUploadForm);
        }

        function saveAndSubmitFileUploadForm(event) {
            var $file_input = $(event.target),
                $form = $file_input.closest('form');

            $form.submit();
        }

        function changeEmployeePhoto(event) {
            var $element = $(event.target),
                $figure = $element.closest('figure'),
                $file_input = $figure.find('input[type="file"]');

            //open file dialog
            $file_input.click();
        }

        return {
            init: init
        };
    }())
});