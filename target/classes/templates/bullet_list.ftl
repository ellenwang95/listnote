<ul>
	<#list cursors as cursor>
	<li class="bullet_list" style="margin-left:${20*point_collection.units.get(cursor).unit.level}px">
		${point_collection.units.get(cursor).unit.body}
		<span class="exposing_hashtags_container"><#list point_collection.units.get(cursor).unit.tags.units as tag>
			<span class="tag">
				${tag.body}
			</span>
			</#list>
		</span>
	</li>
	</#list>
</ul>