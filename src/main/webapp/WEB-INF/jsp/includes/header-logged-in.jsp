<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="userId" scope="session" value="${param.uid}"/>

<div id="header">
                    <div id="kk-logo" class="floatLeft">
                        <img src="static/global/images/kk-logo.png" alt="the KrystalArk Project">
                    </div>
                    <div id="add-plus-block" class="floatRight">
                         <a href="javascript://">
                             <div>+</div>
                         </a>
                    </div>
</div>
<div class="animated-area displayNone">
   <form method="POST" id="message-form" onsubmit="return false;" >
       <div class = "form-container">
           <div class="white-block">

                <div id="compose-message">

                        <input type="hidden" name="message-type" id="message-type" value="text">
                        <div id="head">
                            <h2>New Message: Select Message Type</h2>
                        </div>
                        <div id="select-message-type">
                            <div id="text" class="floatLeft text box on">
                                <div class="message-type-label">Written Message</div>
                            </div>
                            <div id="photo" class="floatLeft photo box">
                                <div class="message-type-label">Picture Message</div>
                            </div>
                            <div id="video"  class="floatLeft video box">
                                <div class="message-type-label">Video Message</div>
                            </div>
                        </div>
                        <div id="compose-text">
                            <div class="subject-textbox">
                                <input type="text" name="subject" id="subject" value="Enter a subject" onblur="changeInputVal(this,'Enter a subject')" onfocus="changeInputVal(this,'Enter a subject')">
                            </div>
                            <div class="body-textbox">
                              <textarea id="emailbody" name="emailbody"></textarea>
                            </div>
                        </div>
                        <div id="add-files-block">
                             <a href="javascript://" id="add-files">Add files <span>(photos,videos,audio)</span></a>
                        </div>

                </div>
           </div>
           <div class="next-button">
                 <input type="submit" name="email-submit" id="email-submit" value="Next"/>
           </div>
       </div>
    </form>

   <form id="add-beneficiary-form" action="POST" >
        <div class = "form-container">
           <div class="white-block">
                <div id="add-beneficiary-container">

                   <div id="add-beneficiary" >
                      <div class="head">
                            <h2>New Message: Add beneficiary</h2>
                      </div>
                      <div id="add-beneficiary-block">
                            <div class="label">
                                Select 1 or multiple Beneficiaries:
                            </div>
                            <div id="pre-added-bene">
                                 <ul>
                                     <li benefId="1">Tarun Tyagi</li>
                                     <li benefId="2">Das A</li>
                                     <li benefId="3">Srik</li>
                                 </ul>
                            </div>
                            <p id="add-ben-or">
                                OR
                            </p>
                            <div class="label">
                                Add a new Beneficiary:
                            </div>
                            <div class="subject-textbox">
                                <input type="text" name="bname" id="bname">
                            </div>
                      </div>
                      <div id="delivery-block">
                          <div class="label">
                                Deliver on:
                          </div>
                          <div id="deliver-options">
                              <ul>
                                  <li id="postmortem" class="send-options">Postmortem</li>
                                  <li id="postmortem-date" class="send-options">Postmortem, on date:</li>
                                  <li id="prior-date" class="send-options">Prior Postmortem, on date:</li>
                              </ul>
                              <div id="date-selection-block" class="displayNone">
                                    <div class="calendar-arrow">
                                    </div>
                                    <div class="row">
                                        <div class="label floatLeft">Repeat</div>
                                        <div class="floatLeft">
                                            <select id="recurrent" name="recurrent">
                                                 <option value = "once" selected="selected">Only Once</option>
                                                <option value = "weekly">Weekly</option>
                                                <option value = "monthly">Monthly</option>
                                                <option value = "annually">Annually</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="label floatLeft">ON</div>
                                        <div class="floatLeft">
                                            <ul class="day-picker">
                                                <li>S</li>
                                                <li class="day-selected">M</li>
                                                <li>T</li>
                                                <li>W</li>
                                                <li>T</li>
                                                <li>F</li>
                                                <li>S</li>

                                            </ul>
                                        </div>
                                    </div>

                                   <div class="row" id="start-date-row">
                                        <div class="label floatLeft">Start</div>
                                        <div class="floatLeft">
                                            <input type="text" name="start-date" id="start-date" value=""></input>
                                        </div>
                                    </div>
                                  <div class="row" id="end-date-row">
                                        <div class="label floatLeft">End</div>
                                        <div class="floatLeft">
                                            <select id="end-date-condition" name="end-date-condition">
                                                <option value="">None</option>
                                                <option value="for">For</option>
                                                <option value="by">By</option>
                                            </select>
                                            <br><br><input type="text" name="end-date" id="end-date" value="" class="displayNone"></input>
                                            <input type="text" name="end-occurance" id="end-occurance" value="1" class="displayNone"></input>
                                            <span class="end-occurrance-span displayNone"> times</span>
                                        </div>
                                    </div>
                               </div>
                          </div>
                      </div>
                      <div id="categorize-block">
                           <div class="label">
                                Categorize
                            </div>
                           <div class="select-box-div">
                               <select id="select-box-select">
                                   <option value="">Select Category</option>
                                   <option value="first">First</option>
                                   <option value="anniversary">Anniversary</option>
                               </select>
                           </div>

                      </div>
                      <div id="message-preview-block">
                              <div id="message-preview-orange">
                                  <p id="subject-preview">
                                    Re: {{data.userMessage.subject}}
                                    <input type="hidden" name="message-id" id="message-id" value="{{data.userMessage.id}}"></input>
                                    <input type="hidden" name="methodType" id="methodType" value=""></input>
                                  </p>
                                  <p id="body-preview">
                                        {{data.userMessage.messageBody}}
                                  </p>
                                  <p id="file-preview">

                                  </p>
                              </div>
                      </div>
                   </div>
           </div>
           </div>
           <div class="next-button">

                 <div class="floatRight">
                     <input type="submit" name="email-submit" id="beneficiary-submit" value="Next"/>
                 </div>
               <div class="floatRight" id="back-message-button">Back</div>
           </div>
       </div>
   </form>

</div>

<!-- modal loading window -->
<div id="data-loader-window" class="displayNone">
        <div class="loader"></div>
</div>
