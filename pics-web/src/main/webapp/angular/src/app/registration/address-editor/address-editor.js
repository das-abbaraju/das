angular.module('PICS.registration')

.controller('addressEditor', function ($scope, addressService, countryService) {
    addressService.get(function (data) {
        $scope.input = data.input;
        $scope.output = data.output;
        $scope.strikeIronAddressFound = data.strikeIronAddressFound;
        $scope.addressLines = data.output.formattedAddress.split('\n');
    });

    countryService.query(function (countryList) {
        $scope.countryList = countryList;
    });

    $scope.addressUserRejectionCount = addressService.getUserRejectedCount();
});