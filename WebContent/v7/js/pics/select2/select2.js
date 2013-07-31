PICS.define('select2.Select2', {
    methods: {
        render: function () {
            //select2 is default, select2Min has no search field
            var select2Min = $('.select2Min'),
                select2Default = $('.select2');

            if (select2Min.length) {
                select2Min.select2({
                    minimumResultsForSearch: -1,
                    width: 'element'
                });
            }

            if (select2Default.length) {
                $('.select2').select2();
            }
        }
    }
});