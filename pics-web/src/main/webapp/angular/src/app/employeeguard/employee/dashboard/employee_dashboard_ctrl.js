angular.module('PICS.employeeguard')

.controller('employeeDashboardCtrl', function ($scope, EmployeeDashboard, EmployeeAssignment) {
    $scope.employee = EmployeeDashboard.get();
    $scope.assignment = EmployeeAssignment.get();
});