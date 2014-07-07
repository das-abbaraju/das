angular.module('PICS.services')

.factory('phoneNumberService', function ($resource) {
    return $resource('/sales-phone/:countryId.action');
});