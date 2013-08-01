PICS.define('country.Country', {
    methods: {
    	renderSubdivision: function (data) {
            var subdivision_data = $.trim(data),
                $subdivision = $('.countrySubdivision'),
                $subdivision_select = $subdivision.find('select')

            if (subdivision_data.length > 0) {
                $subdivision.html(subdivision_data);

                $subdivision.slideDown(400);

                $subdivision.find('select').select2();
            } else {
                $subdivision.slideUp(400);
                $subdivision.html('');
            }
        }
    }
});


