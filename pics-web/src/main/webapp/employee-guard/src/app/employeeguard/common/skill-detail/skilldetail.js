angular.module('PICS.employeeguard')

.controller('skillDetailCtrl', function ($scope, $routeParams, SkillDetail, EmployeeService) {
    $scope.employee = EmployeeService.getEmployee($routeParams.id);
    $scope.skill = SkillDetail.get({id: $routeParams.id, skillId: $routeParams.skillId});
});
