<ul>
	<#list cursors as cursor>
	<li class="def_list" style="margin-left:${left_margins.get(cursor?int)}px">
		<h2 class="def_list_element term">${point_collection.units.get(cursor?int).unit.term}:</h2>
		<div class="def_list_element def">${point_collection.units.get(cursor?int).unit.body}</div>
		<span class="exposing_hashtags_container"><#list point_collection.units.get(cursor).unit.tags as tag>
			<span class="tag">
				${tag.body}
			</span>
			</#list>
		</span>
	</li>
	</#list>
</ul>