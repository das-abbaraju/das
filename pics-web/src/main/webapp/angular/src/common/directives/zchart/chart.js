angular.module('PICS.directives')

.directive('chart', function (AnimatedArcChart) {
    return {
        restrict: 'E',
        scope: {
            data: '=',
            width: '@',
            height: '@',
            colors: "=",
        },
        link: function (scope, element, attrs) {
            scope.$watch('data', function(newValue) {
                if (newValue) {
                    var chartData = extractChartData(newValue),
                        chart = new AnimatedArcChart(chartData);

                    chart.draw(element[0], scope.width, scope.height, scope.colors);
                }
            });

            function extractChartData(data) {
                return [data.completed + data.pending, data.expiring, data.expired];
            }
        }
    };
});