angular.module('helloworld')

.controller('helloworld', function ($scope, $resource) {
    $scope.message = 'hello world';
    $resource('http://localhost:8080/AngularResult.action').query(function (data) {
        $scope.data = data;
        alert($scope.data[0].name);
    });
});