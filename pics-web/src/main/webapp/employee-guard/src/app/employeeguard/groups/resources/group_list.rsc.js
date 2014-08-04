angular.module('PICS.groups')

.factory('GroupListResource', function($resource) {
    return $resource('/employee-guard/src/app/employeeguard/groups/json/group_list.json');
});