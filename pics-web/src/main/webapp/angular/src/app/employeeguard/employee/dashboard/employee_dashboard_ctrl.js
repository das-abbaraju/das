angular.module('PICS.employeeguard')

.controller('employeeDashboardCtrl', function ($scope, EmployeeInfo, EmployeeAssignment, SkillStatus) {
    $scope.employee = EmployeeInfo.get();
    $scope.assignments = EmployeeAssignment.query();
    $scope.getSkillClass = SkillStatus.getClassNameFromStatus;

    $scope.setSlug = function() {
        if (!$scope.employee.slug) {
            $scope.employee.slug = $scope.employee.email;
        }
    };

    $scope.employee.$promise.then($scope.setSlug);
});