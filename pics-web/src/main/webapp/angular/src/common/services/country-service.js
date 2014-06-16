angular.module('PICS.services')

.factory('countryService', function ($resource) {
    return $resource('/countries.action');
});