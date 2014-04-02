angular.module('PICS.employeeguard')

.factory('EmployeeInfo', function($resource, $routeParams) {
    return $resource('/employee-guard/employee/summary/employee-info');
});