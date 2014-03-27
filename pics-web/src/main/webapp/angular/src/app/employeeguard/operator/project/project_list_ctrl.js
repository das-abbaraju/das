angular.module('PICS.employeeguard')

.controller('operatorProjectListCtrl', function ($scope, ProjectList) {
    $scope.projects = ProjectList.query();
});