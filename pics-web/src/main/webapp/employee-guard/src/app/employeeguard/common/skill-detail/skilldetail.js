angular.module('PICS.employeeguard')

.controller('skillDetailCtrl', function ($scope, $routeParams, WhoAmI, SkillDetail, EmployeeService, SkillStatus) {
    WhoAmI.get(function(user) {
        $scope.userType = user.type.toLowerCase();
    });

    $scope.employee = EmployeeService.getEmployee($routeParams.id);
    $scope.skill = SkillDetail.get({id: $routeParams.id, skillId: $routeParams.skillId});

    $scope.getSkillStatusClassName = function(status) {
        return SkillStatus.getClassNameFromStatus(status);
    };
});