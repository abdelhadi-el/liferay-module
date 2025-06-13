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

<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.document.library.util.DLURLHelperUtil"%>
<%@page import="com.liferay.document.library.preview.exception.DLPreviewGenerationInProcessException"%>
<%@page import="com.liferay.document.library.preview.exception.DLPreviewSizeException"%>
<%@page import="com.liferay.portal.util.WebKeys"%>
<%@page import="com.liferay.document.library.web.internal.constants.DLWebKeys"%>
<%@page import="com.liferay.portal.kernel.repository.model.FileVersion"%>
<%@ include file="/document_library/init.jsp" %>

<%
Exception exception = (Exception)request.getAttribute(DLWebKeys.DOCUMENT_LIBRARY_PREVIEW_EXCEPTION);
FileVersion fileVersion = (FileVersion)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_VERSION);
		 System.out.println("Debug 1 exception----"+exception.getMessage());	
		 System.out.println("Debug 2 fileVersion----"+fileVersion.getVersion());
%>

<c:choose>
	<c:when test="<%= exception instanceof DLPreviewSizeException %>">
		<div class="preview-file-error-container">
			<h3><liferay-ui:message key="file-too-big-to-preview" /></h3>

			<p class="text-secondary">
				<liferay-ui:message key="file-exceeds-size-limit-to-preview-download-to-view-it" />
			</p>

			<clay:link
				buttonStyle="secondary"
				href="<%= DLURLHelperUtil.getDownloadURL(fileVersion.getFileEntry(), fileVersion, themeDisplay, StringPool.BLANK) %>"
				label='<%= LanguageUtil.get(resourceBundle, "download") %>'
				title='<%= LanguageUtil.format(resourceBundle, "file-size-x", LanguageUtil.formatStorageSize(fileVersion.getSize(), locale), false) %>'
			/>
		</div>
	</c:when>
	<c:when test="<%= exception instanceof DLPreviewGenerationInProcessException %>">
		<clay:alert
			message='<%= LanguageUtil.get(resourceBundle, "generating-preview-will-take-a-few-minutes") %>'
			title='<%= LanguageUtil.get(request, "info") + ":" %>'
		/>
	</c:when>
	<c:otherwise>
		<div class="preview-file-error-container">
			<h3><liferay-ui:message key="no-preview-available" /></h3>

			<p class="text-secondary">
				<liferay-ui:message key="hmm-looks-like-this-item-doesnt-have-a-preview-we-can-show-you" />
			</p>
		</div>
	</c:otherwise>
</c:choose>