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

            showImportFilename($element);
            updateHiddenFilenameForValidation($element);
        }

        function showImportFilename($element) {
            var $form_group = $element.closest('.form-group'),
                $display_name = $form_group.find('.filename-display'),
                filename = $element[0].files[0].name;

            if (filename) {
                $display_name.html(filename);
            }
        }

        function updateHiddenFilenameForValidation($element) {
            var $validate_filename = $('#validate-filename'),
                filename = $element[0].files[0].name;

            $validate_filename.val(filename);
        }

        function disableSubmitBtnAfterSubmit(event) {
            var $form = $(event.target),
                $submitBtn = $form.find('button[type="submit"]');

            $submitBtn.attr('disabled', true);
        }

        return {
            init: init
        };
    }())
});