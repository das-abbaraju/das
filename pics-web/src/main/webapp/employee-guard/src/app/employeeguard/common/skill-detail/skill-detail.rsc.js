angular.module('PICS.employeeguard')

.factory('SkillDetail', function($resource) {
    return $resource('/employee-guard/json/eg-common/skill-detail/skill-detail.json');
});