angular.module('PICS.employeeguard')

.factory('EmployeeSkills', function($resource, $routeParams) {
    return $resource('/employee-guard/operators/skills/employees/' + $routeParams.id);
});