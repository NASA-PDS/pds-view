<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="title">Create Data Set</div>
<div class="basicBox">
<form action="<s:url action="SaveDataSetInfo" />" method="post">
	<input type="hidden" name="dataUUID" value="<s:property value="dataUUID" />" />
	
	
	<div class="label"><s:text name="saveDataSet.label.setType" /></div>
	<div class="contents">
		<select name="setType" onchange="this.form.submit()">
			<s:iterator value="setTypeOptions">
				<option value="<s:property value="value" />"<s:if test="selected"> selected="selected"</s:if>><s:property value="label" /></option>
			</s:iterator>
		</select>
		
		<input type="submit" name="filterSetType" value="select" class="accessibility" />
	</div>
	
	<s:if test="setType == 'IMAGE'">
		<table cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<div class="label">Width</div>
					<div class="contents"><input type="text" size="10" name="width" value="<s:property value="width" />" /></div>
				</td>
				<td>
					<div class="label">Height</div>
					<div class="contents"><input type="text" size="10" name="height" value="<s:property value="height" />" /></div>
				</td>
			</tr>
			<tr>
				<td>
					<div class="label">Bits Per Pixel</div>
					<div class="contents">
						<select name="bitsPerPixel">
							<s:iterator value="bitsPerPixelOptions">
								<option value="<s:property value="value" />"<s:if test="selected"> selected="selected"</s:if>><s:property value="label" /></option>
							</s:iterator>
						</select>
					</div>
				</td>
				<td>
					<div class="label">Pixel Format</div>
					<div class="contents">
						<select name="pixelFormat">
							<s:iterator value="pixelFormatOptions">
								<option value="<s:property value="value" />"<s:if test="selected"> selected="selected"</s:if>><s:property value="label" /></option>
							</s:iterator>
						</select>
					</div>
				</td>
			</tr>
		</table>
	</s:if>
	
	<input type="submit" name="save" value="save" />
</div>
<%--
	<div class="label"><s:text name="saveDataSet.label.collectedLocation" /></div>
	<div class="contents">
		<select name="collectedLocation">
			<s:iterator value="locationOptions">
				<option value="<s:property value="value" />"<s:if test="selected"> selected="selected"</s:if>><s:property value="label" /></option>
			</s:iterator>
		</select>
	</div>
	
	<div class="label"><input type="checkbox" name="attached" value="1"<s:if test="attached"> checked="checked"</s:if> /> <s:text name="saveDataSet.label.embedMetaInfo" /></div>
	
	<div class="label"><s:text name="saveDataSet.label.dataType" /></div>
	<div class="content">
		<select name="dataType">
		<s:iterator value="dataTypeOptions">
			<option value="<s:property value="value" />"<s:if test="selected"> selected="selected"</s:if>><s:property value="label" /></option>
		</s:iterator>
		</select>
	</div>
	 --%>
</form>