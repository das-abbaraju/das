PICS.define('country.Zipcode', {
    methods: {
        render: function (selected_country, $zipcode) {
            if ($zipcode.length) {
                if (selected_country == 'AE') {
                    $zipcode.slideUp(400);
                } else {
                    $zipcode.slideDown(400);
                }
            }
        }
    }
});


