angular.module('PICS.employeeguard')

.factory('SiteAssignments', function($resource, $routeParams) {
    return $resource('/employee-guard/operators/assignments/summary/:id');
});