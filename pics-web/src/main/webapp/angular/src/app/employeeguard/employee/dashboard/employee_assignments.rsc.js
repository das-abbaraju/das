angular.module('PICS.employeeguard')

.factory('EmployeeAssignment', function($resource, $routeParams) {
    // return $resource('/employee-guard/corporates/sites/:id');
    return $resource('/angular/json/employee/assignments.json');
});