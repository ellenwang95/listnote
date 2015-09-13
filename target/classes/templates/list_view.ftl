<ul class="unstyled_vertical_list" id="note_collection_view">
	<#list note_collection as note>
	<li>
		<span>${note.title}</span>
		<span style="float:right">${note.date}</span>
	</li>
	</#list>
</ul>