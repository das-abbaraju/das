angular.module('PICS.services')

.factory('registrationRequestService', function ($resource) {
    return $resource('/registration-requests/:registrationKey.action');
});