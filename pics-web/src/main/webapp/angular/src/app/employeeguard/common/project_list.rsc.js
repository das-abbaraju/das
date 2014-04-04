angular.module('PICS.employeeguard')

.factory('ProjectList', function($resource) {
    return $resource('/employee-guard/operators/projects/list');
});