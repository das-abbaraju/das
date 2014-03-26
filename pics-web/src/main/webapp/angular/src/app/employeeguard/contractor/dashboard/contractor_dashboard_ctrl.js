angular.module('PICS.employeeguard')

.controller('contractorDashboardCtrl', function ($scope, ContractorStatus) {
    $scope.status = ContractorStatus.get();
});