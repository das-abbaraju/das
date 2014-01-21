<form action="#" method="post" class="form-horizontal js-validation" role="form">
    <fieldset>
        <div class="form-group has-error">
            <label name="someName1" class="col-md-3 control-label"><strong>Some Required Label</strong></label>
            <div class="col-md-4">
                <input name="someName1" class="form-control" type="text" tabindex="1" value="Some Default Value"/>
            </div>
            <div class="toolip-container col-md-1 col-xs-1">
               <i class="icon-info-sign icon-large" data-toggle="tooltip" data-placement="top" title="" data-original-title="This is a sentence that explains the purpose of the form field." data-container="body"></i>
            </div>
        </div>

        <div class="form-group">
            <label name="someName2" class="col-md-3 control-label">Some Label</label>
            <div class="col-md-4">
                <textarea name="someName2" class="form-control" tabindex="2">Some Default Value</textarea>
            </div>
            <div class="toolip-container col-md-1 col-xs-1">
               <i class="icon-info-sign icon-large" data-toggle="tooltip" data-placement="right" title="" data-original-title="This is a sentence that explains the purpose of the form field." data-container="body"></i>
            </div>
        </div>

        <div class="form-group">
            <label name="someName3" class="col-md-3 control-label"><strong>Some Required Label</strong></label>
            <div class="col-md-4 col-xs-11">
                <select name="someName3" class="form-control select2Min" tabindex="3">
                    <option value="someValue" selected="selected">Some Default Option</option>
                    <option value="someValue2">Some Other Option</option>
                </select>
            </div>
            <div class="toolip-container col-md-1 col-xs-1">
               <i class="icon-info-sign icon-large" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="This is a sentence that explains the purpose of the form field." data-container="body"></i>
            </div>
        </div>

        <div class="form-group">
            <label name="groups" class="col-md-3 control-label">Some Label</label>
            <div class="col-md-4">
                <select name="someName4" class="form-control select2" multiple tabindex="4">
                    <option value="someValue1" selected="selected">Some Default Option</option>
                    <option value="someValue2">Some Other Option</option>
                    <option value="someValue3">Some Other Option</option>
                </select>
            </div>
            <div class="toolip-container col-md-1 col-xs-1">
               <i class="icon-info-sign icon-large" data-toggle="tooltip" data-placement="left" title="" data-original-title="This is a sentence that explains the purpose of the form field." data-container="body"></i>
            </div>
        </div>

        <div class="form-group">
            <div class="col-md-9 col-md-offset-3 form-actions">
                <button name="someName6" type="submit" class="btn btn-success" tabindex="5" >Some Action</button>
                <a href="#" class="btn btn-default" tabindex="6">Some Action</a>
            </div>
        </div>
    </fieldset>
</form>