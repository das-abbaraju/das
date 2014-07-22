angular.module('PICS.employeeguard')

.factory('SkillDetail', function($resource) {
    return $resource('/employee-guard/skillreview/employee/:id/skill/:skillId/info');
});