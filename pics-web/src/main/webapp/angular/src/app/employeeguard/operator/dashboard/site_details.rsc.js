angular.module('PICS.employeeguard')

.factory('SiteDetails', function($resource, $routeParams) {
    return $resource('/employee-guard/corporates/sites/:id');
});