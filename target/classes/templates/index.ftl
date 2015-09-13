<html>
	<head>
		<link rel="stylesheet" href="css/app/main.css" />
		<link rel="stylesheet" href="css/app/nav.css" />
		<link rel="stylesheet" href="css/app/panel.css" />
		<script type="text/javascript" src="js/vendor/require.js"></script>
		<script type="text/javascript" data-main="js/vendor">
		require.config({
			baseUrl: 'js/vendor',
			shim: {
				'getStyleObject': {
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
		require(['modernizr'], function(Modernizr) {
			Modernizr.load([
			{
				test: Modernizr.csstransforms,
				yep: 'css/app/transformations.css'
			}]);
		});
		require(['jquery', 'lodash', 'domReady', 'history', 'crossroads'], function($, _, domReady, History, X) {
			$.ajaxPrefilter(function(options, original_options, jqXHR) {
				options.data = $.param($.extend(original_options.data, {
					'from':window.location.pathname
				})); //send the location of the calling script with each Ajax request
			});
			History.Adapter.bind(window, 'statechange', function() {
				var State = History.getState();
				X.parse(document.location.pathname + document.location.search + document.location.hash)
			});
			X.ignoreState = true;
			
			domReady(function() {
				$('.placeholder').mousedown(function(e) {
					e.preventDefault();
					e.stopPropagation();
					$(this).nextAll('.placeholder_input').trigger('focus');
					$(this).css('display', 'none');
				});

				$('input.placeholder_input')
				.focus(function(e) {
					$(this).prevAll('.placeholder').css('display', 'none');
				})
				.blur(function(e) {
					if(this.value==='') {
						$(this).prevAll('.placeholder').css('display', '');
					}
				});
				
				$('#primary_search_container').keypress(function(e) {
					//live searching?
				});
			});
		});
		</script>
	</head>
	
	<body>
		<span id="nav_container">
			<a href="/"><span id="logo_container"><img src="/img/logo_full_prototype.png" id="logo" /></span></a>
			<div id="logo_shadow"></div>
			${rendered_navigation}
		</span>
		<div id="block_view_panel_container">
			<span style="width:100%; height:100%; position:relative;">
				<div class="pretty_banner"></div>
				<span id="primary_search_container">
					<span class="placeholder" id="primary_search_container_placeholder">
						SEARCH
					</span>
					<input id="primary_search_container" class="placeholder_input" type="text" />
				</span><!--
			--><span id="title_banner">
				<#if title??>
					<span id="note_title">${title}</span>
				<#else>
					<span id="title_banner">HOME</span>
				</#if></span>
				<div class="main_body">
					<!-- smattering of lists goes here -->
					${rendered_body}
				</div><!-- top padding, absolute header -->
			</span>
		</div>
	</body>
</html>