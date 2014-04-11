angular.module('PICS.employeeguard')

.controller('operatorProjectListCtrl', function ($scope, ProjectList, WhoAmI) {
    $scope.projects = ProjectList.query();
    $scope.user = WhoAmI.get();

    $scope.user.$promise.then(function(user) {
        if (user.type.toLowerCase() === 'corporate') {
            $scope.orderByField = 'site';
        }
    });
});