angular.module('PICS.employeeguard')

.controller('menuFilterCtrl', function ($scope) {
    $scope.selected = 'All';

    $scope.select = function(item) {
        $scope.selected = item;
    };

    $scope.isSelected = function(item) {
       return $scope.selected === item;
    };
});