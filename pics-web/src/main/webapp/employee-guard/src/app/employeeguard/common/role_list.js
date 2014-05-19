angular.module('PICS.employeeguard')

.factory('RoleList', function($resource) {
    return $resource('/angular/json/operator/role_list.json');
});