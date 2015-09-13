<ul>
	<#list cursors as cursor>
	<li class="def_list" style="margin-left:${20*point_collection.units[cursor].level}px">
		<h2 class="def_list_element term">${point.term}:</h2>
		<div class="def_list_element def">${point.body}</div>
		<span class="exposing_hashtags_container"><#list point_collection.units[cursor].tags as tag>
			<span class="tag">
				${tag.body}
			</span>
			</#list>
		</span>
	</li>
	</#list>
</ul>