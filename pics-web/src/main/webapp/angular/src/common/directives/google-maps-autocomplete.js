angular.module('PICS.directives')

.directive('googleMapsAutocomplete', function () {
    return {
        restrict: 'A',
        scope: {
            googleMapsAutocomplete: '='
        },
        link: function (scope, element, attrs) {
            scope.googleMapsAutocomplete = new google.maps.places.Autocomplete(element[0], {});
        }
    };
});