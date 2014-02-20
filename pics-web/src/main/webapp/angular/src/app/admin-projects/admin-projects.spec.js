describe('ProjectEditCtrl tests', function () {

    var $scope;

    beforeEach(module('admin-projects'));

    beforeEach(inject(function ($rootScope) {
        $scope = $rootScope.$new();
    }));

    it('should remove an existing team member',
        inject(function ($controller) {
            var teamMember = {};

            $controller('ProjectsEditCtrl', {
                $scope: $scope,
                project: {
                    teamMembers: [teamMember]
                }
            });

            // Verify the initial setup
            expect($scope.project.teamMembers).toEqual([teamMember]);

            // Execute and verify results
            $scope.removeTeamMember(teamMember);
            expect($scope.project.teamMembers).toEqual([]);
        }));
});
