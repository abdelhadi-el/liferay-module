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

<%@ include file="/document_library/init.jsp" %>

<%
FolderActionDisplayContext folderActionDisplayContext = new FolderActionDisplayContext(request);
%>

<c:if test="<%= folderActionDisplayContext.isShowActions() %>">
	<liferay-ui:icon-menu
		direction="left-side"
		icon="<%= StringPool.BLANK %>"
		markupView="lexicon"
		message='<%= LanguageUtil.get(request, "actions") %>'
		showWhenSingleIcon="<%= true %>"
	>
		<c:if test="<%= folderActionDisplayContext.isDownloadFolderActionVisible() %>">
			<liferay-ui:icon
				message="download"
				method="get"
				url="<%= folderActionDisplayContext.getDownloadFolderURL() %>"
			/>
		</c:if>

		<c:if test="<%= folderActionDisplayContext.isEditFolderActionVisible() %>">
			<liferay-ui:icon
				message="edit"
				url="<%= folderActionDisplayContext.getEditFolderURL() %>"
			/>
		</c:if>

		<c:if test="<%= folderActionDisplayContext.isMoveFolderActionVisible() %>">
			<liferay-ui:icon
				message="move"
				url="<%= folderActionDisplayContext.getMoveFolderURL() %>"
			/>
		</c:if>

		<%-- Chintan - Copy Functionality --%>
		<c:if test="<%= folderActionDisplayContext.isCopyFolderActionVisible() %>">
			<liferay-ui:icon
				message="copy"
				url="<%= folderActionDisplayContext.getCopyFolderURL() %>"
			/>
		</c:if>

		<c:if test="<%= folderActionDisplayContext.isDeleteExpiredTemporaryFileEntriesActionVisible() %>">
			<liferay-ui:icon
				message="delete-expired-temporary-files"
				url="<%= folderActionDisplayContext.getDeleteExpiredTemporaryFileEntriesURL() %>"
			/>
		</c:if>

		<c:if test="<%= folderActionDisplayContext.isAddFolderActionVisible() %>">
			<liferay-ui:icon
				message="add-folder"
				url="<%= folderActionDisplayContext.getAddFolderURL() %>"
			/>
		</c:if>

		<c:if test="<%= folderActionDisplayContext.isAddRepositoryActionVisible() %>">
			<liferay-ui:icon
				message="add-repository"
				url="<%= folderActionDisplayContext.getAddRepositoryURL() %>"
			/>
		</c:if>

		<c:if test="<%= folderActionDisplayContext.isAddMediaActionVisible() %>">
			<liferay-ui:icon
				message="add-file-entry"
				url="<%= folderActionDisplayContext.getAddMediaURL() %>"
			/>

			<c:if test="<%= folderActionDisplayContext.isMultipleUploadSupported() %>">
				<liferay-ui:icon
					cssClass="hide upload-multiple-documents"
					message="multiple-media"
					url="<%= folderActionDisplayContext.getAddMultipleMediaURL() %>"
				/>
			</c:if>
		</c:if>

		<c:if test="<%= folderActionDisplayContext.isViewSlideShowActionVisible() %>">
			<liferay-ui:icon
				cssClass='<%= folderActionDisplayContext.getRandomNamespace() + "-slide-show" %>'
				message="view-slide-show"
				url="javascript:;"
			/>
		</c:if>

		<c:if test="<%= folderActionDisplayContext.isAddFileShortcutActionVisible() %>">
			<liferay-ui:icon
				message="add-shortcut"
				url="<%= folderActionDisplayContext.getAddFileShortcutURL() %>"
			/>
		</c:if>

		<c:if test="<%= folderActionDisplayContext.isAccessFromDesktopActionVisible() %>">
			<liferay-util:include page="/document_library/access_from_desktop.jsp" servletContext="<%= application %>" />
		</c:if>

		<c:if test="<%= folderActionDisplayContext.isPermissionsActionVisible() %>">
			<liferay-security:permissionsURL
				modelResource="<%= folderActionDisplayContext.getModelResource() %>"
				modelResourceDescription="<%= HtmlUtil.escape(folderActionDisplayContext.getModelResourceDescription()) %>"
				resourcePrimKey="<%= String.valueOf(folderActionDisplayContext.getResourcePrimKey()) %>"
				var="permissionsURL"
				windowState="<%= LiferayWindowState.POP_UP.toString() %>"
			/>

			<liferay-ui:icon
				message="permissions"
				method="get"
				url="<%= permissionsURL %>"
				useDialog="<%= true %>"
			/>
		</c:if>

		<c:if test="<%= folderActionDisplayContext.isDeleteFolderActionVisible() %>">
			<liferay-ui:icon-delete
				trash="<%= folderActionDisplayContext.isTrashEnabled() %>"
				url="<%= folderActionDisplayContext.getDeleteFolderURL() %>"
			/>
		</c:if>

		<c:if test="<%= folderActionDisplayContext.isPublishFolderActionVisible() %>">
			<liferay-ui:icon-delete
				confirmation="are-you-sure-you-want-to-publish-the-selected-folder"
				message="publish-to-live"
				url="<%= folderActionDisplayContext.getPublishFolderURL() %>"
			/>
		</c:if>
	</liferay-ui:icon-menu>

	<aui:script use="uploader">
		if (!A.UA.ios && A.Uploader.TYPE != 'none') {
			var uploadMultipleDocumentsIcon = A.all(
				'.upload-multiple-documents:hidden'
			);

			uploadMultipleDocumentsIcon.show();
		}

		var slideShow = A.one(
			'.<%= folderActionDisplayContext.getRandomNamespace() %>-slide-show'
		);

		if (slideShow) {
			slideShow.on('click', function(event) {
				var slideShowWindow = window.open(
					'<%= folderActionDisplayContext.getViewSlideShowURL() %>',
					'slideShow',
					'directories=no,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no'
				);

				slideShowWindow.focus();
			});
		}
	</aui:script>
</c:if>