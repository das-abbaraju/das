angular.module('PICS.employeeguard')

.factory('SiteDashboard', function($resource, $routeParams) {
    return $resource('/employee-guard/operators/summary');
});