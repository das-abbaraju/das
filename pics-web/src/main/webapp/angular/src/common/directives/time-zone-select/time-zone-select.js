angular.module('PICS.directives')

.directive('timeZoneSelect', function () {
    function getTimeZoneFormat(data) {
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

    return {
        restrict: 'A',
        link: function (scope, element) {
            scope.$watch('timeZones', function (newTimeZones) {
                element.select2({
                    data: newTimeZones,
                    minimumResultsForSearch: -1,
                    width: '100%',
                    escapeMarkup:function (markup) {
                        return markup;
                    },
                    formatResult: getTimeZoneFormat,
                    formatSelection: getTimeZoneFormat,
                    containerCssClass: 'timezone-select'
                });
            });
        }
    };
});