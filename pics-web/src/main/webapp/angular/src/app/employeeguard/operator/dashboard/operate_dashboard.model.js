angular.module('PICS.employeeguard')

.factory('Site', function () {
    var Model = function (data) {
        this.data = data;
    };

    Model.prototype.getData = function () {
        return this.data;
    };

    return Model;
});