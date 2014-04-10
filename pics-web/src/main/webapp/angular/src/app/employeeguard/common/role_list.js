angular.module('PICS.employeeguard')

.factory('RoleList', function($resource) {
    // return $resource('/employee-guard/operators/roles/list');
    return $resource('/angular/json/operator/role_list.json');
});