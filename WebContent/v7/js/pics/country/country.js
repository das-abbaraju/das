PICS.define('country.Country', {
    methods: (function () {

        // Private

        function getTimezoneFormat(data) {
            var resultArr = [
                '<article class="timezone-data">',
                    '<p>',
                        '<span class="zone">' + data.id + '</span>',
                        '<span class="date">',
                            '(UTC' + data.offset + ') ',
                            data.date,
                        '</span>',
                    '</p>',
                    '<p class="time">',
                        data.time,
                    '</p>',
                '</article>'
            ];

            return resultArr.join('');
        }

        // Public

        function getTimezones(country, callback) {
            PICS.ajax({
                url: 'TimeZoneRetriever.action',
                data: {
                    countryCode: country
                },
                dataType: 'json',
                success: function(data, textStatus, jqXHR) {
                    if (callback) {
                        callback(data);
                    } else {
                        renderTimezoneList(data);
                    }
                }
            });
        }

        function modifyZipcodeDisplay(selected_country) {
            var $zipcode = $('.zipcode');

            if ($zipcode.length) {
                if (selected_country == 'AE') {
                    $zipcode.slideUp(400);
                } else {
                    $zipcode.slideDown(400);
                }
            }
        }

        function renderSubdivision(subdivision_list) {
            var subdivision_list = $.trim(subdivision_list),
                $subdivision_container = $('.countrySubdivision');

            if (subdivision_list.length > 0) {
                $subdivision_container.html(subdivision_list);

                $subdivision_container.find('select').select2({width: 'auto'});

                $subdivision_container.slideDown(400);
            } else {
                $subdivision_container.slideUp(400);
                $subdivision_container.html('');
            }
        }


        function renderTimezoneList(data, callback) {
            var $timezone_input = $('input.timezone_input');

            $timezone_input.select2({
                data: data.result,
                minimumResultsForSearch: -1,
                width: 'element',
                escapeMarkup:function (markup) {
                    return markup;
                },
                formatResult: getTimezoneFormat,
                formatSelection: getTimezoneFormat,
                initSelection: function (element, callback) {
                    var result = data.result,
                        selected = '';

                    //select pre-set value
                    for (x = 0; x < result.length; x++) {
                        if (result[x].id ==  $(element).val()) {
                            selected = result[x];
                        }
                    }

                    //if no pre-set value, show placeholder
                    if (selected == '') {
                        $(element).val('');
                    }

                    callback(selected);
                }
            });

            //if only a single result, select it
            if (data.result.length <= 1) {
                //set select2 value for an object: select2('data', value_object, triggerChange);
                $timezone_input.select2('data', data.result[0], true);
            }
        }

        return {
            getTimezones: getTimezones,
            modifyZipcodeDisplay: modifyZipcodeDisplay,
            renderSubdivision: renderSubdivision,
            renderTimezoneList: renderTimezoneList
        };
    }())
});


