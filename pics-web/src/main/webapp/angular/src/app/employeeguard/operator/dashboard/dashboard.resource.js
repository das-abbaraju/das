angular.module('PICS.employeeguard')

.factory('SiteResource', function($resource, $routeParams) {
    return $resource('/employee-guard/operators/summary');
});