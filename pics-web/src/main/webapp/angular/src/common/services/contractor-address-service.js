angular.module('PICS.services')

.factory('addressService', function ($resource) {
    var picsVerified = false,
        userRejectedCount = 0,
        resource = $resource('/address');

    resource.isVerified = function () {
        return picsVerified || userRejectedCount == 2;
    };

    resource.incrementUserRejectedCount = function (value) {
        userRejectedCount++;
    };

    resource.getUserRejectedCount = function () {
        return userRejectedCount;
    };

    return resource;
});
