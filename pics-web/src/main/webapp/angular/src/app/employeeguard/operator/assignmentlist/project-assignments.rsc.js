angular.module('PICS.employeeguard')

.factory('ProjectAssignments', function($resource, $routeParams) {
    // return $resource('/employee-guard/corporates/sites');
    return $resource('/angular/json/operator/assignmentlist/project_assignments:id.json');
});