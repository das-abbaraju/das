angular.module('PICS.employeeguard')

.factory('SiteAssignments', function($resource, $routeParams) {
    return $resource('/angular/json/operator/assignmentlist/site_assignments:id.json');
});