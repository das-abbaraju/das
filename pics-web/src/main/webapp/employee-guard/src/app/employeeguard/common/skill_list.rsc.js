angular.module('PICS.employeeguard')

.factory('OperatorSkillList', function($resource) {
    return $resource('/employee-guard/api/skills');
});