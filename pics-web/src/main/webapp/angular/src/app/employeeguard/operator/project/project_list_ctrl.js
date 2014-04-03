angular.module('PICS.employeeguard')

.controller('operatorProjectListCtrl', function ($scope, ProjectList) {
    $scope.projects = ProjectList.query(function(projects) {
        if ($scope.hasSites(projects)) {
            $scope.operator_corp = true;
            $scope.orderByField = 'site';
        }
    });

    $scope.hasSites = function(projects) {
        return projects[0].siteName;
    };
});