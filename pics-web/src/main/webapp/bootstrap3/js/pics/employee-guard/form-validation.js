PICS.define('employee-guard.FormValidation', {
    methods: (function () {
        function init() {
            $('body').on('click', '.js-validation [type="submit"]', submitFormForValidation);
        }

        function submitFormForValidation(event) {
            var $element = $(event.target),
                $form = $element.closest('form'),
                url = url = $form.attr('action'),
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
                    field_with_error.closest('.form-group').addClass("has-error");
                }
            } else {
                $form.submit();
            }
        }

        function clearFieldErrors($form) {
            $form.find('.has-error').removeClass('has-error')
        }

        return {
            init: init
        };
    }())
});