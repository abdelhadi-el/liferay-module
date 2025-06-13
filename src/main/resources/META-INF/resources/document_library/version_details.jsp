<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */
--%>

<%@page import="com.liferay.document.library.kernel.model.DLVersionNumberIncrease"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@ include file="/document_library/init.jsp" %>

<%
boolean checkedOut = GetterUtil.getBoolean(request.getAttribute("edit_file_entry.jsp-checkedOut"));
%>

<div id="<portlet:namespace />versionDetails" style="display: none;">
	<aui:fieldset>
		<h5 class="control-label"><liferay-ui:message key="select-whether-this-is-a-major-or-minor-version" /></h5>

		<aui:input checked="<%= checkedOut %>" label="major-version" name="versionDetailsVersionIncrease" onChange='<%= renderResponse.getNamespace() + "showVersionNotes(event);" %>' type="radio" value="<%= DLVersionNumberIncrease.MAJOR %>" />

		<aui:input checked="<%= !checkedOut %>" label="minor-version" name="versionDetailsVersionIncrease" onChange='<%= renderResponse.getNamespace() + "showVersionNotes(event);" %>' type="radio" value="<%= DLVersionNumberIncrease.MINOR %>" />

		<aui:input checked="<%= false %>" label="keep-current-version-number" name="versionDetailsVersionIncrease" onChange='<%= renderResponse.getNamespace() + "hideVersionNotes(event);" %>' type="radio" value="<%= DLVersionNumberIncrease.NONE %>" />

		<aui:input label="version-notes" maxLength="75" name="versionDetailsChangeLog" />
	</aui:fieldset>

	<aui:script>
		function <portlet:namespace />hideVersionNotes(event) {
			var fieldset = event.currentTarget.closest('fieldset');

			var versionNotes = fieldset.querySelector(
				'#<portlet:namespace />versionDetailsChangeLog'
			);

			if (versionNotes) {
				versionNotes.parentElement.classList.add('hide');
			}
		}

		function <portlet:namespace />showVersionNotes(event) {
			var fieldset = event.currentTarget.closest('fieldset');

			var versionNotes = fieldset.querySelector(
				'#<portlet:namespace />versionDetailsChangeLog'
			);

			if (versionNotes) {
				versionNotes.parentElement.classList.remove('hide');
			}
		}
	</aui:script>
</div>