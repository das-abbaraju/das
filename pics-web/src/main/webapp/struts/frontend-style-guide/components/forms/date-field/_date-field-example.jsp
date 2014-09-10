<form action="#" method="post" class="form-horizontal js-validation" role="form">
<div class="form-group">
    <label class="col-md-3 control-label ng-scope" translate="PROJECT_CREATE.FORM.START_DATE">Start Date</label>
    <div class="col-md-5">
        <fieldset class="expiration-date">
            <div class="row date">
                <div class="col-xs-4">
                    <input class="form-control year ng-pristine ng-valid ng-valid-pattern" name="year" ng-change="updateDatePicker(year, month, day)" type="text" placeholder="YYYY" maxlength="4" ng-model="year" ng-pattern="onlyNumbers">
                </div>
                <div class="col-xs-3">
                    <input class="form-control month ng-pristine ng-valid ng-valid-pattern" name="month" ng-change="updateDatePicker(year, month, day)" type="text" placeholder="MM" maxlength="2" ng-model="month" ng-pattern="onlyNumbers">
                </div>
                <div class="col-xs-3">
                    <input class="form-control day ng-pristine ng-valid ng-valid-pattern" name="day" ng-change="updateDatePicker(year, month, day)" type="text" placeholder="DD" maxlength="2" ng-model="day" ng-pattern="onlyNumbers">
                </div>
                <div class="col-xs-1">
                    <button class="btn-link" onClick="return false;">
                        <i class="icon-calendar"></i>
                    </button>
                </div>

            </div>
        </fieldset>
    </div>
</form>