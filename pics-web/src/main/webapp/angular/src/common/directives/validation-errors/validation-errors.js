angular.module('PICS.directives')

.directive('validationErrors', function () {
    return {
        restrict: 'A',
        scope: {
            validationErrors: '=',
            onChange: '=inlineValidation'
        },
        link: function (scope, element, attr) {
            var $form = element,
                $field = $form.find('[ng-model]');

            $field.on('blur', function (event) {
                var field = $(event.target),
                    fieldValue = field.val();

                // Blur only handles "required" validation
                if (fieldValue !== '') return;

                var fieldModel = field.attr('ng-model'),
                    keys = fieldModel.split('.'),
                    formKey = keys[0],
                    fieldKey = keys[1];

                scope.onChange(formKey, fieldModel, fieldKey, fieldValue);
            });

            $field.on('change', function (event) {
                var field = $(event.target),
                    fieldValue = field.val();

                // Blur event handles empty "required" fields
                if (!fieldValue) return;

                var fieldModel = field.attr('ng-model'),
                    keys = fieldModel.split('.'),
                    formKey = keys[0],
                    fieldKey = keys[1];

                scope.onChange(formKey, fieldModel, fieldKey, fieldValue);
            });

            function displayValidationErrors(validationErrors) {
                var field_with_error;

                if (validationErrors) {
                    angular.forEach(validationErrors, function (errorMessages, index) {
                        if (index == 'PICS.DUPLICATE') {
                            showDuplicateMessage($form, errorMessages[0]);
                        }
                        field_with_error = $form.find('[ng-model="' + index + '"]');
                        field_with_error.closest('.form-group').addClass('has-error');
                        field_with_error.siblings('.help-block').remove();
                        field_with_error.after('<span class="help-block">' + errorMessages[0] + '</span>');
                    });
                } 
            }

            function showDuplicateMessage($form, errorMessage) {
                var fields = errorMessage.split(','),
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

                angular.forEach(fields, function (field, index) {
                    list.push('<li>' + field + '</li>');
                });

                list.push('</ul>');

                return list.join('');
            }

            function clearValidationErrors() {
                $form.find('.has-error').removeClass('has-error');
                $form.find('.has-error .help-block').remove();
                $form.find('.alert').remove();
            }

            // we pass the fieldModel as a separate property so we can access it easier
            function clearValidationErrorsForField(fieldModel) {
                var $field;

                if (fieldModel == 'PICS.DUPLICATE') {
                    $form.find('.alert').remove();
                } else {
                    $field = $form.find('[ng-model="' + fieldModel + '"]');

                    if ($field) {
                        $field.closest('.form-group').removeClass('has-error');
                        $field.siblings('.help-block').remove();
                    }
                }
            }

            scope.$watch('validationErrors', function (validationErrors) {
                if (!validationErrors) return;

                if (validationErrors.fieldModel) {
                    clearValidationErrorsForField(validationErrors.fieldModel);
                } else {
                    clearValidationErrors($form);
                }

                displayValidationErrors(validationErrors.errors);
            }, true);
        }
    };
});