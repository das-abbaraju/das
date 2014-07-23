angular.module('PICS.employeeguard.skills')

.factory('SkillListResource', function($resource) {
    var skillListResource = $resource('/employee-guard/api/skills', {}, {
        update: { method: 'PUT'},
        query: { method: 'GET', isArray:true }
    });

    return skillListResource;
});