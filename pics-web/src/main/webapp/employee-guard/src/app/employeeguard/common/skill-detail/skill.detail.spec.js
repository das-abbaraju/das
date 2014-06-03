describe('Skill Details', function() {
    var scope;

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();
        $controller("skillDetailCtrl", {
            $scope: scope
        });
    }));

    it("should have an employee object", function() {
        expect(scope.employee).toBeDefined();
    });

    it("should have an skill object", function() {
        expect(scope.skill).toBeDefined();
    });
});