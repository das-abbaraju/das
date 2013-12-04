//any element with 'select2' as a class generates a default select2
//any element with 'select2Min' as a class generates a select2 without the searcn field
PICS.define('select2.Select2', {
    methods: (function () {
        function init() {
            createSelect2Element();
        }

        function createSelect2Element() {
            var select2Min = $('.select2Min'),
                select2Default = $('.select2');

            if (select2Min.length) {
                select2Min.select2({
                    minimumResultsForSearch: -1,
                    width: 'resolve'
                });
            }
            if (select2Default.length) {
                $('.select2').select2({
                    width: 'resolve'
                });
            }

            $('.select2').trigger('intialized.select2');
        }

        return {
            init: init
        };
    }())
});