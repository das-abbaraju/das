angular.module('PICS.employeeguard')

.factory('SiteDashboard', function($resource, $routeParams) {
    return $resource('/employee-guard/operators/summary');
    // return $resource('/angular/json/dashboard.json');
});