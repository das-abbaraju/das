
var app = angular.module('peopleApp', ['util']);


app.controller('AlertCtrl',function ($scope, $http, $rootScope, $timeout){

    $rootScope.$watch("modalAlert", function(newValue, oldValue){
        if(newValue) {
            $("#alert-modal").modal('show');
        }else{
            $("#alert-modal").modal('hide');
        }});


    $timeout(function() { $rootScope.modalAlert = "Uh-oh!" }, 1000);

   $scope.closeModal = function(){
       $("#alert-modal").modal('hide');
   }

});

app.controller('PeopleSearchCtrl', function ($scope, $http){

    function showWarning(msg) {
        $scope.warning = msg;
    }

    $scope.hideWarning = function() {
        $scope.warning = null;
    }

    $scope.searchPrefix = "";
    $scope.people       = [];

    $scope.search = function() {
        $scope.hideWarning();

        var prefix = $scope.searchPrefix.trim();
        var prefixLength = prefix.length;
        if (prefixLength == 0) {
            showWarning("Please enter a search term!.");
        }

        else if (prefixLength < 2) {
            showWarning("Please enter at least two characters.");
        }

        else {
            var url = "/people/" + prefix;

            $http.get(url).success(function(data) {
                for (var i = 0; i < data.people.length; i++) {
                    var p = data.people[i];
                    p.name = p.first + " " + p.last;
                }
                $scope.people = data.people;
            });
        }
    }

    $scope.reverse      = false;
    $scope.toggleSortOrder = function(column) {
        if (column === $scope.predicate) {
            // User clicked on the same column that's already being used to sort.
            // Reverse the sort.
            $scope.reverse = !$scope.reverse;
        }
        else {
            // User clicked on a different column. Sort in ascending order.
            $scope.predicate = column;
            $scope.reverse = false;
        }
    }

    $scope.checkSearch = function() {
        $scope.search();
        if($scope.searchPrefix.trim() == ""){
            $scope.people = null;
        }
    }

    $scope.$watch("searchPrefix", $scope.search);

});
