angular.module('PICS.employeeguard')

.controller('serverInfoCtrl', function ($scope, ServerInfo) {
    $scope.server = ServerInfo.get();
});