angular.module('PICS.employeeguard')

.controller('operatorDashboardCtrl', function ($scope, SiteDashboard) {
    var model;

    SiteDashboard.get().$promise.then(onSuccess, onError);

    function onSuccess(result) {
        $scope.site = result;
        $scope.loadAssignmentChart();
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

    $scope.redrawChart = function () {
        d3.select('.assignment-chart').html('');
        $scope.loadAssignmentChart();
    };

    $scope.loadAssignmentChart = function () {
        var height = 200,
            width = 420,
            arcStartAngle = -0.5 * Math.PI,
            arcEndAngle = 0.5 * Math.PI;

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

        var createArc = d3.svg.arc()
            .outerRadius(height)
            .innerRadius(height - 80);

        var pie = d3.layout.pie()
            .sort(null)
            .startAngle(arcStartAngle)
            .endAngle(arcEndAngle)
            .value(function(d) { return d.amount; });


        var chart = d3.select('.assignment-chart')
                        .append('svg')
                        .attr('height', height)
                        .attr('width', width)
                        .append('g')
                        .attr('transform', 'translate(' + width/2 + ',' + height + ')');

        chart.selectAll('.arc')
            .data(pie(data))
            .enter()
            .append('g')
            .append('path')
            .attr('fill', function(d, i) { return d.data.color; })
            .transition()
            .duration(1500)
            .attrTween('d', animatePie);

        function animatePie(d) {
            var i = d3.interpolate({startAngle: arcStartAngle, endAngle: arcStartAngle}, d);
            return function(time) { return createArc(i(time)); };
        }
    };
});