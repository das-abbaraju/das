
var app = angular.module('peopleApp', []);

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
                    var birthDateString = moment(p.birthDate).format("DD MMM, YYYY");
                    p.birthDate = birthDateString;
                    p.name = p.first + " " + p.last;
                }
                $scope.people = data.people;
            });
        }
    }
});
