PICS.define('employee-guard.FileUpload', {
    methods: (function () {
        function init() {
            $('.btn-import').on('click', importFile);
            $('.file-import').on('change', showImportFilename);
        }

        function importFile(event) {
            var $import_button = $(event.target),
                $form = $import_button.closest('form'),
                $file_input = $form.find('input[type="file"]');

            $file_input.click();
        }

        function showImportFilename(event) {
            var $element = $(event.target),
                $form_group = $element.closest('.form-group'),
                $display_name = $form_group.find('.filename-display'),
                filename = $element[0].files[0].name;

            if (filename) {
                $display_name.html('<p>' + filename + '</p>');
            }
        }

        return {
            init: init
        };
    }())
});