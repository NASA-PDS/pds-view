 <%@ taglib prefix="s" uri="/struts-tags" %>

<div class="spacedWrapper">
    <div class="title"><s:text name="selectObject.title" /></div>
    <div class="basicBox">
        <div class="contents"><s:text name="selectObject.text.select" /></div>
        
        <s:iterator value="slice.tabularDataObjects" status="iterStatus">
    	        	<a href="<s:url action="LoadData" >
        		<s:param name="activeId" value="#iterStatus.index"/>
        		<s:param name="procId" value="%{procId}"/>		
        		</s:url>">
        		<s:property value="tabFileName"/>
        		        	</a>
        		- <s:property value="type"/> with
        		<s:property value="columns.size"/> Columns and
        		<s:property value="totalRows"/> Rows
        		<br/>
        		
        	<%--
        	type - data file name linked to load        	 
        	index # (to pass in)
        	activeId
        	--%>
        </s:iterator>
    </div>
</div> 
 
