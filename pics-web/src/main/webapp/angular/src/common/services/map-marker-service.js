angular.module('PICS.services')

.factory('mapMarkerService', function () {
    var factory = {},
        markers;

    factory.getMarkers  = function () {
        return markers;
    };

    factory.setMarkers = function (value) {
        markers = value;
    };

    return factory;
});