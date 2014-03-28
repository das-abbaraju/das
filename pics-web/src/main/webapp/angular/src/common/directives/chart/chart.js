angular.module('PICS.directives')

//overrides to drawConfig be added to the template or the controller file.  If not defined, drawConfig uses default values
.directive('chart', function (TypeToChart) {
    return {
        restrict: 'E',
        scope: {
            data: '=',
            type: '@',
            drawConfig: '='
        },
        link: function (scope, element, attrs) {
            scope.$watch('data', function(newValue) {
                if (newValue) {
                    var chart, chartConfig,
                        chartData = newValue,
                        Chart = TypeToChart[scope.type],
                        drawConfig = scope.drawConfig || {};

                    chart = new Chart(chartData);

                    drawConfig.element = element[0];

                    chart.draw(drawConfig);
                }
            });
        }
    };
});