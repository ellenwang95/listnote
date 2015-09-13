<div>
</div>
<ul class="unstyled_vertical_list" id="note_list">
	<#list note_collection as note>
	<li style="padding:15px;">
		<span style="float:right; font-size:11px;">${note.date}</span><span style="width:70%; overflow:hidden; text-overflow: ellipsis;">${note.title}</span>
	</li>
	</#list>
</ul>