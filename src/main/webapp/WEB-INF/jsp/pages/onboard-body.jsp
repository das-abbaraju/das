<div id="onboarding-center-container">
    <div id="onboard-header">

    </div>
</div>
<div id="onboard-form-1" class="onboard-form-div">
    <form id="step1">
        <div class="steps">
            STEP 1 OF 3
        </div>
        <div class="heading">
            Add a Loved One
        </div>
        <div class="form-fields">
            <div class="row">
                <div class="label floatLeft">
                    First Name
                </div>
                <div class="field floatLeft">
                   <input type="text" name="fn" id="fn" value ="" class="input-field">
                </div>
            </div>
            <div class="row">
                <div class="label floatLeft">
                      Last Name
                </div>
                <div class="field floatLeft">
                     <input type="text" name="fn" id="ln" value =""  class="input-field">
                </div>
            </div>
            <div class="row">
                <div class="label floatLeft">
                      Contact
                </div>
                <div class="field floatLeft">
                    <select id="select-contact-method">
                        <option value="">Select</option>
                         <option value="email">Email</option>
                         <option value="phone">Phone</option>
                         <option value="address">Address</option>
                    </select>
                    <div class="sublabel displayNone">

                    </div>
                    <div class="subfield displayNone">
                         <input type="text" name="email" id="email" value ="">
                         <input type="text" name="phone" id="phone" value ="">
                         <input type="text" name="address" id="address1" value ="">
                    </div>

                </div>
            </div>
            <div class="row">
                <div class="label floatLeft rl">
                    Relationship
                </div>
                <div class="field floatLeft">
                       <select id="select-rln">
                        <option value="">Select</option>
                         <option value="email">Dad</option>
                         <option value="phone">Mother</option>
                         <option value="address">Sister</option>
                         <option value="address">Brother</option>
                         <option value="address">Cousin</option>
                         <option value="address">Uncle</option>
                         <option value="address">Aunty</option>
                         <option value="address">Husband</option>
                         <option value="address">Wife</option>
                         <option value="address">Fiance</option>
                         <option value="address">Son</option>
                         <option value="address">Daugther</option>
                         <option value="address">Son-in-Law</option>
                         <option value="address">Daughter-in-Law</option>
                         <option value="address">Boyfriend</option>
                         <option value="address">Girlfriend</option>
                         <option value="address">Best Friend</option>
                         <option value="address">Friend</option>
                         <option value="address">Co-worker</option>

                    </select>
                </div>
            </div>
            <div class="row">
                <div class="label floatLeft minor">

                </div>
                <div class="field floatLeft minortext">
                      Is this a minor?
                      <input type="radio" name="minor" id="minor-yes" value ="1">Yes</input>
                      <input type="radio" name="minor" id="minor-no" value ="0">No</input>

                </div>
            </div>
            <div class="row button-row">
                  <button class="add-button" value="Add" name="add-button" id="add-button"></button>
            </div>
        </div>
    </form>

</div>

<div id="onboard-form-2" class="onboard-form-div">
        <div class="steps">
            STEP 2 OF 3
        </div>
        <div class="heading">
            Add a Photo
        </div>
        <div id="photo-box">
            <!-- The global progress bar -->
            <div id="progress" class="progress">
                <div class="progress-bar progress-bar-success"></div>
            </div>
            <div id="files" class="files"></div>
            <div class="photo-label">
                <input type="file" id="fileupload" name="files[]" style="visibility: hidden; width: 1px; height: 1px"/>
                <a href="javascript://" id="choose-photo">Choose a Photo</a>
            </div>
        </div>
        <div id="photo-button-div">
            <ul>
                <li>
                    <a href="javascript://">
                        <img src="static/global/images/buttons/cancel.png">
                    </a>
                </li>
                <li>
                    <a href="javascript://">
                        <img src="static/global/images/buttons/takephoto.png">
                    </a>
                </li>
                <li>
                    <a href="javascript://">
                        <img src="static/global/images/buttons/save.png">
                    </a>
                </li>
            </ul>
        </div>
</div>


