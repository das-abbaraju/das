angular.module('PICS.roles')

.factory('RoleListResource', function($resource) {
    return $resource('/employee-guard/src/app/employeeguard/roles/json/role_list.json');
});