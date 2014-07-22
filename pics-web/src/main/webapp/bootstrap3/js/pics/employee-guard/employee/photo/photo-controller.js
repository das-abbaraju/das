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
                photoValidation = PICS.getClass('employee-guard.FormValidation'),
                fileUpdateCtrl = PICS.getClass('employee-guard.FileUpload'),
                filename = fileUpdateCtrl.getFileNameFromFileInput(event);

            if (filename) {
                $('#validate-filename').val(filename);
            }

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