//any element with 'select2' as a class generates a default select2
//any element with 'select2Min' as a class generates a select2 without the searcn field
PICS.define('select2.Select2', {
    methods: (function () {
        function init() {
            createSelect2Element();
            overrideSelect2Icons();
            $('body').on('change', '.select2', addTagCloseIcon);
        }

        function addTagCloseIcon(event) {
            var $element = $(event.target),
                $select2_container = $element.siblings('.select2'),
                $select2_close = $select2_container.find('.select2-search-choice-close');

            $select2_close.each(function() {
                if ($(this).find('i').length <= 0) {
                    $(this).append('<i class="icon-remove"></i>');
                }
            });
        }

        function createSelect2Element() {
            var select2Min = $('.select2Min'),
                select2Default = $('.select2');

            if (select2Min.length) {
                select2Min.select2({
                    minimumResultsForSearch: -1
                });
            }

            if (select2Default.length) {
                $('.select2').select2();
            }
        }

        function overrideSelect2Icons() {
            $('.select2-arrow b').remove();
            $('.select2-arrow').append('<i class="icon-caret-down"></i>');
            $('.select2-search-choice-close').append('<i class="icon-remove"></i>');
        }

        return {
            init: init
        };
    }())
});