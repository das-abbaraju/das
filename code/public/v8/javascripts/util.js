/* This 'util' module will hold our filters. */
var util = angular.module('util', [])

/* The 'gender' filter. */
util.filter('gender', function() {
    return function(input) {
        switch(input) {
            case "M": return "male";
            case "F": return "female";
            default:  return "unknown";
        }
    }
});


util.directive('messageBox', function() {
    return {
        restrict: 'E',
        scope: {
            ngModel: '=',
            type: '@',
            icon: '@'
        },
        transclude: true,
        templateUrl: 'templates/message-box.html',

        link: function (scope, element, attrs, $timeout) {
            scope.close = function () {
                scope.ngModel = null;
            }

        }
    }// The meat of the directive goes below.
});

util.factory('picsnotify', function($rootScope, $timeout) {
    $rootScope.warning = null;
    $rootScope.error   = null;

    return {
        showWarning: function(msg) {
            if (msg) {
                $rootScope.warning = msg;
                $timeout(function() { $rootScope.warning = null }, 3000);
            }
        },
        showError: function(msg) {
            if (msg) {
                $rootScope.error = msg;
                $timeout(function() { $rootScope.error = null }, 3000);
            }
        }
    }
});

util.factory('picsappHttp', function($http, picsnotify) {
    function handleError(errorData) {
        picsnotify.showError(errorData.error);
    }

    return {
        get: function(url, successCallback) {
            $http.get(url).success(successCallback).error(handleError);
        },
        post: function(url, data, successCallback) {
            $http.post(url, data).success(successCallback).error(handleError);
        }
    }
});
