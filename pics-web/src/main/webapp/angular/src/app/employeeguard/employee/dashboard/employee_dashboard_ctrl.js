angular.module('PICS.employeeguard')

.controller('employeeDashboardCtrl', function ($scope, EmployeeDashboard, EmployeeAssignment, SkillStatusConverter) {
    $scope.employee = EmployeeDashboard.get();
    $scope.assignment = EmployeeAssignment.get();
    $scope.skillConvert = SkillStatusConverter.convert;
    // console.log(SkillStatusConverter);
    // $scope.skillConvert = SkillStatusConverter.convert('Expired');
});