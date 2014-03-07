angular.module('PICS.employeeguard')

.factory('SiteList', function($resource, $routeParams) {
    return $resource('/employee-guard/corporates/sites');
});