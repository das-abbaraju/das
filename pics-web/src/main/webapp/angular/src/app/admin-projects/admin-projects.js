
angular.module('admin-projects', [])

.controller('ProjectsEditCtrl', function ($scope, project) {
    $scope.project = project;

    $scope.removeTeamMember = function (teamMember) {
        var idx = $scope.project.teamMembers.indexOf(teamMember);

        if (idx >= 0) {
            $scope.project.teamMembers.splice(idx, 1);
        }
    };
});