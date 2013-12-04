PICS.define('employee-guard.employee.skill.EditController', {
    methods: (function () {
        function init() {
            if ($('.employee_guard_employee_skill-page')) {
                $('body').on('change', '.file-select', changeSelectedDocument);
            }
        }

        function changeSelectedDocument(event) {
            updateDocumentEditUrl(event);
            enableFileUpdateButton();
        }

        function updateDocumentEditUrl(event) {
            var $element = $(event.target),
                $edit_btn = $('.edit-btn'),
                url = modifyEditURL($edit_btn.attr('href'), $element.val());

            $edit_btn.attr('href', url);
        }

        function modifyEditURL(url, newID) {
            var currentID = url.substr(url.lastIndexOf('/') + 1),
                newUrl = url.replace(currentID, newID);

            return newUrl;
        }

        function enableFileUpdateButton() {
            var $form_submit = $('[type="submit"]');

            $form_submit.removeAttr('disabled');
        }

        return {
            init: init
        };
    }())
});