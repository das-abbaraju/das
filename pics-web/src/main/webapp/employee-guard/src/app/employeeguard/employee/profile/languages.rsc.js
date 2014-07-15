angular.module('PICS.employeeguard')

.factory('Language', function($resource) {
    return $resource('/employee-guard/api/languages');
});