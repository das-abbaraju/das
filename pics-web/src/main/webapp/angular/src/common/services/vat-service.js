angular.module('PICS.services')

.factory('vatService', function ($resource) {
    return $resource('/tax-id-info/:country/:language.action');
});