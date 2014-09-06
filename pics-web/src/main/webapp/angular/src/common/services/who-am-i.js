angular.module('PICS.services')

.factory('whoAmI', function($resource) {
    return $resource('/whoAmI.action');
});