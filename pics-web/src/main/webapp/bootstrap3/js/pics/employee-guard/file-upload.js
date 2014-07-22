PICS.define('employee-guard.FileUpload', {
    methods: (function () {
        function init() {
            $('body').on('click', '.btn-import', importFile);
            $('body').on('change', '.file-import', changeImportedFile);
            $('body').on('submit', '.disable-on-submit', disableSubmitBtnAfterSubmit);
        }

        function importFile(event) {
            var $import_button = $(event.target),
                $form = $import_button.closest('form'),
                $file_input = $form.find('input[type="file"]');

            $file_input.click();
        }

        function changeImportedFile(event) {
            var $element = $(event.target);

            showImportFilename($element, event);
            updateHiddenFilenameForValidation($element, event);
        }

        function showImportFilename($element, event) {
            var $form_group = $element.closest('.form-group'),
                $display_name = $form_group.find('.filename-display'),
                filename = getFileNameFromFileInput(event);

            if (filename) {
                $display_name.html(filename);
            }
        }

        function updateHiddenFilenameForValidation($element, event) {
            var $validate_filename = $('#validate-filename'),
                filename = getFileNameFromFileInput(event);

            if (filename) {
                $validate_filename.val(filename);
            }
        }

        function disableSubmitBtnAfterSubmit(event) {
            var $form = $(event.target),
                $submitBtn = $form.find('button[type="submit"]');

            $submitBtn.attr('disabled', true);
        }

        function getFileNameFromFileInput(event) {
            var file = event.target.value,
                lastSlash = file.lastIndexOf('\\'),
                filename = file.substring(lastSlash + 1, file.length);

            return filename;
        }

        return {
            init: init,
            getFileNameFromFileInput: getFileNameFromFileInput
        };
    }())
});