describe('Menu Filter Controller', function() {
    beforeEach(angular.mock.module('PICS.employeeguard'));

    var scope;

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();
        $controller("menuFilterCtrl", {
            $scope: scope
        });
    }));

    it('should initialize with "All" selected by defaut', function() {
        expect(scope.selected).toEqual('All');
    });

    it('should set selected item', function() {
        scope.select('Green Eyes');

        expect(scope.selected).toEqual('Green Eyes');
    });

    describe('isSelected', function() {
        it('should be true when the current selection is the same as selected', function() {
            scope.select('Rosebud');

            expect(scope.isSelected('Rosebud')).toBeTruthy();
        });

        it('should be false when current selection is NOT the same as selected', function() {
            scope.select('Rosebud');

            expect(scope.isSelected('Rose')).toBeFalsy();
        });
    });
});