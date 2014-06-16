angular.module('PICS.services')

.factory('dialectService', function ($resource) {
    return $resource('/dialects/:id.action');
});