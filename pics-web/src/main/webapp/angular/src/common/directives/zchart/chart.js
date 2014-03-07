angular.module('PICS.directives')

.directive('chart', function (AnimatedArcChart) {
    return {
        restrict: 'E',
        scope: {
            datasrc: '=',
            width: '@',
            height: '@',
            colors: "="
        },
        link: function (scope, element) {
            if (scope.datasrc) {
                scope.datasrc.then(onSuccess, onError);
            }

            scope.$on('handleBroadcast', function() {
                scope.datasrc.then(onSuccess, onError);
            });


            function onSuccess(data) {
                var chartData = [data.completed + data.pending, data.expiring, data.expired],
                    chart = new AnimatedArcChart(chartData);

                chart.draw(element[0], scope.width, scope.height, scope.colors);
            }

            function onError(error) {
                console.log(error);
            }

        }
    };
});