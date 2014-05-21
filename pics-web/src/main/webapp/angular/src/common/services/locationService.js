angular.module('PICS.services')

.factory('locationService', function ($resource) {
    return $resource('/location.action');
});