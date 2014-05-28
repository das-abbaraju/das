PICS.define('employee-guard.employee.photo.PhotoController', {
    methods: (function () {
        function init() {
            $('.employee-image .overlay-container').on('click', changeEmployeePhoto);
            $('#edit_employee_photo input[type="file"]').on('change', saveAndSubmitFileUploadForm);
        }

        function saveAndSubmitFileUploadForm(event) {
            var $element = $(event.target),
                $figure = $element.closest('figure'),
                $file_input = $figure.find('input[type="file"]'),
                photoValidation = PICS.getClass('employee-guard.FormValidation');

            //update hidden field that holds filename for validation
            $('#validate-filename').val($file_input[0].files[0].name);

            //send for validation
            photoValidation.submitFormForValidation(event);
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