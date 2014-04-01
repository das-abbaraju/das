angular.module('PICS.employeeguard')

.factory('SiteAssignmentDetails', function($resource, $routeParams) {
    return $resource('/employee-guard/corporates/sites/:id');
});