angular.module('PICS.employeeguard')

.factory('RoleListResource', function($resource) {
    return $resource('/employee-guard/json/roles/role_list.json');
});