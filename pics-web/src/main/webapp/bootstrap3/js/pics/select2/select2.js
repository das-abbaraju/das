//any element with 'select2' as a class generates a default select2
//any element with 'select2Min' as a class generates a select2 without the searcn field
PICS.define('select2.Select2', {
    methods: (function () {
        function init() {
            configureSelect2();
            configureSelect2NoSearch();
        }

        function configureSelect2() {
            var $select2Default = $('.select2');

            if ($select2Default.length) {
                $select2Default.select2({
                    width: getSelect2Width()
                });

                $select2Default.trigger('intialized.select2');
            }
        }

        function configureSelect2NoSearch() {
            var $select2Min = $('.select2Min');

            if ($select2Min.length) {
                $select2Min.select2({
                    minimumResultsForSearch: -1,
                    width: getSelect2Width()
                });

                $select2Min.trigger('intialized.select2');
            }
        }

        //set select2 width different for bootstrap vs v6 layout
        function getSelect2Width() {
            return $('body').data('is-bootstrap') ? '100%' : 'resolve';
        }

        return {
            init: init
        };
    }())
});