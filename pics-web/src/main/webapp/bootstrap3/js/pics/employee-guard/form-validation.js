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

                    if (id == 'PICS.DUPLICATE') {
                        showDuplicateMessage($form, data.fieldErrors[id][0]);
                    }
                    field_with_error = $('[name="' + id + '"]');
                    field_with_error.closest('.form-group').addClass('has-error');
                    field_with_error.after('<span class="help-block">' + data.fieldErrors[id][0] + '</span>');
                }
            } else {
                $form.submit();
            }
        }

        function showDuplicateMessage($form, message) {
            var fields = message.split(','),
                field_list = createFieldList(fields);

            var duplicate_message = [
                '<div class="alert alert-danger">',
                    '<h4>Oops! You have a duplicate.</h4>',
                    '<p>Please check:',
                        field_list,
                    '</p>',
                '</div>'
            ];

            duplicate_message = duplicate_message.join('');
            $form.prepend(duplicate_message);
        }

        function createFieldList(fields) {
            var list = ['<ul>'];

            for (var x = 0; x < fields.length; x++) {
                list.push('<li>' + fields[x] + '</li>');
            }

            list.push('</ul>');

            return list.join('');
        }

        function clearFieldErrors($form) {
            $form.find('.has-error .help-block').remove();
            $form.find('.has-error').removeClass('has-error');
            $form.find('.alert').remove();
        }

        return {
            init: init,
            submitFormForValidation: submitFormForValidation
        };
    }())
});