angular.module('PICS.employeeguard')

.factory('SkillList', function($resource, $routeParams) {
    // return $resource('/employee-guard/operators/skills/employees/' + $routeParams.id);
    return $resource('/angular/json/operator/employee_skills/skill_list.json');
});