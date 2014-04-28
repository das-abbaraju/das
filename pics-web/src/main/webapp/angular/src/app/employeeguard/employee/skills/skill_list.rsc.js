angular.module('PICS.employeeguard')

.factory('EmployeeSkillList', function($resource, $routeParams) {
    return $resource('/angular/json/employee/skills/skill_list.json');
});