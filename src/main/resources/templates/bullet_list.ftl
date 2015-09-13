<ul>
	<#list cursors as cursor>
	<li class="bullet_list" style="margin-left:${20*point_collection.units[cursor].level}px">
		${point.body}
		<span class="exposing_hashtags_container"><#list point_collection.units[cursor].tags as tag>
			<span class="tag">
				${tag.body}
			</span>
			</#list>
		</span>
	</li>
	</#list>
</ul>