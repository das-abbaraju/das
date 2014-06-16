angular.module('PICS.registration')

.controller('addressConfirmation', function ($scope, $location, addressService) {
    addressService.get(function (data) {
        $scope.output = data.output;
        $scope.addressLines = data.output.formattedAddress.split('\n');
    });

    $scope.addressUserRejectionCount = addressService.getUserRejectedCount();

    $scope.onEditClick = function () {
        addressService.incrementUserRejectedCount();

        if (addressService.getUserRejectedCount() == 1) {
            $location.path('/Registration!basicAddressEditor.action');
        } else {
            $location.path('/Registration!advancedAddressEditor.action');
        }
    };
});