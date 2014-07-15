angular.module('PICS.employeeguard')

.factory('Dialect', function($resource) {
    return $resource('/employee-guard/api/dialects/:id');
});