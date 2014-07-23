angular.module('PICS.employeeguard')

.controller('operatorRoleListCtrl', function ($scope, RoleList, WhoAmI) {
    $scope.roles = RoleList.query();
    $scope.user = WhoAmI.get();
});