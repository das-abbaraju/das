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
