angular.module('PICS.employeeguard')

.controller('menuFilterCtrl', function ($scope) {
    var selected = 'All';

    function select(item) {
        $scope.selected = item;
    }

    function isSelected(item) {
       return $scope.selected === item;
    }

    angular.extend($scope, {
		selected: selected,
		select: select,
		isSelected: isSelected
	});
});