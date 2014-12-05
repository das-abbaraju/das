var app = angular.module('demo3', []);

app.controller('Demo3Ctrl', function($scope) {

  $scope.people = [{name: "Julia", city: "Beverly Hills"},
                   {name: "Ravi", city: "Bangalore"},
                   {name: "Beverly", city: "London"},
                   {name: "Roberto", city: "Madrid"}];
});

