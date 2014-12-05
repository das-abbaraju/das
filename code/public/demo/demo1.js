var app = angular.module('myApp', []);

app.controller('MyCtrl', function($scope) {
  // Only variables inside the $scope object are visible to the view.
  $scope.text1 = "";
});

