angular.module('PICS.charts', [])

.factory('TypeToChart', function(AnimatedArcChart) {
    return {
        arc: AnimatedArcChart
    };
});