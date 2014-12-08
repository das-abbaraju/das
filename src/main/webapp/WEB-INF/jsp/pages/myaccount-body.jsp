
           <div id="teaser-section">
               <div id="teaser-area">
                   <div id="thumb" class="floatLeft">
                       <div class="ann-circular">
                            <style>
                                .ann-circular {
                                        width: 84px;
                                        height: 84px;
                                        border-radius: 84px;
                                        -webkit-border-radius: 84px;
                                        -moz-border-radius: 84px;
                                         background: url("/site/static/my-account/images/temp-pics/ann-g.png") no-repeat center center;
                                        }
                            </style>
                        </div>
                   </div>
                   <div id="teaser" class="floatLeft">
                         <h1>Tell Luane what to expect on her first kiss</h1>
                         <div id="schedule-message-container">
                              <button type="button" name="schedule-message" id="schedule-message" value=""/>
                         </div>
                   </div>
               </div>

           </div>
            <div id="body-section">
                <div id="home-section">
                    <div class="null-case">
                        <div class="header-line">
                             <!-- this is the header line -->
                        </div>
                        <div id="counts">
                            <!-- this is where the counts go-->
                        </div>
                        <div id="sent-messages-block">
                            <div id="outgoing" class="count-blocks floatLeft">
                            </div>

                            <div id="incoming" class="count-blocks floatLeft">

                            </div>

                            <div id="bene" class="count-blocks floatLeft">

                            </div>
                        </div>
                    </div>
                </div>

                 <!--- messages --template -->
                 <div id = "messages-section" class="displayNone">

                 </div>

                <!-- messages templates end--->

                <!-- beneficiary template -->
                <a name="benef"></a>
                <div id="benef-section" class="displayNone">
                    <div id="benef-header">
                            <div id="benef-settings" class="floatLeft">
                                <a><h1>Beneficiaries<img src="static/my-account/images/icons/down-arrow.png" ></h1></a>
                            </div>

                    </div>
                    <div id="bene-block">
                       <div class="left">
                           <div class="row">
                                 <div class="round-thumb floatLeft">

                                 </div>
                                 <div class="name-rln floatLeft">
                                     <h1>Manon Smith</h1>
                                     <p>MOM</p>
                                 </div>
                           </div>
                       </div>
                       <div class="right"></div>
                    </div>
                </div>

                <!-- beneficiary template -->
             </div>

<script type="text/javascript">
    $(document).ready(function(){
          var url ="/api/usermsg/accountview/${userId}";
          var data = "";
          LoadSpinner("data-loader-window","Loading Dashboard...");
          jQuery.ajax({
                                method:     'GET',
                                url:        url,
                                data:       data,
                                headers: {
                                    "Accept" : "application/json; charset=utf-8",
                                    "Content-Type": "application/json; charset=utf-8"
                                },
                                beforeSend: function() {
                                    //attach loader
                                   $('#data-loader-window').modal();

                                },
                                success:    function(resp) {
                                  PopulateUserInfo(resp);
                                  PopulateCounts(resp);

                                },
                                error:      function() {
                                    alert("error")
                                }
                            });
    })
</script>