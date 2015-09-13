<html>
<head>
	<link href='http://fonts.googleapis.com/css?family=Open+Sans:400italic,400,700,300' rel='stylesheet' type='text/css' />
	<link rel="stylesheet" href="/css/app/main.css" />
	<link rel="stylesheet" href="/css/app/authenticate.css" />
	<style type="text/css">
	</style>
	<script type="text/javascript" src="/js/vendor/jquery.js"></script>
</head>

<body>
	<div id="valign_container">
		<span id="valign_wrapper">
			<span id="authentication_container">
				<span id="authentication_wrapper">
					<form action="/" method="POST" id="mainform">
					<img src="/img/logo.png" height="40" style="margin:50px auto;" />
					<div id="pointer_triangle"></div><!--
				--><div style="background-color:#333538;">
						<div id="authentication_header">HI! PLEASE LOG IN.</div>
						<div class="input_container">
							<label for="u">USER:</label><input type="text" name="u" />
						</div>
						<div class="input_container">
							<label for="u">PASS:</label><input type="password" name="p" />
						</div>
						<div class="button_container" onclick="$('#mainform').submit();">
							<span class="button_underlay" style="background-color:#c13c32;"></span>
							<span class="button_overlay">LOG IN</span>
						</div>
					</div>
				</span>
			</span>
		</span>
	</div>
</body>	
</html>