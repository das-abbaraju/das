angular.module('PICS.employeeguard')

.controller('operatorDashboardCtrl', function ($scope, SiteDashboard) {
    var model;

    SiteDashboard.get().$promise.then(onSuccess, onError);

    function onSuccess(result) {
        $scope.site = result;
    }

    function onError(error) {
        console.log(error);
    }

    $scope.calculateStatusPercentage = function (amount, total) {
        return (amount / total) * 100;
    };


    $scope.getProjectStatus = function (project) {
        var progress_bar = {},
            total = project.completed + project.pending + project.expiring + project.expired;

        progress_bar.success = {
            amount: (project.pending + project.completed),
            width: $scope.calculateStatusPercentage((project.pending + project.completed), total)
        };

        progress_bar.warning = {
            amount: project.expiring,
            width: $scope.calculateStatusPercentage(project.expiring, total)
        };

        progress_bar.danger = {
            amount: project.expired,
            width: $scope.calculateStatusPercentage(project.expired, total)
        };

        return progress_bar;
    };
});