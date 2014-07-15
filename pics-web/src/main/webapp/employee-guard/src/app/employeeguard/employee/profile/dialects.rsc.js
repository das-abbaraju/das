angular.module('PICS.employeeguard')

.factory('Dialect', function($resource) {
    return $resource('/employee-guard/json/employee/settings/dialects_:id.json');
});