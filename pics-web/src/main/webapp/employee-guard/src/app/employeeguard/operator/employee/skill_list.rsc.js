angular.module('PICS.employeeguard')

.factory('SkillList', function($resource) {
    return $resource('/employee-guard/operators/:siteId/skills/employees/:id');
});