angular.module('PICS.employeeguard')

.factory('Model', function () {
    var Model = function (data) {
        this.data = data;
    };

    Model.prototype.getData = function () {
        return this.data;
    };

    Model.prototype.getAllProjectSkills = function () {
        var projectSkills = [],
            data = this.getData(),
            projects = data.projects;

        //loop over projects
        for (var i in projects) {
            //loop over project skills
            for (var j in projects[i].skills) {
                projectSkills.push(projects[i].skills[j]);
            }
        }

        return projectSkills;
    };

    Model.prototype.getAllProjectRoles = function () {
        var projectRoles = [],
        data = this.getData(),
        projects = data.projects;

        for (var x in projects) {
            for (var y in projects[x].roles) {
                projectRoles.push(projects[x].roles[y]);
            }
        }

        return projectRoles;
    };

    return Model;
});