angular.module('PICS.employeeguard')

.factory('Employee', function () {
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

    Model.prototype.getRoleByName = function (name) {
        var data = this.getData(),
            roles = data.roles;

        for (var i = 0; i < roles.length; i++) {
            if (roles[i].name == name) {
                return roles[i];
            }
        }
    };

    Model.prototype.getProjectByName = function (name) {
        var data = this.getData(),
            projects = data.projects;

        for (var i = 0; i < projects.length; i++) {
            if (projects[i].name == name) {
                return projects[i];
            }
        }
    };


    return Model;
});