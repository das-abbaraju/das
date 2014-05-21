angular.module('PICS.services')

.factory('companyFinderService', function ($resource) {
    return $resource('/contractors/locations.action');
});