angular.module('PICS.employeeguard')

.controller('operatorAssignmentsCtrl', function ($scope, SiteAssignments, ProjectAssignments) {
    $scope.site_assignments = SiteAssignments.get();
    $scope.project_assignments = ProjectAssignments.query();
});