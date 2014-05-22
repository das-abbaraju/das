angular.module('PICS.employeeguard')

.factory('ProjectAssignments', function($resource, $routeParams) {
    return $resource('/employee-guard/operators/assignments/projects/:id');
});