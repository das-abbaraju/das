angular.module('PICS.employeeguard')

.controller('betaFeedbackCtrl', function ($scope, Feedback, $httpBackend) {
    var success = {
        "status": "request for feedback received"
    };

    $httpBackend.when('POST', '/employee-guard/beta/feedback').respond(function(method, url, data, headers) {
        return [200, success];
    });

    $scope.submitFeedback = function () {
        Feedback.save({
            feedbackComment: $scope.feedback
        });

        $scope.feedbackSent = true;
    };
})

.directive('betafeedback', function () {
    return {
        restrict: 'E',
        templateUrl: '/angular/src/app/employeeguard/common/betafeedback/_beta-feedback.tpl.html'
    };
});
