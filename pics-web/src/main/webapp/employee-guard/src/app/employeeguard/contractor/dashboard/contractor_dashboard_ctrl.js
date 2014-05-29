angular.module('PICS.employeeguard')

.controller('contractorDashboardCtrl', function ($scope, ContractorStatus) {
    $scope.status = ContractorStatus.get();

    $scope.status.$promise.then(function(data) {
        $scope.employees = $scope.getNumberofEmployees(data);

        $scope.chartData = [
            data.completed + data.pending,
            data.expiring,
            data.expired
        ];
    });

    $scope.getNumberofEmployees = function(status) {
        return status.completed + status.pending + status.expiring + status.expired;
    };
});