angular.module('PICS.employeeguard')

.controller('changeLogCtrl', function ($scope, WhoAmI) {
    WhoAmI.get(function(user) {
        $scope.user = user.type.toLowerCase();
    });
});