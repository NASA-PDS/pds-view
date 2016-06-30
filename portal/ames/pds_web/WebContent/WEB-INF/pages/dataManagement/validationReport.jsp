<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="w" uri="/widget-tags"%>

============================================================
<s:text name="validationResults.title.elementDetails" />
============================================================
<s:text name="validationResults.label.name" />: <s:property value="selectedNode.name" />

<s:if test="selectedNode.file.directory"><s:text name="validationResults.label.size" />: <s:property value="selectedNode.numFiles" /> (<s:property value="selectedNode.size" />)</s:if><s:else><s:text name="validationResults.label.size" />: <s:property value="selectedNode.size" /></s:else>

<s:text name="validationResults.label.problems" />: <s:text name="previewFile.text.numErrors"><s:param value="selectedNode.numErrors" /></s:text>, <s:text name="previewFile.text.numWarnings"><s:param value="selectedNode.numWarnings" /></s:text>

<s:if test="newValues.size() > 0">============================================================
<s:text name="validationResults.title.newValues" />
============================================================
<s:text name="validationResults.table.newValue.column.key" />	<s:text name="validationResults.table.newValue.column.value" />	<s:text name="validationResults.table.newValue.column.resource" />	<s:text name="validationResults.table.newValue.column.path" />	<s:text name="validationResults.table.newValue.column.lineNumber" />

<s:iterator value="newValues" status="status"><s:property value="key" />	<s:property value="value" />	<s:property value="resource" />	<s:property value="path" />	<s:property value="lineNumber" />
</s:iterator></s:if>

============================================================
<s:text name="validationResults.title.problems" />
============================================================
<s:text name="validationResults.table.problems.column.description" />	<s:text name="validationResults.table.problems.column.resource" />	<s:text name="validationResults.table.problems.column.path" />	<s:text name="validationResults.table.problems.column.lineNumber" />

<s:iterator value="problemGroups">[<s:property value="type.severity" />] <s:property value="description" escape="false" /> (<s:property value="size" />)
<s:iterator value="clusters"><s:if test="problems.size() > 1">     <s:property value="description" escape="false" /> <s:text name="generic.text.matches"><s:param value="problems.size()" /></s:text>
<s:iterator value="problems">          <s:property value="message" escape="false" />	<s:property value="resource" />	<s:property value="path" />	<s:property value="lineNumber" />
</s:iterator></s:if><s:else><s:iterator value="problems">     <s:property value="message" escape="false" />	<s:property value="resource" />	<s:property value="path" />	<s:property value="lineNumber" />
</s:iterator></s:else></s:iterator><s:if test="excessProblems > 0"><s:text name="validation.text.excessErrors"><s:param value="excessProblems" /></s:text></s:if>
</s:iterator>
<s:if test="problemGroups.size() == 0"><s:text name="validationResults.table.problems.text" /></s:if>