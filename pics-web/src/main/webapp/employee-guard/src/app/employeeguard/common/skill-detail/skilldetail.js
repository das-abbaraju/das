angular.module('PICS.employeeguard')

.controller('skillDetailCtrl', function ($scope, $routeParams, SkillDetail, EmployeeDetail) {
    $scope.skill = SkillDetail.get();
    $scope.employee = EmployeeDetail.get();

    // $scope.skill = SkillDetail.get({id: $routeParams.skillId});
    // $scope.employee = EmployeeDetail.get({id: $routeParams.id});
});
