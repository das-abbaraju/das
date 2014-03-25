angular.module('PICS.employeeguard')

.controller('employeeDashboardCtrl', function ($scope, EmployeeDashboard, EmployeeAssignment, SkillStatusConverter) {
    $scope.employee = EmployeeDashboard.get();
    $scope.assignment = EmployeeAssignment.get();
    $scope.getSkillClass = SkillStatusConverter.convert;

    $scope.setSlug = function() {
        if (!$scope.employee.slug) {
            $scope.employee.slug = $scope.employee.email;
        }
    };

    $scope.employee.$promise.then($scope.setSlug);
});