describe('Menu Filter Controller', function() {
    beforeEach(angular.mock.module('PICS.employeeguard'));

    var scope;

    beforeEach(inject(function($rootScope, $controller, FactoryName) {
        scope = $rootScope.$new();
        $controller("controllerName", {
            $scope: scope
        });

        //mock a factory
        mockFactory = FactoryName;

        //mock factory call
        mockFactory.save = jasmine.createSpy();
    }));

    it('should initialize with "All" selected by defaut', function() {
        expect(scope.selected).toEqual('All');
    });

    it('should call submitTestResults ', function() {
        spyOn(scope, 'submitTestResults');

        scope.loadNextQuestion();
        expect(scope.submitTestResults).toHaveBeenCalled();
    });
});