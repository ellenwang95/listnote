<ul>
	<#list cursors as cursor>
	<li class="bullet_list" style="margin-left:${20*point_collection.units[cursor].level}px">
		${point.body}
	</li>
	<#else>
	<li>
	
	</li>
	</#list>
</ul>