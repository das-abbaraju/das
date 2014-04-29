angular.module('PICS.employeeguard')

.controller('betaFeedbackCtrl', function ($scope, Feedback) {
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
