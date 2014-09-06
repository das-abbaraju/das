describe('Company finder controller', function () {
    var $compile, $rootScope, $controller, $interpolate, $httpBackend, $scope = {};

    beforeEach(angular.mock.module('PICSApp'));

    beforeEach(inject(function (_$compile_, _$rootScope_, _$controller_, _$interpolate_, _$httpBackend_) {
            $compile = _$compile_;
            $rootScope = _$rootScope_;
            $controller = _$controller_;
            $interpolate = _$interpolate_;
            $httpBackend = _$httpBackend_;
            $scope = $rootScope.$new();
    }));

    beforeEach(function () {
         $controller("companyFinderCtrl", {
            $scope: $scope
        });
    });

    it('should return the correct marker class based on the preflag', function () {
        expect($scope.getMarkerClass('Red')).toEqual('red-marker');
        expect($scope.getMarkerClass('Amber')).toEqual('yellow-marker');
        expect($scope.getMarkerClass('Green')).toEqual('green-marker');
        expect($scope.getMarkerClass('gobbldigook')).toEqual('gray-marker');
    });
});