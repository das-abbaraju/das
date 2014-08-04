angular.module('PICS.employeeguard.skills')

/* MISSING JSON INFORMATION
    contractor: "All Employees" group
    contractor, operator, corp: skill type
    operator, corp:  isRequired
*/

.controller('skillListCtrl', function ($scope, $filter, SkillListResource, WhoAmI) {
    WhoAmI.get(function(user) {
        $scope.userType = user.type.toLowerCase();

        if (user.type.toLowerCase() === 'corporate') {
            $scope.orderByField = 'skill';
        }
    });

    SkillListResource.query(function (skills) {
        var requiredSkills;

        $scope.skills = skills;

        requiredSkillsList = $filter('filter')(skills, { isRequiredSkill: true });

        if (requiredSkillsList) {
            $scope.requiredSkillsList = requiredSkillsList;
            prefillSelectedSkills(requiredSkillsList);
        }
    });

    function prefillSelectedSkills(requiredSkillsList) {
        var selected_skills = [];

        angular.forEach(requiredSkillsList, function(skill, i) {
            selected_skills.push(skill.id);
        });
        $scope.selected_skills = selected_skills;
    }

    function formatRequestPayload(selected_skills) {
        var required_skills = [];

        if (selected_skills) {
            angular.forEach($scope.skills, function(skill, i) {
                angular.forEach(selected_skills, function(selected, i) {
                    selected = parseInt(selected);
                    if (skill.id === selected) {
                        required_skills.push({
                            id: skill.id,
                            name: skill.name
                        });
                    }
                });
            });
        }

        return required_skills;
    }

    $scope.updateRequiredSkills = function(selected_skills) {
        var requiredSkills = formatRequestPayload(selected_skills);
        SkillListResource.update(requiredSkills);
        $scope.toggleFormDisplay();
        $scope.requiredSkillsList = requiredSkills;
    };

    $scope.toggleFormDisplay = function() {
        $scope.showEditForm = !$scope.showEditForm;
    };

    angular.extend($scope, {
        prefillSelectedSkills: prefillSelectedSkills
    });
});