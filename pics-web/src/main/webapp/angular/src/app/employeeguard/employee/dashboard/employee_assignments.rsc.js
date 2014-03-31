angular.module('PICS.employeeguard')

.factory('EmployeeAssignment', function($resource, $routeParams) {
    // return $resource('/angular/json/employee/assignments.json');
    return $resource('/employee-guard/employee/summary/assignments');
});