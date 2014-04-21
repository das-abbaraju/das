angular.module('PICS.employeeguard')

.factory('SiteList', function($resource, $routeParams) {
    // return $resource('/employee-guard/corporates/sites');
    return $resource('/angular/json/operator/corporate_sites.json');
});