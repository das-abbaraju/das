angular.module('PICS.employeeguard')

.factory('EmployeeSkillList', function($resource, $routeParams) {
    return $resource('/employee-guard/employee/profile/skills');
});