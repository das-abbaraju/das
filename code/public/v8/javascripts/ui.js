
var app = angular.module('peopleApp', ['util', 'ngQuickDate', 'ngRoute', 'ngCookies']);

app.controller('TopCtrl', function($scope) {

  $scope.editingPerson = null;

  $scope.editing = function() {
    return $scope.editingPerson != null;
  }

  $scope.startEdit = function(person) {
    $scope.editingPerson = person;
  }

  $scope.stopEdit = function() {
    $scope.editingPerson = null;
  }
});


app.controller('ShowCtrl', function ($scope, picsappHttp, $routeParams, $location) {

    $scope.editingPerson = null;

    $scope.editing = function() {
        return $scope.editingPerson != null;
    }

    $scope.startEdit = function(person) {
        $scope.editingPerson = person;
    }

    $scope.stopEdit = function() {
        $scope.editingPerson = null;
    }

    picsappHttp.get("/person/" + $routeParams.id, function(data) {
        $scope.editingPerson = data.person;
    });

    $scope.cancelEdit = function() {
        returnToMainPage();
    }

    function returnToMainPage() {
        $location.path("/");
    }
});

app.config(function($routeProvider) {
    $routeProvider.
        when('/search', {
            templateUrl: 'search.html',
            controller:  'PeopleSearchCtrl'
        }).
        when('/show/:id', {
            templateUrl: 'show.html',
            controller:  'ShowCtrl'
        }).
        when('/edit/:id', {
            templateUrl: 'edit.html',
            controller:  'EditCtrl'
        }).
        otherwise({
            redirectTo: '/search'
        });
});

app.controller('PeopleSearchCtrl', function ($scope, picsappHttp, $cookies, data) {
  $scope.searchPrefix = "";
  $scope.people       = [];
  $scope.predicate    = 'last';
  $scope.reverse      = false;

  $scope.checkSearch = function() {
    var value = $scope.searchPrefix ? $scope.searchPrefix : ""
    $cookies.lastSearch = $scope.searchPrefix;
    if (value.length < 2) {
      $scope.people = [];
    }
    else {
      search(value);
    }
  }

  $scope.toggleSortOrder = function(column) {
    if (column == $scope.predicate) {
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

  $scope.hideWarning = function() {
    $scope.warning = null;
  }

  search = function(prefix, data) {
      var url = "/person/" ;

      for (var i = 0; i < data.people.length; i++) {
          var p = data.people[i];
          p.name = p.first + " " + p.last;
          p.birthDate = new Date(p.birthDate);
          p.salary = 0;
      }
      $scope.people = data.people;
  }




    if($cookies.lastSearch){
        $scope.searchPrefix=$cookies.lastSearch ;
        $scope.checkSearch();
    }
});

app.controller('EditCtrl', function ($scope, picsappHttp, $routeParams, $location) {
    $scope.genders = { 'F': 'female', 'M': 'male' }

    $scope.editingPerson = null;

    // We're relying on the fact that AngularJS creates a new instance of
    // this controller every time an edit view is activated via the router.
    picsappHttp.get("/person/" + $routeParams.id, function(data) {
        $scope.editingPerson = data.person;
    });

    function returnToMainPage() {
        $location.path("/");
    }

    $scope.cancelEdit = function() {
        returnToMainPage();
    }

    $scope.save = function() {
        var url = "/person/" + $scope.editingPerson.id;

        picsappHttp.post(url, $scope.editingPerson, function(data) {
            returnToMainPage();
        });
    }
});
app.controller('AlertCtrl', function ($scope, $rootScope, $timeout) {
  $rootScope.modalAlert = null;

  $rootScope.$watch("modalAlert", function(newValue) {
    if (newValue != null)
      $("#alert-modal").modal('show');
    else
      $("#alert-modal").modal('hide');
  });

  $scope.closeModal = function() {
    $rootScope.modalAlert = null;
  }
});




var INTEGER_REGEXP = /^\-?\d+$/;
app.directive('integer', function() {
    return {
        require: 'ngModel',
        link: function(scope, elm, attrs, ctrl) {
            ctrl.$parsers.unshift(function(viewValue) {
                if (INTEGER_REGEXP.test(viewValue)) {
                    // it is valid
                    ctrl.$setValidity('integer', true);
                    return viewValue;
                } else {
                    // it is invalid, return undefined (no model update)
                    ctrl.$setValidity('integer', false);
                    return undefined;
                }
            });
        }
    };
});

var FLOAT_REGEXP = /^\-?\d+((\.|\,)\d+)?$/;
app.directive('smartFloat', function() {
    return {
        require: 'ngModel',
        link: function(scope, elm, attrs, ctrl) {
            ctrl.$parsers.unshift(function(viewValue) {
                if (FLOAT_REGEXP.test(viewValue)) {
                    ctrl.$setValidity('float', true);
                    return parseFloat(viewValue.replace(',', '.'));
                } else {
                    ctrl.$setValidity('float', false);
                    return undefined;
                }
            });
        }
    };
});

