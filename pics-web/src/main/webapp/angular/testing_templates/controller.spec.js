describe('Menu Filter Controller', function() {
    beforeEach(angular.mock.module('PICS.employeeguard'));

    var scope;

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();
        $controller("controllerName", {
            $scope: scope
        });
    }));

    it('should initialize with "All" selected by defaut', function() {
        expect(scope.selected).toEqual('All');
    });
});