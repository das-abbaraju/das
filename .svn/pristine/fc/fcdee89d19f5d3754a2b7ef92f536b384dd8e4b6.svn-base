<div id="homepage-content">

       <div id="sign-up-div">
           <div class="centered-div">
               <form id="sign-up-form" method="POST" ><!-- action = "add-user.view" -->
                  <h1>
                      Sign Up
                  </h1>
                  <div class="input-holder">
                      <div class="floatLeft ">
                            <input type="text" name="fn" id="fn" class="small-input-box  input-box-height hints" value="First Name" onblur="changeInputVal(this,'First Name')" onfocus="changeInputVal(this,'First Name')"/>
                      </div>
                       <div class="floatLeft">
                            <input type="text" name="ln" id="ln" class="small-input-box input-box-height hints" value="Last Name" onblur="changeInputVal(this,'Last Name')" onfocus="changeInputVal(this,'Last Name')"/>
                      </div>
                  </div>
                  <div class="input-holder">
                     <input type="text" name="email" id="email" class="full-input-box input-box-height hints" value="Email Address" onblur="changeInputVal(this,'Email Address')" onfocus="changeInputVal(this,'Email Address')"/>
                  </div>
                  <div class="input-holder">
                     <input type="password" name="password" id="password" class="full-input-box input-box-height hints" value="Password" onblur="changeInputVal(this,'Password')" onfocus="changeInputVal(this,'Password')"/>
                  </div>
                   <div class="input-holder">
                     <input type="text" name="zipcode" id="zipcode" class="full-input-box input-box-height hints" value="Zipcode" onblur="changeInputVal(this,'Zipcode')" onfocus="changeInputVal(this,'Zipcode')"/>
                  </div>
                  <div id="submit-button-div">
                      <div class="floatLeft">
                          By creating an account, I accept KrystalArk<br>
                          <span class="grey-link"><a href="#">Terms of service</a></span>
                          and
                          <span class="grey-link"><a href="#">Privacy Policy</a></span>
                      </div>
                      <div class="floatRight textRight">
                          <button type="submit" id="sign-up-submit" name="sign-up-submit"/>
                      </div>
                  </div>
               </form>
           </div>
       </div>


       <!-- LOgin div starts -->
       <div id="login-div" class="displayNone">
           <div class="centered-div">
               <form id="login-form" method="PUT" >
                  <h1>
                      Login
                  </h1>
                  <div class="input-holder">
                      <div class="floatLeft ">
                            <input type="text" name="username" id="username" class="small-input-box  input-box-height hints" value="Email" onblur="changeInputVal(this,'Email')" onfocus="changeInputVal(this,'Email')"/>
                      </div>
                       <div class="floatLeft">
                            <input type="password" name="loginpassword" id="loginpassword" class="small-input-box input-box-height hints" value="Password" onblur="changeInputVal(this,'Password')" onfocus="changeInputVal(this,'Password')"/>
                      </div>
                  </div>

                  <div id="login-submit-button-div">
                      <div class="floatLeft">
                           <div id="new-user-div">New to KrystalArk? <a href="javascript" id="new-user-link" class="grey-link no-underline">Sign up</a> </div>
                          <div id="forgot-pwd-div"><a href="javascript://" id="forgot-pwd-link" class="grey-link no-underline">Forgot your password?</a></div>

                      </div>
                      <div class="floatRight textRight">
                          <button type="submit" name="login-submit" id="login-submit" value=""/>
                      </div>
                  </div>
               </form>
           </div>
       </div>

      <!-- login div ends -->



       <div id="background-promo-div">




            <a id="prevslide" class="load-item"></a>
	        <a id="nextslide" class="load-item"></a>


            <!--Time Bar-->
            <div id="progress-back" class="load-item">
                <div id="progress-bar"></div>
            </div>

            <!--Control Bar-->
            <div id="controls-wrapper" class="load-item">
                <div id="controls">
                    <!--Navigation-->
                    <ul id="slide-list"></ul>
                </div>
            </div>
           <div id="top-text">
                       <p class="header-text">
                            KRYSTALARK&reg; lets you leave and schedule meaningful messages meant to be retrieved and viewed by your loved ones after you pass away.
                       </p>
           </div>
           <div id="picture-message-block">
               <div id="picture-message">
                   <div class="sub-header">
                        KRYSTALARK&reg; <br />
                        Keeping You Connected With Your Loved Ones. Forever.
                   </div>
               </div>
                 <p class="get-started-button">
                      <button type="button" id="get-started"></button>
                 </p>
           </div>
        </div>


</div>
<div id = "promo-text-contain">
    <div id = "promo-text">
            You May Have Messages From A Loved One Waiting For You - <a href="javascript://">Find Out Now</a>
        </div>
    </div>

 <script>
     $(document).ready(function(){


			jQuery(function($){

				$.supersized({

					// Functionality
					slideshow               :   1,			// Slideshow on/off
					autoplay				:	0,			// Slideshow starts playing automatically
					start_slide             :   1,			// Start slide (0 is random)
					stop_loop				:	0,			// Pauses slideshow on last slide
					random					: 	0,			// Randomize slide order (Ignores start slide)
					slide_interval          :   5000,		// Length between transitions
					transition              :   6, 			// 0-None, 1-Fade, 2-Slide Top, 3-Slide Right, 4-Slide Bottom, 5-Slide Left, 6-Carousel Right, 7-Carousel Left
					transition_speed		:	1000,		// Speed of transition
					new_window				:	1,			// Image links open in new window/tab
					pause_hover             :   0,			// Pause slideshow on hover
					keyboard_nav            :   1,			// Keyboard navigation on/off
					performance				:	1,			// 0-Normal, 1-Hybrid speed/quality, 2-Optimizes image quality, 3-Optimizes transition speed // (Only works for Firefox/IE, not Webkit)
					image_protect			:	0,			// Disables image dragging and right click with Javascript

					// Size & Position
					min_width		        :   0,			// Min width allowed (in pixels)
					min_height		        :   0,			// Min height allowed (in pixels)
					vertical_center         :   1,			// Vertically center background
					horizontal_center       :   1,			// Horizontally center background
					fit_always				:	0,			// Image will never exceed browser width or height (Ignores min. dimensions)
					fit_portrait         	:   1,			// Portrait images will not exceed browser height
					fit_landscape			:   0,			// Landscape images will not exceed browser width

					// Components
					slide_links				:	'blank',	// Individual links for each slide (Options: false, 'num', 'name', 'blank')
					thumb_links				:	1,			// Individual thumb links for each slide
					thumbnail_navigation    :   0,			// Thumbnail navigation
					slides 					:  	[			// Slideshow Images
                                                        {image : '/site/static/global/images/background/slide1.jpg', title : 'lake', thumb : '', url : ''},
                                                        {image : '/site/static/global/images/background/slide3.jpg', title : 'sunset', thumb : '', url : ''},
														{image : '/site/static/global/images/background/slide2.jpg', title : 'kid', thumb : '', url : ''}


												],

					// Theme Options
					progress_bar			:	1,			// Timer for each slide
					mouse_scrub				:	0

				});
		    });

     })
 </script>