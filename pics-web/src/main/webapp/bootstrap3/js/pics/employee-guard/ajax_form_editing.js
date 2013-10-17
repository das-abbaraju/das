PICS.define('employee-guard.AjaxFormEditing', {
    methods: (function () {
        function init() {
            $('.edit-toggle').on('click', requestEditForm);
            $('.edit-container').on('click', '.cancel', cancelEdit);
        }

       function requestEditForm(event) {
            var $element = $(event.target),
                $container = $element.closest('.edit-container'),
                url = $container.attr('data-url');

            disableAllEditToggles();

            PICS.ajax({
                url: url,
                context: $container,
                success: showEditForm,
                error: enableAllEditToggles
            });
        }

        function showEditForm(request_data) {
            var $container = this,
                $display_values = $container.find('.edit-display-values');

            if (request_data.length > 0) {
                hideDisplayValues($display_values);
            }

            $container.append(request_data);

            bindHandlersAfterAjaxRequest()
        }

        function bindHandlersAfterAjaxRequest() {
            enableToolips();
            enableDatePicker();
        }

        function cancelEdit(event){
            var $element = $(event.target),
                $container = $element.closest('.edit-container'),
                $form = $container.find('form'),
                $display_values = $container.find('.edit-display-values');

            showDisplayValues($display_values);

            $form.remove();

            enableAllEditToggles();
        }

        function hideDisplayValues($element) {
            $element.hide();
        }

        function showDisplayValues($element) {
            $element.show();
        }

        function disableAllEditToggles() {
            $('.edit-toggle').hide();
        }

        function enableAllEditToggles() {
            $('.edit-toggle').show();
        }

        function enableToolips() {
            $('body').tooltip({
                selector:'[data-toggle=tooltip]'
            });
        }

        function enableDatePicker() {
            var date_picker = PICS.getClass('employee-guard.DatePicker');

            date_picker.init();
        }

        return {
            init: init
        };
    }())
});