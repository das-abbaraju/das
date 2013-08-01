PICS.define('timezone.Timezone', {
    methods: {
        getTimezones: function (country, callback) {
            var that = this;

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
                        that.renderTimezoneList(data);
                    }
                }
            });
        },

        getTimezoneFormat: function (data) {
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
        },

        renderTimezoneList: function (data, callback) {
            var that = this,
                $timezone_input = $('input.timezone_input');

            $timezone_input.select2({
                data: data.result,
                minimumResultsForSearch: -1,
                width: 'element',
                escapeMarkup:function (markup) {
                    return markup;
                },
                formatResult: that.getTimezoneFormat,
                formatSelection: that.getTimezoneFormat,
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
    }
});