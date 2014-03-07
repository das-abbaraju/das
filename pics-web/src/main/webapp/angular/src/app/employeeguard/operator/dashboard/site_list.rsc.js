angular.module('PICS.employeeguard')

.factory('SiteList', function($resource, $routeParams) {
    // return $resource('/employee-guard/operators/sites');
    return $resource('/angular/json/corp_dashboard_sites.json');
});