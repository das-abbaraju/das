angular.module('PICS.employeeguard')

.controller('operatorProjectListCtrl', function ($scope, ProjectList, WhoAmI) {
    $scope.projects = ProjectList.query();

    $scope.setUserType = function () {
        $scope.user = WhoAmI.get(function(user){
            if (user.type === 'CORPORATE') {
                $scope.orderByField = 'site';
            }
        });
    };

    $scope.setUserType();
});