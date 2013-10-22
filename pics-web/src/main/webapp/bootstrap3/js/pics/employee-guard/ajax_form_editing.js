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
                $display_values = $container.find('.edit-display-values'),
                jQueryElements = PICS.getClass('employee-guard.BindjQueryElements');

            //hide display values
            if (request_data.length > 0) {
                hideDisplayValues($display_values);
            }

            //write form to page
            if ($container.is('section')) {
                $container.find('.content').append(request_data);
            } else {
                $container.append(request_data);
            }

            //rebind jquery plugin code
            jQueryElements.bindAll();
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

        return {
            init: init
        };
    }())
});