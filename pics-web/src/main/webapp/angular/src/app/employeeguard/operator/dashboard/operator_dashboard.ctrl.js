angular.module('PICS.employeeguard')

.controller('operatorDashboardCtrl', function ($scope, SiteDashboard, AnimatedArcChart) {
    SiteDashboard.get().$promise.then(onSuccess, onError);

    function onSuccess(result) {
        $scope.site = result;
        $scope.formatChartData(result);
    }

    function onError(error) {
        console.log(error);
    }

    $scope.calculateStatusPercentage = function (amount, total) {
        return (amount / total) * 100;
    };

    $scope.getProjectStatus = function (project) {
        var progress_bar = {},
            total = project.complete + project.pending + project.expiring + project.expired;

        progress_bar.success = {
            amount: (project.pending + project.complete),
            width: $scope.calculateStatusPercentage((project.pending + project.complete), total)
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

    $scope.formatChartData = function (result) {
        var data = [{
                        amount: $scope.site.completed + $scope.site.pending,
                        color: '#33b679'
                    },{
                        amount: $scope.site.expiring,
                        color: '#f4b400'
                    },{
                        amount: $scope.site.expired,
                        color: '#e74c3c'
                    }];
        var chart = new AnimatedArcChart(data);

        chart.drawChart('.assignment-chart', 420, 200);
    };
});