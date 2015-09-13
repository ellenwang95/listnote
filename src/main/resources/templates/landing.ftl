<html>
	<head>
		<link rel="stylesheet" href="css/app/main.css" />
		<link rel="stylesheet" href="css/app/landing.css" />
		<link href='http://fonts.googleapis.com/css?family=Open+Sans:400italic,400,700,300' rel='stylesheet' type='text/css'>
		<script type="text/javascript">
		require.config({
			baseUrl: 'js/vendor',
			paths: {
				"jquery": "//ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min",
				"Q": "//cdnjs.cloudflare.com/ajax/libs/q.js/1.1.0/q",
				"lodash": "//cdnjs.cloudflare.com/ajax/libs/lodash.js/3.10.1/lodash",
				'app': '../app',
			},
			shim: {
				'getStyleObject': {
					'deps': ['jquery']
				},
				'jquery.filedrop': {
					'deps': ['jquery']
				},
				'history': {
					'deps': ['jquery'],
					'exports': 'History'
				},
				'modernizr': {
					'exports': 'Modernizr'
				}
			}
		});
		require(['jquery', 'lodash', 'Q'], function($, _, Q) {
			
		});
		</script>
		<style type="text/css">
		.focus_container {
			background-image:url(../../img/front_page_gradient.jpg);
			background-size:cover;
		}
		</style>
	</head>
		
	<body>
		<div class="focus_container">
			<div class="horizontal_nav">
				<a href="login.html">
					<span class="cta_button">
						<span class="button_underlay" style="background-color:#5f4278;"></span><!--
					--><span class="button_overlay" style="background-color:#704b8f;">LOG IN</span>
					</span>
				</a>
				<img src="logo.png" id="main_logo" />
			</div>
			<div class="focus_text_wrapper">
				<div class="focus_text">
					<img src="pretty_picture_1.png" />
					<h2>Finally, digital lists that makes sense.</h2>
					<p>
						Listnote was designed to cater to two things: aesthetic perfection and seamless productivity. And it already looks amazing.
					</p>
					<p>
						Ditch the linear list. Unfurl dimensions of information relationships by drawing ties between relevant points. Alongside intuitive-for-once natural language processing, taking your online note-taking to the next level is beautifully effort-list.
					</p>
					<a href="login.html">
						<span class="white_hover_button">
							LOGIN
						</span>
					</a><!--
				--><span class="disabled_gray_button">
						Signup coming soon
					</span>
				</div>
			</div>
		</div>
	</body>
</html>