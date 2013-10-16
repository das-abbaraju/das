PICS.define('employee-guard.AjaxFormEditing', {
    methods: (function () {
        function init() {
            $('.edit-toggle').on('click', requestEditForm);
            $('.edit-container').on('click', '[type="submit"]', submitFormForValidation);
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

        function submitFormForValidation(event) {
            var $element = $(event.target),
                $container = $element.closest('.edit-container'),
                $form = $container.find('form'),
                url = $container.attr('data-url'),
                data = $form.serializeArray();

            //halt form submission
            event.preventDefault();

            // serialized form including json validator interceptors
            data.push({
                name: 'struts.enableJSONValidation',
                value: true
            }, {
                name: 'struts.validateOnly',
                value: true
            });

            PICS.ajax({
                url: url,
                context: $form,
                dataType: 'json',
                data: data,
                success: displayValidationErrors
            });
        }

        function displayValidationErrors(data) {
            var $form = this,
                field_with_error;

            clearFieldErrors($form);

            if (data.fieldErrors) {
                for (var id in data.fieldErrors) {
                    field_with_error = $('[name="' + id + '"]');
                    field_with_error.closest('.form-group ').addClass("has-error");
                }
            } else {
                $form.submit();
            }
        }

        function clearFieldErrors($form) {
            $form.find('.has-error').removeClass('has-error')
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