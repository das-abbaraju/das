angular.module('PICS.services')

.factory('timeZoneService', function ($resource) {
    return $resource('/time-zones/:country.action');
});