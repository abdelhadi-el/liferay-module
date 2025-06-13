package org.apache.jsp.document_005flibrary;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.util.AssetHelper;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.DLDisplayContextProvider;
import com.liferay.document.library.display.context.DLEditFileEntryDisplayContext;
import com.liferay.document.library.display.context.DLFilePicker;
import com.liferay.document.library.display.context.DLViewFileEntryHistoryDisplayContext;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerException;
import com.liferay.document.library.kernel.document.conversion.DocumentConversionUtil;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryTypeException;
import com.liferay.document.library.kernel.exception.DuplicateFolderNameException;
import com.liferay.document.library.kernel.exception.DuplicateRepositoryNameException;
import com.liferay.document.library.kernel.exception.FileEntryLockException;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileMimeTypeException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileShortcutPermissionException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.exception.FolderNameException;
import com.liferay.document.library.kernel.exception.InvalidFileVersionException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.exception.NoSuchMetadataSetException;
import com.liferay.document.library.kernel.exception.RepositoryNameException;
import com.liferay.document.library.kernel.exception.RequiredFileEntryTypeException;
import com.liferay.document.library.kernel.exception.RequiredFileException;
import com.liferay.document.library.kernel.exception.SourceFileNameException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeServiceUtil;
import com.liferay.document.library.kernel.util.AudioProcessorUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.util.ImageProcessorUtil;
import com.liferay.document.library.kernel.util.PDFProcessorUtil;
import com.liferay.document.library.kernel.util.RawMetadataProcessor;
import com.liferay.document.library.kernel.util.VideoProcessorUtil;
import com.liferay.document.library.preview.exception.DLPreviewGenerationInProcessException;
import com.liferay.document.library.preview.exception.DLPreviewSizeException;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.document.library.web.internal.constants.DLWebKeys;
import com.liferay.document.library.web.internal.dao.search.DLResultRowSplitter;
import com.liferay.document.library.web.internal.dao.search.IGResultRowSplitter;
import com.liferay.document.library.web.internal.display.context.DLAdminDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLAdminDisplayContextProvider;
import com.liferay.document.library.web.internal.display.context.DLAdminManagementToolbarDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLAdminNavigationDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLSelectRestrictedFileEntryTypesDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLViewFileEntryTypesDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLViewMoreMenuItemsDisplayContext;
import com.liferay.document.library.web.internal.display.context.IGDisplayContextProvider;
import com.liferay.document.library.web.internal.display.context.logic.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.logic.DLVisualizationHelper;
import com.liferay.document.library.web.internal.display.context.util.DLRequestHelper;
import com.liferay.document.library.web.internal.display.context.util.IGRequestHelper;
import com.liferay.document.library.web.internal.dynamic.data.mapping.util.DLDDMDisplay;
import com.liferay.document.library.web.internal.portlet.action.ActionUtil;
import com.liferay.document.library.web.internal.portlet.action.EditFileEntryMVCActionCommand;
import com.liferay.document.library.web.internal.search.EntriesChecker;
import com.liferay.document.library.web.internal.search.EntriesMover;
import com.liferay.document.library.web.internal.security.permission.resource.DLFileEntryPermission;
import com.liferay.document.library.web.internal.security.permission.resource.DLFileEntryTypePermission;
import com.liferay.document.library.web.internal.security.permission.resource.DLFolderPermission;
import com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings;
import com.liferay.document.library.web.internal.util.DLBreadcrumbUtil;
import com.liferay.document.library.web.internal.util.DLSubscriptionUtil;
import com.liferay.document.library.web.internal.util.DLTrashUtil;
import com.liferay.document.library.web.internal.util.DLWebComponentProvider;
import com.liferay.document.library.web.internal.util.IGUtil;
import com.liferay.dynamic.data.mapping.kernel.DDMStructure;
import com.liferay.dynamic.data.mapping.kernel.DDMStructureManager;
import com.liferay.dynamic.data.mapping.kernel.DDMStructureManagerUtil;
import com.liferay.dynamic.data.mapping.kernel.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.kernel.StorageFieldRequiredException;
import com.liferay.dynamic.data.mapping.kernel.StructureDefinitionException;
import com.liferay.dynamic.data.mapping.kernel.StructureDuplicateElementException;
import com.liferay.dynamic.data.mapping.kernel.StructureNameException;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMDisplay;
import com.liferay.dynamic.data.mapping.util.DDMNavigationHelper;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPDropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList;
import com.liferay.image.gallery.display.kernel.display.context.IGViewFileVersionDisplayContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.InvalidRepositoryException;
import com.liferay.portal.kernel.exception.NoSuchRepositoryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.language.UnicodeLanguageUtil;
import com.liferay.portal.kernel.lock.DuplicateLockException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.repository.AuthenticationRepositoryException;
import com.liferay.portal.kernel.repository.RepositoryConfiguration;
import com.liferay.portal.kernel.repository.RepositoryException;
import com.liferay.portal.kernel.repository.capabilities.CommentCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.BrowserSnifferUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.ToolbarItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.UnicodeFormatter;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowEngineManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.repository.registry.RepositoryClassDefinition;
import com.liferay.portal.repository.registry.RepositoryClassDefinitionCatalogUtil;
import com.liferay.portal.upload.LiferayFileItem;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.documentlibrary.DLGroupServiceSettings;
import com.liferay.portlet.documentlibrary.constants.DLConstants;
import com.liferay.portlet.usersadmin.search.GroupSearch;
import com.liferay.portlet.usersadmin.search.GroupSearchTerms;
import com.liferay.taglib.search.ResultRow;
import com.liferay.taglib.servlet.PipingServletResponse;
import com.liferay.taglib.util.PortalIncludeUtil;
import com.liferay.trash.model.TrashEntry;
import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import com.liferay.bulk.selection.BulkSelectionRunner;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.web.internal.bulk.selection.BulkSelectionRunnerUtil;
import com.liferay.document.library.web.internal.display.context.FolderActionDisplayContext;
import com.liferay.document.library.web.internal.util.DLAssetHelperUtil;
import com.liferay.document.library.web.internal.util.RepositoryClassDefinitionUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.util.RepositoryUtil;

public final class view_005fentries_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList<String>(6);
    _jspx_dependants.add("/document_library/init.jsp");
    _jspx_dependants.add("/init.jsp");
    _jspx_dependants.add("/init-ext.jsp");
    _jspx_dependants.add("/document_library/init-ext.jsp");
    _jspx_dependants.add("/document_library/cast_result.jspf");
    _jspx_dependants.add("/document_library/file_entry_vertical_card.jspf");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_a_href;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_horizontal$1card_url_text_rowChecker_resultRow_actionJspServletContext_actionJsp;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1iterator_searchContainer_resultRowSplitter_markupView_displayStyle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_if_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_clay_icon_symbol_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1status_status_name_cssClass_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_vertical$1card_url_title_rowChecker_resultRow_imageUrl_cssClass_actionJspServletContext_actionJsp;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_className;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_horizontal$1card$1col;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_choose;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_colspan_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_buffer_var;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1icon_toggleRowChecker_icon_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_include_servletContext_page_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_icon_message_markupView_image_cssClass_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_when_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_namespace_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_icon$1vertical$1card_url_title_rowChecker_resultRow_icon_cssClass_actionJspServletContext_actionJsp;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container_total_searchContainer_id;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_vertical$1card$1header;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_otherwise;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1text_colspan;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_vertical$1card$1sticker$1bottom;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1theme_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_horizontal$1card$1icon_icon_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1text;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_icon_message_markupView_image_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_html$1vertical$1card_url_title_rowChecker_resultRow_html_cssClass_actionJspServletContext_actionJsp;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name_cssClass;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_search$1container$1column$1image_toggleRowChecker_src_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_vertical$1card$1footer;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_aui_a_href = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_horizontal$1card_url_text_rowChecker_resultRow_actionJspServletContext_actionJsp = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1iterator_searchContainer_resultRowSplitter_markupView_displayStyle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_if_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_clay_icon_symbol_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1status_status_name_cssClass_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_vertical$1card_url_title_rowChecker_resultRow_imageUrl_cssClass_actionJspServletContext_actionJsp = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_className = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_horizontal$1card$1col = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_choose = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_colspan_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_buffer_var = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1icon_toggleRowChecker_icon_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_icon_message_markupView_image_cssClass_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_when_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_namespace_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_icon$1vertical$1card_url_title_rowChecker_resultRow_icon_cssClass_actionJspServletContext_actionJsp = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container_total_searchContainer_id = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_vertical$1card$1header = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_otherwise = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_colspan = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_vertical$1card$1sticker$1bottom = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1theme_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_horizontal$1card$1icon_icon_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_icon_message_markupView_image_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_html$1vertical$1card_url_title_rowChecker_resultRow_html_cssClass_actionJspServletContext_actionJsp = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name_cssClass = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_search$1container$1column$1image_toggleRowChecker_src_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_vertical$1card$1footer = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_aui_a_href.release();
    _jspx_tagPool_liferay$1frontend_horizontal$1card_url_text_rowChecker_resultRow_actionJspServletContext_actionJsp.release();
    _jspx_tagPool_liferay$1ui_search$1iterator_searchContainer_resultRowSplitter_markupView_displayStyle_nobody.release();
    _jspx_tagPool_c_if_test.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.release();
    _jspx_tagPool_clay_icon_symbol_nobody.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1status_status_name_cssClass_nobody.release();
    _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_nobody.release();
    _jspx_tagPool_liferay$1frontend_vertical$1card_url_title_rowChecker_resultRow_imageUrl_cssClass_actionJspServletContext_actionJsp.release();
    _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_className.release();
    _jspx_tagPool_liferay$1frontend_horizontal$1card$1col.release();
    _jspx_tagPool_c_choose.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_colspan_nobody.release();
    _jspx_tagPool_liferay$1util_buffer_var.release();
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1icon_toggleRowChecker_icon_nobody.release();
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.release();
    _jspx_tagPool_aui_icon_message_markupView_image_cssClass_nobody.release();
    _jspx_tagPool_c_when_test.release();
    _jspx_tagPool_portlet_namespace_nobody.release();
    _jspx_tagPool_liferay$1frontend_icon$1vertical$1card_url_title_rowChecker_resultRow_icon_cssClass_actionJspServletContext_actionJsp.release();
    _jspx_tagPool_liferay$1ui_search$1container_total_searchContainer_id.release();
    _jspx_tagPool_liferay$1frontend_vertical$1card$1header.release();
    _jspx_tagPool_c_otherwise.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_colspan.release();
    _jspx_tagPool_liferay$1frontend_vertical$1card$1sticker$1bottom.release();
    _jspx_tagPool_portlet_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1theme_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1frontend_horizontal$1card$1icon_icon_nobody.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text.release();
    _jspx_tagPool_aui_icon_message_markupView_image_nobody.release();
    _jspx_tagPool_liferay$1frontend_html$1vertical$1card_url_title_rowChecker_resultRow_html_cssClass_actionJspServletContext_actionJsp.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name_cssClass.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.release();
    _jspx_tagPool_liferay$1ui_search$1container$1column$1image_toggleRowChecker_src_nobody.release();
    _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.release();
    _jspx_tagPool_liferay$1frontend_vertical$1card$1footer.release();
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
      //  liferay-frontend:defineObjects
      com.liferay.frontend.taglib.servlet.taglib.DefineObjectsTag _jspx_th_liferay$1frontend_defineObjects_0 = (com.liferay.frontend.taglib.servlet.taglib.DefineObjectsTag) _jspx_tagPool_liferay$1frontend_defineObjects_nobody.get(com.liferay.frontend.taglib.servlet.taglib.DefineObjectsTag.class);
      _jspx_th_liferay$1frontend_defineObjects_0.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1frontend_defineObjects_0.setParent(null);
      int _jspx_eval_liferay$1frontend_defineObjects_0 = _jspx_th_liferay$1frontend_defineObjects_0.doStartTag();
      if (_jspx_th_liferay$1frontend_defineObjects_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_liferay$1frontend_defineObjects_nobody.reuse(_jspx_th_liferay$1frontend_defineObjects_0);
        return;
      }
      _jspx_tagPool_liferay$1frontend_defineObjects_nobody.reuse(_jspx_th_liferay$1frontend_defineObjects_0);
      java.lang.String currentURL = null;
      javax.portlet.PortletURL currentURLObj = null;
      java.lang.String npmResolvedPackageName = null;
      java.util.ResourceBundle resourceBundle = null;
      javax.portlet.WindowState windowState = null;
      currentURL = (java.lang.String) _jspx_page_context.findAttribute("currentURL");
      currentURLObj = (javax.portlet.PortletURL) _jspx_page_context.findAttribute("currentURLObj");
      npmResolvedPackageName = (java.lang.String) _jspx_page_context.findAttribute("npmResolvedPackageName");
      resourceBundle = (java.util.ResourceBundle) _jspx_page_context.findAttribute("resourceBundle");
      windowState = (javax.portlet.WindowState) _jspx_page_context.findAttribute("windowState");
      out.write('\n');
      out.write('\n');
      //  liferay-theme:defineObjects
      com.liferay.taglib.theme.DefineObjectsTag _jspx_th_liferay$1theme_defineObjects_0 = (com.liferay.taglib.theme.DefineObjectsTag) _jspx_tagPool_liferay$1theme_defineObjects_nobody.get(com.liferay.taglib.theme.DefineObjectsTag.class);
      _jspx_th_liferay$1theme_defineObjects_0.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1theme_defineObjects_0.setParent(null);
      int _jspx_eval_liferay$1theme_defineObjects_0 = _jspx_th_liferay$1theme_defineObjects_0.doStartTag();
      if (_jspx_th_liferay$1theme_defineObjects_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_liferay$1theme_defineObjects_nobody.reuse(_jspx_th_liferay$1theme_defineObjects_0);
        return;
      }
      _jspx_tagPool_liferay$1theme_defineObjects_nobody.reuse(_jspx_th_liferay$1theme_defineObjects_0);
      com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay = null;
      com.liferay.portal.kernel.model.Company company = null;
      com.liferay.portal.kernel.model.Account account = null;
      com.liferay.portal.kernel.model.User user = null;
      com.liferay.portal.kernel.model.User realUser = null;
      com.liferay.portal.kernel.model.Contact contact = null;
      com.liferay.portal.kernel.model.Layout layout = null;
      java.util.List layouts = null;
      java.lang.Long plid = null;
      com.liferay.portal.kernel.model.LayoutTypePortlet layoutTypePortlet = null;
      java.lang.Long scopeGroupId = null;
      com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker = null;
      java.util.Locale locale = null;
      java.util.TimeZone timeZone = null;
      com.liferay.portal.kernel.model.Theme theme = null;
      com.liferay.portal.kernel.model.ColorScheme colorScheme = null;
      com.liferay.portal.kernel.theme.PortletDisplay portletDisplay = null;
      java.lang.Long portletGroupId = null;
      themeDisplay = (com.liferay.portal.kernel.theme.ThemeDisplay) _jspx_page_context.findAttribute("themeDisplay");
      company = (com.liferay.portal.kernel.model.Company) _jspx_page_context.findAttribute("company");
      account = (com.liferay.portal.kernel.model.Account) _jspx_page_context.findAttribute("account");
      user = (com.liferay.portal.kernel.model.User) _jspx_page_context.findAttribute("user");
      realUser = (com.liferay.portal.kernel.model.User) _jspx_page_context.findAttribute("realUser");
      contact = (com.liferay.portal.kernel.model.Contact) _jspx_page_context.findAttribute("contact");
      layout = (com.liferay.portal.kernel.model.Layout) _jspx_page_context.findAttribute("layout");
      layouts = (java.util.List) _jspx_page_context.findAttribute("layouts");
      plid = (java.lang.Long) _jspx_page_context.findAttribute("plid");
      layoutTypePortlet = (com.liferay.portal.kernel.model.LayoutTypePortlet) _jspx_page_context.findAttribute("layoutTypePortlet");
      scopeGroupId = (java.lang.Long) _jspx_page_context.findAttribute("scopeGroupId");
      permissionChecker = (com.liferay.portal.kernel.security.permission.PermissionChecker) _jspx_page_context.findAttribute("permissionChecker");
      locale = (java.util.Locale) _jspx_page_context.findAttribute("locale");
      timeZone = (java.util.TimeZone) _jspx_page_context.findAttribute("timeZone");
      theme = (com.liferay.portal.kernel.model.Theme) _jspx_page_context.findAttribute("theme");
      colorScheme = (com.liferay.portal.kernel.model.ColorScheme) _jspx_page_context.findAttribute("colorScheme");
      portletDisplay = (com.liferay.portal.kernel.theme.PortletDisplay) _jspx_page_context.findAttribute("portletDisplay");
      portletGroupId = (java.lang.Long) _jspx_page_context.findAttribute("portletGroupId");
      out.write('\n');
      out.write('\n');
      //  portlet:defineObjects
      com.liferay.taglib.portlet.DefineObjectsTag _jspx_th_portlet_defineObjects_0 = (com.liferay.taglib.portlet.DefineObjectsTag) _jspx_tagPool_portlet_defineObjects_nobody.get(com.liferay.taglib.portlet.DefineObjectsTag.class);
      _jspx_th_portlet_defineObjects_0.setPageContext(_jspx_page_context);
      _jspx_th_portlet_defineObjects_0.setParent(null);
      int _jspx_eval_portlet_defineObjects_0 = _jspx_th_portlet_defineObjects_0.doStartTag();
      if (_jspx_th_portlet_defineObjects_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_portlet_defineObjects_nobody.reuse(_jspx_th_portlet_defineObjects_0);
        return;
      }
      _jspx_tagPool_portlet_defineObjects_nobody.reuse(_jspx_th_portlet_defineObjects_0);
      javax.portlet.ActionRequest actionRequest = null;
      javax.portlet.ActionResponse actionResponse = null;
      javax.portlet.EventRequest eventRequest = null;
      javax.portlet.EventResponse eventResponse = null;
      com.liferay.portal.kernel.portlet.LiferayPortletRequest liferayPortletRequest = null;
      com.liferay.portal.kernel.portlet.LiferayPortletResponse liferayPortletResponse = null;
      javax.portlet.PortletConfig portletConfig = null;
      java.lang.String portletName = null;
      javax.portlet.PortletPreferences portletPreferences = null;
      java.util.Map portletPreferencesValues = null;
      javax.portlet.PortletSession portletSession = null;
      java.util.Map portletSessionScope = null;
      javax.portlet.RenderRequest renderRequest = null;
      javax.portlet.RenderResponse renderResponse = null;
      javax.portlet.ResourceRequest resourceRequest = null;
      javax.portlet.ResourceResponse resourceResponse = null;
      actionRequest = (javax.portlet.ActionRequest) _jspx_page_context.findAttribute("actionRequest");
      actionResponse = (javax.portlet.ActionResponse) _jspx_page_context.findAttribute("actionResponse");
      eventRequest = (javax.portlet.EventRequest) _jspx_page_context.findAttribute("eventRequest");
      eventResponse = (javax.portlet.EventResponse) _jspx_page_context.findAttribute("eventResponse");
      liferayPortletRequest = (com.liferay.portal.kernel.portlet.LiferayPortletRequest) _jspx_page_context.findAttribute("liferayPortletRequest");
      liferayPortletResponse = (com.liferay.portal.kernel.portlet.LiferayPortletResponse) _jspx_page_context.findAttribute("liferayPortletResponse");
      portletConfig = (javax.portlet.PortletConfig) _jspx_page_context.findAttribute("portletConfig");
      portletName = (java.lang.String) _jspx_page_context.findAttribute("portletName");
      portletPreferences = (javax.portlet.PortletPreferences) _jspx_page_context.findAttribute("portletPreferences");
      portletPreferencesValues = (java.util.Map) _jspx_page_context.findAttribute("portletPreferencesValues");
      portletSession = (javax.portlet.PortletSession) _jspx_page_context.findAttribute("portletSession");
      portletSessionScope = (java.util.Map) _jspx_page_context.findAttribute("portletSessionScope");
      renderRequest = (javax.portlet.RenderRequest) _jspx_page_context.findAttribute("renderRequest");
      renderResponse = (javax.portlet.RenderResponse) _jspx_page_context.findAttribute("renderResponse");
      resourceRequest = (javax.portlet.ResourceRequest) _jspx_page_context.findAttribute("resourceRequest");
      resourceResponse = (javax.portlet.ResourceResponse) _jspx_page_context.findAttribute("resourceResponse");
      out.write('\n');
      out.write('\n');

DLTrashUtil dlTrashUtil = (DLTrashUtil)request.getAttribute(DLWebKeys.DOCUMENT_LIBRARY_TRASH_UTIL);

DLWebComponentProvider dlWebComponentProvider = DLWebComponentProvider.getDLWebComponentProvider();

DLAdminDisplayContextProvider dlAdminDisplayContextProvider = dlWebComponentProvider.getDLAdminDisplayContextProvider();
DLDisplayContextProvider dlDisplayContextProvider = dlWebComponentProvider.getDLDisplayContextProvider();
IGDisplayContextProvider igDisplayContextProvider = dlWebComponentProvider.getIGDisplayContextProvider();

Format dateFormatDate = FastDateFormatFactoryUtil.getDate(locale, timeZone);
Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);

      out.write('\n');
      out.write('\n');
      out.write("\n\n\n\n\n\n");

DLRequestHelper dlRequestHelper = new DLRequestHelper(request);

String portletId = dlRequestHelper.getResourcePortletId();

portletName = dlRequestHelper.getResourcePortletName();

String portletResource = dlRequestHelper.getPortletResource();

DLAdminDisplayContext dlAdminDisplayContext = dlAdminDisplayContextProvider.getDLAdminDisplayContext(request, response);

DLAdminManagementToolbarDisplayContext dlAdminManagementToolbarDisplayContext = dlAdminDisplayContextProvider.getDLAdminManagementToolbarDisplayContext(request, response);

DLConfiguration dlConfiguration = ConfigurationProviderUtil.getSystemConfiguration(DLConfiguration.class);
DLGroupServiceSettings dlGroupServiceSettings = dlRequestHelper.getDLGroupServiceSettings();
DLPortletInstanceSettings dlPortletInstanceSettings = dlRequestHelper.getDLPortletInstanceSettings();

long rootFolderId = dlAdminDisplayContext.getRootFolderId();
String rootFolderName = dlAdminDisplayContext.getRootFolderName();

boolean showComments = ParamUtil.getBoolean(request, "showComments", true);
boolean showHeader = ParamUtil.getBoolean(request, "showHeader", true);

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');

String navigation = ParamUtil.getString(request, "navigation", "home");

String currentFolder = ParamUtil.getString(request, "curFolder");
String deltaFolder = ParamUtil.getString(request, "deltaFolder");

long folderId = GetterUtil.getLong((String)request.getAttribute("view.jsp-folderId"));

long repositoryId = GetterUtil.getLong((String)request.getAttribute("view.jsp-repositoryId"));

DLPortletInstanceSettingsHelper dlPortletInstanceSettingsHelper = new DLPortletInstanceSettingsHelper(dlRequestHelper);

String displayStyle = GetterUtil.getString((String)request.getAttribute("view.jsp-displayStyle"));

PortletURL portletURL = liferayPortletResponse.createRenderURL();

portletURL.setParameter("mvcRenderCommandName", (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) ? "/document_library/view" : "/document_library/view_folder");
portletURL.setParameter("navigation", navigation);
portletURL.setParameter("curFolder", currentFolder);
portletURL.setParameter("deltaFolder", deltaFolder);
portletURL.setParameter("folderId", String.valueOf(folderId));

EntriesChecker entriesChecker = new EntriesChecker(liferayPortletRequest, liferayPortletResponse);

entriesChecker.setCssClass("entry-selector");

entriesChecker.setRememberCheckBoxStateURLRegex(dlAdminDisplayContext.getRememberCheckBoxStateURLRegex());

EntriesMover entriesMover = new EntriesMover(dlTrashUtil.isTrashEnabled(scopeGroupId, repositoryId));

String[] entryColumns = dlPortletInstanceSettingsHelper.getEntryColumns();

boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation"));

if (portletTitleBasedNavigation && (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) && (folderId != rootFolderId)) {
	String redirect = ParamUtil.getString(request, "redirect");

	if (Validator.isNotNull(redirect)) {
		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(redirect);
	}

	Folder folder = DLAppServiceUtil.getFolder(folderId);

	renderResponse.setTitle(folder.getName());
}

      out.write("\n\n<div class=\"document-container\" id=\"");
      if (_jspx_meth_portlet_namespace_0(_jspx_page_context))
        return;
      out.write("entriesContainer\">\n\n\t");

	SearchContainer dlSearchContainer = dlAdminDisplayContext.getSearchContainer();
	
      out.write("\n\n\t");
      //  liferay-ui:search-container
      com.liferay.taglib.ui.SearchContainerTag _jspx_th_liferay$1ui_search$1container_0 = (com.liferay.taglib.ui.SearchContainerTag) _jspx_tagPool_liferay$1ui_search$1container_total_searchContainer_id.get(com.liferay.taglib.ui.SearchContainerTag.class);
      _jspx_th_liferay$1ui_search$1container_0.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1ui_search$1container_0.setParent(null);
      _jspx_th_liferay$1ui_search$1container_0.setId("entries");
      _jspx_th_liferay$1ui_search$1container_0.setSearchContainer( dlSearchContainer );
      _jspx_th_liferay$1ui_search$1container_0.setTotal( dlSearchContainer.getTotal() );
      int _jspx_eval_liferay$1ui_search$1container_0 = _jspx_th_liferay$1ui_search$1container_0.doStartTag();
      if (_jspx_eval_liferay$1ui_search$1container_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        java.lang.Integer total = null;
        com.liferay.portal.kernel.dao.search.SearchContainer searchContainer = null;
        total = (java.lang.Integer) _jspx_page_context.findAttribute("total");
        searchContainer = (com.liferay.portal.kernel.dao.search.SearchContainer) _jspx_page_context.findAttribute("searchContainer");
        out.write("\n\t\t");
        //  liferay-ui:search-container-row
        com.liferay.taglib.ui.SearchContainerRowTag _jspx_th_liferay$1ui_search$1container$1row_0 = (com.liferay.taglib.ui.SearchContainerRowTag) _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_className.get(com.liferay.taglib.ui.SearchContainerRowTag.class);
        _jspx_th_liferay$1ui_search$1container$1row_0.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_search$1container$1row_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container_0);
        _jspx_th_liferay$1ui_search$1container$1row_0.setClassName("Object");
        _jspx_th_liferay$1ui_search$1container$1row_0.setModelVar("result");
        int _jspx_eval_liferay$1ui_search$1container$1row_0 = _jspx_th_liferay$1ui_search$1container$1row_0.doStartTag();
        if (_jspx_eval_liferay$1ui_search$1container$1row_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          java.lang.Integer index = null;
          Object result = null;
          com.liferay.portal.kernel.dao.search.ResultRow row = null;
          if (_jspx_eval_liferay$1ui_search$1container$1row_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_liferay$1ui_search$1container$1row_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_liferay$1ui_search$1container$1row_0.doInitBody();
          }
          index = (java.lang.Integer) _jspx_page_context.findAttribute("index");
          result = (Object) _jspx_page_context.findAttribute("result");
          row = (com.liferay.portal.kernel.dao.search.ResultRow) _jspx_page_context.findAttribute("row");
          do {
            out.write("\n\t\t\t");
            out.write('\n');
            out.write('\n');

Folder curFolder = null;
FileEntry fileEntry = null;
FileShortcut fileShortcut = null;

if (result instanceof AssetEntry) {
	AssetEntry assetEntry = (AssetEntry)result;

	if (Objects.equals(assetEntry.getClassName(), DLFileEntryConstants.getClassName())) {
		fileEntry = DLAppLocalServiceUtil.getFileEntry(assetEntry.getClassPK());

		fileEntry = fileEntry.toEscapedModel();
	}
	else {
		fileShortcut = DLAppLocalServiceUtil.getFileShortcut(assetEntry.getClassPK());

		fileShortcut = fileShortcut.toEscapedModel();

		fileEntry = DLAppLocalServiceUtil.getFileEntry(fileShortcut.getToFileEntryId());

		fileEntry = fileEntry.toEscapedModel();
	}
}
else if (result instanceof FileEntry) {
	fileEntry = (FileEntry)result;

	fileEntry = fileEntry.toEscapedModel();
}
else if (result instanceof FileShortcut) {
	fileShortcut = (FileShortcut)result;

	fileShortcut = fileShortcut.toEscapedModel();

	fileEntry = DLAppLocalServiceUtil.getFileEntry(fileShortcut.getToFileEntryId());

	fileEntry = fileEntry.toEscapedModel();
}
else if (result instanceof Folder) {
	curFolder = (Folder)result;
}

            out.write("\n\n\t\t\t");
            //  c:choose
            org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_0 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
            _jspx_th_c_choose_0.setPageContext(_jspx_page_context);
            _jspx_th_c_choose_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1row_0);
            int _jspx_eval_c_choose_0 = _jspx_th_c_choose_0.doStartTag();
            if (_jspx_eval_c_choose_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              do {
                out.write("\n\t\t\t\t");
                //  c:when
                org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_0 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                _jspx_th_c_when_0.setPageContext(_jspx_page_context);
                _jspx_th_c_when_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
                _jspx_th_c_when_0.setTest( fileEntry != null );
                int _jspx_eval_c_when_0 = _jspx_th_c_when_0.doStartTag();
                if (_jspx_eval_c_when_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\n\t\t\t\t\t");

					boolean draggable = false;

					if (!BrowserSnifferUtil.isMobile(request) && (DLFileEntryPermission.contains(permissionChecker, fileEntry, ActionKeys.DELETE) || DLFileEntryPermission.contains(permissionChecker, fileEntry, ActionKeys.UPDATE))) {
						draggable = true;

						if (dlSearchContainer.getRowMover() == null) {
							dlSearchContainer.setRowMover(entriesMover);
						}
					}

					if (dlSearchContainer.getRowChecker() == null) {
						dlSearchContainer.setRowChecker(entriesChecker);
					}

					Map<String, Object> rowData = new HashMap<String, Object>();

					rowData.put("actions", StringUtil.merge(dlAdminManagementToolbarDisplayContext.getAvailableActions(fileEntry)));
					rowData.put("draggable", draggable);
					rowData.put("title", fileEntry.getTitle());

					row.setData(rowData);

					DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext = null;

					if (fileShortcut == null) {
						dlViewFileVersionDisplayContext = dlDisplayContextProvider.getDLViewFileVersionDisplayContext(request, response, fileEntry.getFileVersion());

						row.setPrimaryKey(String.valueOf(fileEntry.getFileEntryId()));
					}
					else {
						dlViewFileVersionDisplayContext = dlDisplayContextProvider.getDLViewFileVersionDisplayContext(request, response, fileShortcut);

						row.setPrimaryKey(String.valueOf(fileShortcut.getFileShortcutId()));
					}

					FileVersion latestFileVersion = fileEntry.getFileVersion();

					if ((user.getUserId() == fileEntry.getUserId()) || permissionChecker.isContentReviewer(user.getCompanyId(), scopeGroupId) || DLFileEntryPermission.contains(permissionChecker, fileEntry, ActionKeys.UPDATE)) {
						latestFileVersion = fileEntry.getLatestFileVersion();
					}

					latestFileVersion = latestFileVersion.toEscapedModel();

					String thumbnailSrc = DLURLHelperUtil.getThumbnailSrc(fileEntry, latestFileVersion, themeDisplay);
					
                    out.write("\n\n\t\t\t\t\t");
                    //  c:choose
                    org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_1 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                    _jspx_th_c_choose_1.setPageContext(_jspx_page_context);
                    _jspx_th_c_choose_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
                    int _jspx_eval_c_choose_1 = _jspx_th_c_choose_1.doStartTag();
                    if (_jspx_eval_c_choose_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      do {
                        out.write("\n\t\t\t\t\t\t");
                        //  c:when
                        org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_1 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                        _jspx_th_c_when_1.setPageContext(_jspx_page_context);
                        _jspx_th_c_when_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                        _jspx_th_c_when_1.setTest( displayStyle.equals("descriptive") );
                        int _jspx_eval_c_when_1 = _jspx_th_c_when_1.doStartTag();
                        if (_jspx_eval_c_when_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                          do {
                            out.write("\n\t\t\t\t\t\t\t");
                            //  c:choose
                            org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_2 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                            _jspx_th_c_choose_2.setPageContext(_jspx_page_context);
                            _jspx_th_c_choose_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
                            int _jspx_eval_c_choose_2 = _jspx_th_c_choose_2.doStartTag();
                            if (_jspx_eval_c_choose_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_2 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_2.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_2);
                              _jspx_th_c_when_2.setTest( Validator.isNotNull(thumbnailSrc) );
                              int _jspx_eval_c_when_2 = _jspx_th_c_when_2.doStartTag();
                              if (_jspx_eval_c_when_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-image
                              com.liferay.taglib.ui.SearchContainerColumnImageTag _jspx_th_liferay$1ui_search$1container$1column$1image_0 = (com.liferay.taglib.ui.SearchContainerColumnImageTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1image_toggleRowChecker_src_nobody.get(com.liferay.taglib.ui.SearchContainerColumnImageTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1image_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1image_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_2);
                              _jspx_th_liferay$1ui_search$1container$1column$1image_0.setSrc( thumbnailSrc );
                              _jspx_th_liferay$1ui_search$1container$1column$1image_0.setToggleRowChecker( true );
                              int _jspx_eval_liferay$1ui_search$1container$1column$1image_0 = _jspx_th_liferay$1ui_search$1container$1column$1image_0.doStartTag();
                              if (_jspx_th_liferay$1ui_search$1container$1column$1image_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1image_toggleRowChecker_src_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1image_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1image_toggleRowChecker_src_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1image_0);
                              out.write("\n\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_2.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_2);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_2);
                              out.write("\n\t\t\t\t\t\t\t\t");
                              //  c:otherwise
                              org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_0 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                              _jspx_th_c_otherwise_0.setPageContext(_jspx_page_context);
                              _jspx_th_c_otherwise_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_2);
                              int _jspx_eval_c_otherwise_0 = _jspx_th_c_otherwise_0.doStartTag();
                              if (_jspx_eval_c_otherwise_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-text
                              com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_0 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
                              int _jspx_eval_liferay$1ui_search$1container$1column$1text_0 = _jspx_th_liferay$1ui_search$1container$1column$1text_0.doStartTag();
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1ui_search$1container$1column$1text_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_0.doInitBody();
                              }
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-document-library:mime-type-sticker
                              com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag _jspx_th_liferay$1document$1library_mime$1type$1sticker_0 = (com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag) _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.get(com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag.class);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1column$1text_0);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_0.setCssClass("sticker-secondary");
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_0.setFileVersion( latestFileVersion );
                              int _jspx_eval_liferay$1document$1library_mime$1type$1sticker_0 = _jspx_th_liferay$1document$1library_mime$1type$1sticker_0.doStartTag();
                              if (_jspx_th_liferay$1document$1library_mime$1type$1sticker_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.reuse(_jspx_th_liferay$1document$1library_mime$1type$1sticker_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.reuse(_jspx_th_liferay$1document$1library_mime$1type$1sticker_0);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_liferay$1ui_search$1container$1column$1text_0.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1ui_search$1container$1column$1text_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_0);
                              out.write("\n\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_otherwise_0.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_otherwise_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_0);
                              return;
                              }
                              _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_0);
                              out.write("\n\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_choose_2.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                            }
                            if (_jspx_th_c_choose_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_2);
                              return;
                            }
                            _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_2);
                            out.write("\n\n\t\t\t\t\t\t\t");
                            //  liferay-ui:search-container-column-jsp
                            com.liferay.taglib.ui.SearchContainerColumnJSPTag _jspx_th_liferay$1ui_search$1container$1column$1jsp_0 = (com.liferay.taglib.ui.SearchContainerColumnJSPTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_colspan_nobody.get(com.liferay.taglib.ui.SearchContainerColumnJSPTag.class);
                            _jspx_th_liferay$1ui_search$1container$1column$1jsp_0.setPageContext(_jspx_page_context);
                            _jspx_th_liferay$1ui_search$1container$1column$1jsp_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
                            _jspx_th_liferay$1ui_search$1container$1column$1jsp_0.setColspan( 2 );
                            _jspx_th_liferay$1ui_search$1container$1column$1jsp_0.setPath("/document_library/view_file_entry_descriptive.jsp");
                            int _jspx_eval_liferay$1ui_search$1container$1column$1jsp_0 = _jspx_th_liferay$1ui_search$1container$1column$1jsp_0.doStartTag();
                            if (_jspx_th_liferay$1ui_search$1container$1column$1jsp_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_colspan_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1jsp_0);
                              return;
                            }
                            _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_colspan_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1jsp_0);
                            out.write("\n\n\t\t\t\t\t\t\t");
                            if (_jspx_meth_liferay$1ui_search$1container$1column$1jsp_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_1, _jspx_page_context))
                              return;
                            out.write("\n\t\t\t\t\t\t");
                            int evalDoAfterBody = _jspx_th_c_when_1.doAfterBody();
                            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                          } while (true);
                        }
                        if (_jspx_th_c_when_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_1);
                          return;
                        }
                        _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_1);
                        out.write("\n\t\t\t\t\t\t");
                        //  c:when
                        org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_3 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                        _jspx_th_c_when_3.setPageContext(_jspx_page_context);
                        _jspx_th_c_when_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                        _jspx_th_c_when_3.setTest( displayStyle.equals("icon") );
                        int _jspx_eval_c_when_3 = _jspx_th_c_when_3.doStartTag();
                        if (_jspx_eval_c_when_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                          do {
                            out.write("\n\n\t\t\t\t\t\t\t");

							row.setCssClass("entry-card lfr-asset-item");
							
                            out.write("\n\n\t\t\t\t\t\t\t");
                            //  liferay-ui:search-container-column-text
                            com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_1 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                            _jspx_th_liferay$1ui_search$1container$1column$1text_1.setPageContext(_jspx_page_context);
                            _jspx_th_liferay$1ui_search$1container$1column$1text_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_3);
                            int _jspx_eval_liferay$1ui_search$1container$1column$1text_1 = _jspx_th_liferay$1ui_search$1container$1column$1text_1.doStartTag();
                            if (_jspx_eval_liferay$1ui_search$1container$1column$1text_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1ui_search$1container$1column$1text_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_1.doInitBody();
                              }
                              do {
                              out.write("\n\n\t\t\t\t\t\t\t\t");

								PortletURL rowURL = liferayPortletResponse.createRenderURL();

								rowURL.setParameter("mvcRenderCommandName", "/document_library/view_file_entry");
								rowURL.setParameter("redirect", HttpUtil.removeParameter(currentURL, liferayPortletResponse.getNamespace() + "ajax"));
								rowURL.setParameter("fileEntryId", String.valueOf(fileEntry.getFileEntryId()));
								
                              out.write("\n\n\t\t\t\t\t\t\t\t");
                              //  c:choose
                              org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_3 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                              _jspx_th_c_choose_3.setPageContext(_jspx_page_context);
                              _jspx_th_c_choose_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1column$1text_1);
                              int _jspx_eval_c_choose_3 = _jspx_th_c_choose_3.doStartTag();
                              if (_jspx_eval_c_choose_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_4 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_4.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_3);
                              _jspx_th_c_when_4.setTest( dlViewFileVersionDisplayContext.hasCustomThumbnail() );
                              int _jspx_eval_c_when_4 = _jspx_th_c_when_4.doStartTag();
                              if (_jspx_eval_c_when_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-util:buffer
                              com.liferay.taglib.util.BufferTag _jspx_th_liferay$1util_buffer_0 = (com.liferay.taglib.util.BufferTag) _jspx_tagPool_liferay$1util_buffer_var.get(com.liferay.taglib.util.BufferTag.class);
                              _jspx_th_liferay$1util_buffer_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1util_buffer_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_4);
                              _jspx_th_liferay$1util_buffer_0.setVar("customThumbnailHtml");
                              int _jspx_eval_liferay$1util_buffer_0 = _jspx_th_liferay$1util_buffer_0.doStartTag();
                              if (_jspx_eval_liferay$1util_buffer_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1util_buffer_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1util_buffer_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1util_buffer_0.doInitBody();
                              }
                              do {
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t");

											dlViewFileVersionDisplayContext.renderCustomThumbnail(request, PipingServletResponse.createPipingServletResponse(pageContext));
											
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_liferay$1util_buffer_0.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1util_buffer_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1util_buffer_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1util_buffer_var.reuse(_jspx_th_liferay$1util_buffer_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1util_buffer_var.reuse(_jspx_th_liferay$1util_buffer_0);
                              java.lang.String customThumbnailHtml = null;
                              customThumbnailHtml = (java.lang.String) _jspx_page_context.findAttribute("customThumbnailHtml");
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-frontend:html-vertical-card
                              com.liferay.frontend.taglib.servlet.taglib.HtmlVerticalCardTag _jspx_th_liferay$1frontend_html$1vertical$1card_0 = (com.liferay.frontend.taglib.servlet.taglib.HtmlVerticalCardTag) _jspx_tagPool_liferay$1frontend_html$1vertical$1card_url_title_rowChecker_resultRow_html_cssClass_actionJspServletContext_actionJsp.get(com.liferay.frontend.taglib.servlet.taglib.HtmlVerticalCardTag.class);
                              _jspx_th_liferay$1frontend_html$1vertical$1card_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_html$1vertical$1card_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_4);
                              _jspx_th_liferay$1frontend_html$1vertical$1card_0.setActionJsp("/document_library/file_entry_action.jsp");
                              _jspx_th_liferay$1frontend_html$1vertical$1card_0.setActionJspServletContext( application );
                              _jspx_th_liferay$1frontend_html$1vertical$1card_0.setCssClass("entry-display-style file-card");
                              _jspx_th_liferay$1frontend_html$1vertical$1card_0.setHtml( customThumbnailHtml );
                              _jspx_th_liferay$1frontend_html$1vertical$1card_0.setResultRow( row );
                              _jspx_th_liferay$1frontend_html$1vertical$1card_0.setRowChecker( entriesChecker );
                              _jspx_th_liferay$1frontend_html$1vertical$1card_0.setTitle( latestFileVersion.getTitle() );
                              _jspx_th_liferay$1frontend_html$1vertical$1card_0.setUrl( (rowURL != null) ? rowURL.toString() : null );
                              int _jspx_eval_liferay$1frontend_html$1vertical$1card_0 = _jspx_th_liferay$1frontend_html$1vertical$1card_0.doStartTag();
                              if (_jspx_eval_liferay$1frontend_html$1vertical$1card_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                              out.write('\n');
                              out.write('\n');
                              //  liferay-frontend:vertical-card-sticker-bottom
                              com.liferay.frontend.taglib.servlet.taglib.VerticalCardStickerBottomTag _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_0 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardStickerBottomTag) _jspx_tagPool_liferay$1frontend_vertical$1card$1sticker$1bottom.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardStickerBottomTag.class);
                              _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_html$1vertical$1card_0);
                              int _jspx_eval_liferay$1frontend_vertical$1card$1sticker$1bottom_0 = _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_0.doStartTag();
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1sticker$1bottom_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1sticker$1bottom_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_0.doInitBody();
                              }
                              do {
                              out.write('\n');
                              out.write('	');
                              //  liferay-document-library:mime-type-sticker
                              com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag _jspx_th_liferay$1document$1library_mime$1type$1sticker_1 = (com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag) _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.get(com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag.class);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_1.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_0);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_1.setCssClass("sticker-bottom");
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_1.setFileVersion( latestFileVersion );
                              int _jspx_eval_liferay$1document$1library_mime$1type$1sticker_1 = _jspx_th_liferay$1document$1library_mime$1type$1sticker_1.doStartTag();
                              if (_jspx_th_liferay$1document$1library_mime$1type$1sticker_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.reuse(_jspx_th_liferay$1document$1library_mime$1type$1sticker_1);
                              return;
                              }
                              _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.reuse(_jspx_th_liferay$1document$1library_mime$1type$1sticker_1);
                              out.write('\n');
                              int evalDoAfterBody = _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_0.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1sticker$1bottom_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1sticker$1bottom.reuse(_jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1sticker$1bottom.reuse(_jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_0);
                              out.write('\n');
                              out.write('\n');
                              //  liferay-frontend:vertical-card-header
                              com.liferay.frontend.taglib.servlet.taglib.VerticalCardHeaderTag _jspx_th_liferay$1frontend_vertical$1card$1header_0 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardHeaderTag) _jspx_tagPool_liferay$1frontend_vertical$1card$1header.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardHeaderTag.class);
                              _jspx_th_liferay$1frontend_vertical$1card$1header_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_vertical$1card$1header_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_html$1vertical$1card_0);
                              int _jspx_eval_liferay$1frontend_vertical$1card$1header_0 = _jspx_th_liferay$1frontend_vertical$1card$1header_0.doStartTag();
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1header_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1header_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1frontend_vertical$1card$1header_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1frontend_vertical$1card$1header_0.doInitBody();
                              }
                              do {
                              out.write('\n');
                              out.write('	');
                              out.print( LanguageUtil.format(request, "x-ago-by-x", new String[] {LanguageUtil.getTimeDescription(locale, System.currentTimeMillis() - latestFileVersion.getCreateDate().getTime(), true), HtmlUtil.escape(latestFileVersion.getUserName())}, false) );
                              out.write('\n');
                              int evalDoAfterBody = _jspx_th_liferay$1frontend_vertical$1card$1header_0.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1header_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1frontend_vertical$1card$1header_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1header.reuse(_jspx_th_liferay$1frontend_vertical$1card$1header_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1header.reuse(_jspx_th_liferay$1frontend_vertical$1card$1header_0);
                              out.write('\n');
                              out.write('\n');
                              //  liferay-frontend:vertical-card-footer
                              com.liferay.frontend.taglib.servlet.taglib.VerticalCardFooterTag _jspx_th_liferay$1frontend_vertical$1card$1footer_0 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardFooterTag) _jspx_tagPool_liferay$1frontend_vertical$1card$1footer.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardFooterTag.class);
                              _jspx_th_liferay$1frontend_vertical$1card$1footer_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_vertical$1card$1footer_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_html$1vertical$1card_0);
                              int _jspx_eval_liferay$1frontend_vertical$1card$1footer_0 = _jspx_th_liferay$1frontend_vertical$1card$1footer_0.doStartTag();
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1footer_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1footer_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1frontend_vertical$1card$1footer_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1frontend_vertical$1card$1footer_0.doInitBody();
                              }
                              do {
                              out.write('\n');
                              out.write('	');
                              //  aui:workflow-status
                              com.liferay.taglib.aui.WorkflowStatusTag _jspx_th_aui_workflow$1status_0 = (com.liferay.taglib.aui.WorkflowStatusTag) _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_nobody.get(com.liferay.taglib.aui.WorkflowStatusTag.class);
                              _jspx_th_aui_workflow$1status_0.setPageContext(_jspx_page_context);
                              _jspx_th_aui_workflow$1status_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card$1footer_0);
                              _jspx_th_aui_workflow$1status_0.setShowIcon( false );
                              _jspx_th_aui_workflow$1status_0.setShowLabel( false );
                              _jspx_th_aui_workflow$1status_0.setStatus( latestFileVersion.getStatus() );
                              int _jspx_eval_aui_workflow$1status_0 = _jspx_th_aui_workflow$1status_0.doStartTag();
                              if (_jspx_th_aui_workflow$1status_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_nobody.reuse(_jspx_th_aui_workflow$1status_0);
                              return;
                              }
                              _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_nobody.reuse(_jspx_th_aui_workflow$1status_0);
                              out.write("\n\n\t");
                              //  c:choose
                              org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_4 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                              _jspx_th_c_choose_4.setPageContext(_jspx_page_context);
                              _jspx_th_c_choose_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card$1footer_0);
                              int _jspx_eval_c_choose_4 = _jspx_th_c_choose_4.doStartTag();
                              if (_jspx_eval_c_choose_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_5 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_5.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_4);
                              _jspx_th_c_when_5.setTest( fileShortcut != null );
                              int _jspx_eval_c_when_5 = _jspx_th_c_when_5.doStartTag();
                              if (_jspx_eval_c_when_5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t<span class=\"file-icon-color-0\">\n\t\t\t\t");
                              if (_jspx_meth_aui_icon_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_5, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t</span>\n\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_5.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_5);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_5);
                              out.write("\n\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_6 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_6.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_4);
                              _jspx_th_c_when_6.setTest( fileEntry.hasLock() || fileEntry.isCheckedOut() );
                              int _jspx_eval_c_when_6 = _jspx_th_c_when_6.doStartTag();
                              if (_jspx_eval_c_when_6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t<span class=\"file-icon-color-0\">\n\t\t\t\t");
                              if (_jspx_meth_aui_icon_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_6, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t</span>\n\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_6.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_6);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_6);
                              out.write('\n');
                              out.write('	');
                              int evalDoAfterBody = _jspx_th_c_choose_4.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_choose_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_4);
                              return;
                              }
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_4);
                              out.write('\n');
                              int evalDoAfterBody = _jspx_th_liferay$1frontend_vertical$1card$1footer_0.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1footer_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1frontend_vertical$1card$1footer_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1footer.reuse(_jspx_th_liferay$1frontend_vertical$1card$1footer_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1footer.reuse(_jspx_th_liferay$1frontend_vertical$1card$1footer_0);
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              }
                              if (_jspx_th_liferay$1frontend_html$1vertical$1card_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_html$1vertical$1card_url_title_rowChecker_resultRow_html_cssClass_actionJspServletContext_actionJsp.reuse(_jspx_th_liferay$1frontend_html$1vertical$1card_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_html$1vertical$1card_url_title_rowChecker_resultRow_html_cssClass_actionJspServletContext_actionJsp.reuse(_jspx_th_liferay$1frontend_html$1vertical$1card_0);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_4.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_4);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_4);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_7 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_7.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_3);
                              _jspx_th_c_when_7.setTest( Validator.isNull(thumbnailSrc) );
                              int _jspx_eval_c_when_7 = _jspx_th_c_when_7.doStartTag();
                              if (_jspx_eval_c_when_7 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-frontend:icon-vertical-card
                              com.liferay.frontend.taglib.servlet.taglib.IconVerticalCardTag _jspx_th_liferay$1frontend_icon$1vertical$1card_0 = (com.liferay.frontend.taglib.servlet.taglib.IconVerticalCardTag) _jspx_tagPool_liferay$1frontend_icon$1vertical$1card_url_title_rowChecker_resultRow_icon_cssClass_actionJspServletContext_actionJsp.get(com.liferay.frontend.taglib.servlet.taglib.IconVerticalCardTag.class);
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_7);
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setActionJsp("/document_library/file_entry_action.jsp");
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setActionJspServletContext( application );
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setCssClass("entry-display-style file-card");
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setIcon("documents-and-media");
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setResultRow( row );
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setRowChecker( entriesChecker );
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setTitle( latestFileVersion.getTitle() );
                              _jspx_th_liferay$1frontend_icon$1vertical$1card_0.setUrl( (rowURL != null) ? rowURL.toString() : null );
                              int _jspx_eval_liferay$1frontend_icon$1vertical$1card_0 = _jspx_th_liferay$1frontend_icon$1vertical$1card_0.doStartTag();
                              if (_jspx_eval_liferay$1frontend_icon$1vertical$1card_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                              out.write('\n');
                              out.write('\n');
                              //  liferay-frontend:vertical-card-sticker-bottom
                              com.liferay.frontend.taglib.servlet.taglib.VerticalCardStickerBottomTag _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_1 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardStickerBottomTag) _jspx_tagPool_liferay$1frontend_vertical$1card$1sticker$1bottom.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardStickerBottomTag.class);
                              _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_1.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_icon$1vertical$1card_0);
                              int _jspx_eval_liferay$1frontend_vertical$1card$1sticker$1bottom_1 = _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_1.doStartTag();
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1sticker$1bottom_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1sticker$1bottom_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_1.doInitBody();
                              }
                              do {
                              out.write('\n');
                              out.write('	');
                              //  liferay-document-library:mime-type-sticker
                              com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag _jspx_th_liferay$1document$1library_mime$1type$1sticker_2 = (com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag) _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.get(com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag.class);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_2.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_1);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_2.setCssClass("sticker-bottom");
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_2.setFileVersion( latestFileVersion );
                              int _jspx_eval_liferay$1document$1library_mime$1type$1sticker_2 = _jspx_th_liferay$1document$1library_mime$1type$1sticker_2.doStartTag();
                              if (_jspx_th_liferay$1document$1library_mime$1type$1sticker_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.reuse(_jspx_th_liferay$1document$1library_mime$1type$1sticker_2);
                              return;
                              }
                              _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.reuse(_jspx_th_liferay$1document$1library_mime$1type$1sticker_2);
                              out.write('\n');
                              int evalDoAfterBody = _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_1.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1sticker$1bottom_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1sticker$1bottom.reuse(_jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_1);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1sticker$1bottom.reuse(_jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_1);
                              out.write('\n');
                              out.write('\n');
                              //  liferay-frontend:vertical-card-header
                              com.liferay.frontend.taglib.servlet.taglib.VerticalCardHeaderTag _jspx_th_liferay$1frontend_vertical$1card$1header_1 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardHeaderTag) _jspx_tagPool_liferay$1frontend_vertical$1card$1header.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardHeaderTag.class);
                              _jspx_th_liferay$1frontend_vertical$1card$1header_1.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_vertical$1card$1header_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_icon$1vertical$1card_0);
                              int _jspx_eval_liferay$1frontend_vertical$1card$1header_1 = _jspx_th_liferay$1frontend_vertical$1card$1header_1.doStartTag();
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1header_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1header_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1frontend_vertical$1card$1header_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1frontend_vertical$1card$1header_1.doInitBody();
                              }
                              do {
                              out.write('\n');
                              out.write('	');
                              out.print( LanguageUtil.format(request, "x-ago-by-x", new String[] {LanguageUtil.getTimeDescription(locale, System.currentTimeMillis() - latestFileVersion.getCreateDate().getTime(), true), HtmlUtil.escape(latestFileVersion.getUserName())}, false) );
                              out.write('\n');
                              int evalDoAfterBody = _jspx_th_liferay$1frontend_vertical$1card$1header_1.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1header_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1frontend_vertical$1card$1header_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1header.reuse(_jspx_th_liferay$1frontend_vertical$1card$1header_1);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1header.reuse(_jspx_th_liferay$1frontend_vertical$1card$1header_1);
                              out.write('\n');
                              out.write('\n');
                              //  liferay-frontend:vertical-card-footer
                              com.liferay.frontend.taglib.servlet.taglib.VerticalCardFooterTag _jspx_th_liferay$1frontend_vertical$1card$1footer_1 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardFooterTag) _jspx_tagPool_liferay$1frontend_vertical$1card$1footer.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardFooterTag.class);
                              _jspx_th_liferay$1frontend_vertical$1card$1footer_1.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_vertical$1card$1footer_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_icon$1vertical$1card_0);
                              int _jspx_eval_liferay$1frontend_vertical$1card$1footer_1 = _jspx_th_liferay$1frontend_vertical$1card$1footer_1.doStartTag();
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1footer_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1footer_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1frontend_vertical$1card$1footer_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1frontend_vertical$1card$1footer_1.doInitBody();
                              }
                              do {
                              out.write('\n');
                              out.write('	');
                              //  aui:workflow-status
                              com.liferay.taglib.aui.WorkflowStatusTag _jspx_th_aui_workflow$1status_1 = (com.liferay.taglib.aui.WorkflowStatusTag) _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_nobody.get(com.liferay.taglib.aui.WorkflowStatusTag.class);
                              _jspx_th_aui_workflow$1status_1.setPageContext(_jspx_page_context);
                              _jspx_th_aui_workflow$1status_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card$1footer_1);
                              _jspx_th_aui_workflow$1status_1.setShowIcon( false );
                              _jspx_th_aui_workflow$1status_1.setShowLabel( false );
                              _jspx_th_aui_workflow$1status_1.setStatus( latestFileVersion.getStatus() );
                              int _jspx_eval_aui_workflow$1status_1 = _jspx_th_aui_workflow$1status_1.doStartTag();
                              if (_jspx_th_aui_workflow$1status_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_nobody.reuse(_jspx_th_aui_workflow$1status_1);
                              return;
                              }
                              _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_nobody.reuse(_jspx_th_aui_workflow$1status_1);
                              out.write("\n\n\t");
                              //  c:choose
                              org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_5 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                              _jspx_th_c_choose_5.setPageContext(_jspx_page_context);
                              _jspx_th_c_choose_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card$1footer_1);
                              int _jspx_eval_c_choose_5 = _jspx_th_c_choose_5.doStartTag();
                              if (_jspx_eval_c_choose_5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_8 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_8.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_5);
                              _jspx_th_c_when_8.setTest( fileShortcut != null );
                              int _jspx_eval_c_when_8 = _jspx_th_c_when_8.doStartTag();
                              if (_jspx_eval_c_when_8 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t<span class=\"file-icon-color-0\">\n\t\t\t\t");
                              if (_jspx_meth_aui_icon_2((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_8, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t</span>\n\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_8.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_8);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_8);
                              out.write("\n\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_9 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_9.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_5);
                              _jspx_th_c_when_9.setTest( fileEntry.hasLock() || fileEntry.isCheckedOut() );
                              int _jspx_eval_c_when_9 = _jspx_th_c_when_9.doStartTag();
                              if (_jspx_eval_c_when_9 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t<span class=\"file-icon-color-0\">\n\t\t\t\t");
                              if (_jspx_meth_aui_icon_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_9, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t</span>\n\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_9.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_9);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_9);
                              out.write('\n');
                              out.write('	');
                              int evalDoAfterBody = _jspx_th_c_choose_5.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_choose_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_5);
                              return;
                              }
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_5);
                              out.write('\n');
                              int evalDoAfterBody = _jspx_th_liferay$1frontend_vertical$1card$1footer_1.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1footer_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1frontend_vertical$1card$1footer_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1footer.reuse(_jspx_th_liferay$1frontend_vertical$1card$1footer_1);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1footer.reuse(_jspx_th_liferay$1frontend_vertical$1card$1footer_1);
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              }
                              if (_jspx_th_liferay$1frontend_icon$1vertical$1card_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_icon$1vertical$1card_url_title_rowChecker_resultRow_icon_cssClass_actionJspServletContext_actionJsp.reuse(_jspx_th_liferay$1frontend_icon$1vertical$1card_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_icon$1vertical$1card_url_title_rowChecker_resultRow_icon_cssClass_actionJspServletContext_actionJsp.reuse(_jspx_th_liferay$1frontend_icon$1vertical$1card_0);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_7.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_7);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_7);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:otherwise
                              org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_1 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                              _jspx_th_c_otherwise_1.setPageContext(_jspx_page_context);
                              _jspx_th_c_otherwise_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_3);
                              int _jspx_eval_c_otherwise_1 = _jspx_th_c_otherwise_1.doStartTag();
                              if (_jspx_eval_c_otherwise_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-frontend:vertical-card
                              com.liferay.frontend.taglib.servlet.taglib.VerticalCardTag _jspx_th_liferay$1frontend_vertical$1card_0 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardTag) _jspx_tagPool_liferay$1frontend_vertical$1card_url_title_rowChecker_resultRow_imageUrl_cssClass_actionJspServletContext_actionJsp.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardTag.class);
                              _jspx_th_liferay$1frontend_vertical$1card_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_vertical$1card_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_1);
                              _jspx_th_liferay$1frontend_vertical$1card_0.setActionJsp("/document_library/file_entry_action.jsp");
                              _jspx_th_liferay$1frontend_vertical$1card_0.setActionJspServletContext( application );
                              _jspx_th_liferay$1frontend_vertical$1card_0.setCssClass("entry-display-style file-card");
                              _jspx_th_liferay$1frontend_vertical$1card_0.setImageUrl( thumbnailSrc );
                              _jspx_th_liferay$1frontend_vertical$1card_0.setResultRow( row );
                              _jspx_th_liferay$1frontend_vertical$1card_0.setRowChecker( entriesChecker );
                              _jspx_th_liferay$1frontend_vertical$1card_0.setTitle( latestFileVersion.getTitle() );
                              _jspx_th_liferay$1frontend_vertical$1card_0.setUrl( (rowURL != null) ? rowURL.toString() : null );
                              int _jspx_eval_liferay$1frontend_vertical$1card_0 = _jspx_th_liferay$1frontend_vertical$1card_0.doStartTag();
                              if (_jspx_eval_liferay$1frontend_vertical$1card_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                              out.write('\n');
                              out.write('\n');
                              //  liferay-frontend:vertical-card-sticker-bottom
                              com.liferay.frontend.taglib.servlet.taglib.VerticalCardStickerBottomTag _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_2 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardStickerBottomTag) _jspx_tagPool_liferay$1frontend_vertical$1card$1sticker$1bottom.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardStickerBottomTag.class);
                              _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_2.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card_0);
                              int _jspx_eval_liferay$1frontend_vertical$1card$1sticker$1bottom_2 = _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_2.doStartTag();
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1sticker$1bottom_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1sticker$1bottom_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_2.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_2.doInitBody();
                              }
                              do {
                              out.write('\n');
                              out.write('	');
                              //  liferay-document-library:mime-type-sticker
                              com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag _jspx_th_liferay$1document$1library_mime$1type$1sticker_3 = (com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag) _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.get(com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag.class);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_3.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_2);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_3.setCssClass("sticker-bottom");
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_3.setFileVersion( latestFileVersion );
                              int _jspx_eval_liferay$1document$1library_mime$1type$1sticker_3 = _jspx_th_liferay$1document$1library_mime$1type$1sticker_3.doStartTag();
                              if (_jspx_th_liferay$1document$1library_mime$1type$1sticker_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.reuse(_jspx_th_liferay$1document$1library_mime$1type$1sticker_3);
                              return;
                              }
                              _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.reuse(_jspx_th_liferay$1document$1library_mime$1type$1sticker_3);
                              out.write('\n');
                              int evalDoAfterBody = _jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_2.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1sticker$1bottom_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1sticker$1bottom.reuse(_jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_2);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1sticker$1bottom.reuse(_jspx_th_liferay$1frontend_vertical$1card$1sticker$1bottom_2);
                              out.write('\n');
                              out.write('\n');
                              //  liferay-frontend:vertical-card-header
                              com.liferay.frontend.taglib.servlet.taglib.VerticalCardHeaderTag _jspx_th_liferay$1frontend_vertical$1card$1header_2 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardHeaderTag) _jspx_tagPool_liferay$1frontend_vertical$1card$1header.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardHeaderTag.class);
                              _jspx_th_liferay$1frontend_vertical$1card$1header_2.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_vertical$1card$1header_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card_0);
                              int _jspx_eval_liferay$1frontend_vertical$1card$1header_2 = _jspx_th_liferay$1frontend_vertical$1card$1header_2.doStartTag();
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1header_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1header_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1frontend_vertical$1card$1header_2.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1frontend_vertical$1card$1header_2.doInitBody();
                              }
                              do {
                              out.write('\n');
                              out.write('	');
                              out.print( LanguageUtil.format(request, "x-ago-by-x", new String[] {LanguageUtil.getTimeDescription(locale, System.currentTimeMillis() - latestFileVersion.getCreateDate().getTime(), true), HtmlUtil.escape(latestFileVersion.getUserName())}, false) );
                              out.write('\n');
                              int evalDoAfterBody = _jspx_th_liferay$1frontend_vertical$1card$1header_2.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1header_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1frontend_vertical$1card$1header_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1header.reuse(_jspx_th_liferay$1frontend_vertical$1card$1header_2);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1header.reuse(_jspx_th_liferay$1frontend_vertical$1card$1header_2);
                              out.write('\n');
                              out.write('\n');
                              //  liferay-frontend:vertical-card-footer
                              com.liferay.frontend.taglib.servlet.taglib.VerticalCardFooterTag _jspx_th_liferay$1frontend_vertical$1card$1footer_2 = (com.liferay.frontend.taglib.servlet.taglib.VerticalCardFooterTag) _jspx_tagPool_liferay$1frontend_vertical$1card$1footer.get(com.liferay.frontend.taglib.servlet.taglib.VerticalCardFooterTag.class);
                              _jspx_th_liferay$1frontend_vertical$1card$1footer_2.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_vertical$1card$1footer_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card_0);
                              int _jspx_eval_liferay$1frontend_vertical$1card$1footer_2 = _jspx_th_liferay$1frontend_vertical$1card$1footer_2.doStartTag();
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1footer_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1footer_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1frontend_vertical$1card$1footer_2.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1frontend_vertical$1card$1footer_2.doInitBody();
                              }
                              do {
                              out.write('\n');
                              out.write('	');
                              //  aui:workflow-status
                              com.liferay.taglib.aui.WorkflowStatusTag _jspx_th_aui_workflow$1status_2 = (com.liferay.taglib.aui.WorkflowStatusTag) _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_nobody.get(com.liferay.taglib.aui.WorkflowStatusTag.class);
                              _jspx_th_aui_workflow$1status_2.setPageContext(_jspx_page_context);
                              _jspx_th_aui_workflow$1status_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card$1footer_2);
                              _jspx_th_aui_workflow$1status_2.setShowIcon( false );
                              _jspx_th_aui_workflow$1status_2.setShowLabel( false );
                              _jspx_th_aui_workflow$1status_2.setStatus( latestFileVersion.getStatus() );
                              int _jspx_eval_aui_workflow$1status_2 = _jspx_th_aui_workflow$1status_2.doStartTag();
                              if (_jspx_th_aui_workflow$1status_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_nobody.reuse(_jspx_th_aui_workflow$1status_2);
                              return;
                              }
                              _jspx_tagPool_aui_workflow$1status_status_showLabel_showIcon_nobody.reuse(_jspx_th_aui_workflow$1status_2);
                              out.write("\n\n\t");
                              //  c:choose
                              org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_6 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                              _jspx_th_c_choose_6.setPageContext(_jspx_page_context);
                              _jspx_th_c_choose_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_vertical$1card$1footer_2);
                              int _jspx_eval_c_choose_6 = _jspx_th_c_choose_6.doStartTag();
                              if (_jspx_eval_c_choose_6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_10 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_10.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_6);
                              _jspx_th_c_when_10.setTest( fileShortcut != null );
                              int _jspx_eval_c_when_10 = _jspx_th_c_when_10.doStartTag();
                              if (_jspx_eval_c_when_10 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t<span class=\"file-icon-color-0\">\n\t\t\t\t");
                              if (_jspx_meth_aui_icon_4((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_10, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t</span>\n\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_10.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_10);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_10);
                              out.write("\n\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_11 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_11.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_6);
                              _jspx_th_c_when_11.setTest( fileEntry.hasLock() || fileEntry.isCheckedOut() );
                              int _jspx_eval_c_when_11 = _jspx_th_c_when_11.doStartTag();
                              if (_jspx_eval_c_when_11 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t<span class=\"file-icon-color-0\">\n\t\t\t\t");
                              if (_jspx_meth_aui_icon_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_11, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t</span>\n\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_11.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_11);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_11);
                              out.write('\n');
                              out.write('	');
                              int evalDoAfterBody = _jspx_th_c_choose_6.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_choose_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_6);
                              return;
                              }
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_6);
                              out.write('\n');
                              int evalDoAfterBody = _jspx_th_liferay$1frontend_vertical$1card$1footer_2.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1frontend_vertical$1card$1footer_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1frontend_vertical$1card$1footer_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1footer.reuse(_jspx_th_liferay$1frontend_vertical$1card$1footer_2);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_vertical$1card$1footer.reuse(_jspx_th_liferay$1frontend_vertical$1card$1footer_2);
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              }
                              if (_jspx_th_liferay$1frontend_vertical$1card_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_vertical$1card_url_title_rowChecker_resultRow_imageUrl_cssClass_actionJspServletContext_actionJsp.reuse(_jspx_th_liferay$1frontend_vertical$1card_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_vertical$1card_url_title_rowChecker_resultRow_imageUrl_cssClass_actionJspServletContext_actionJsp.reuse(_jspx_th_liferay$1frontend_vertical$1card_0);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_otherwise_1.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_otherwise_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_1);
                              return;
                              }
                              _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_1);
                              out.write("\n\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_choose_3.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_choose_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_3);
                              return;
                              }
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_3);
                              out.write("\n\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_liferay$1ui_search$1container$1column$1text_1.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                            }
                            if (_jspx_th_liferay$1ui_search$1container$1column$1text_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_1);
                              return;
                            }
                            _jspx_tagPool_liferay$1ui_search$1container$1column$1text.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_1);
                            out.write("\n\t\t\t\t\t\t");
                            int evalDoAfterBody = _jspx_th_c_when_3.doAfterBody();
                            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                          } while (true);
                        }
                        if (_jspx_th_c_when_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_3);
                          return;
                        }
                        _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_3);
                        out.write("\n\t\t\t\t\t\t");
                        //  c:otherwise
                        org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_2 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                        _jspx_th_c_otherwise_2.setPageContext(_jspx_page_context);
                        _jspx_th_c_otherwise_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                        int _jspx_eval_c_otherwise_2 = _jspx_th_c_otherwise_2.doStartTag();
                        if (_jspx_eval_c_otherwise_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                          do {
                            out.write("\n\n\t\t\t\t\t\t\t");

							for (int i = 0; i < entryColumns.length; i++) {
								String curEntryColumn = entryColumns[i];
							
                            out.write("\n\n\t\t\t\t\t\t\t\t");
                            //  c:choose
                            org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_7 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                            _jspx_th_c_choose_7.setPageContext(_jspx_page_context);
                            _jspx_th_c_choose_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_2);
                            int _jspx_eval_c_choose_7 = _jspx_th_c_choose_7.doStartTag();
                            if (_jspx_eval_c_choose_7 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_12 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_12.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_7);
                              _jspx_th_c_when_12.setTest( curEntryColumn.equals("name") );
                              int _jspx_eval_c_when_12 = _jspx_th_c_when_12.doStartTag();
                              if (_jspx_eval_c_when_12 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t");

										PortletURL rowURL = liferayPortletResponse.createRenderURL();

										rowURL.setParameter("mvcRenderCommandName", "/document_library/view_file_entry");
										rowURL.setParameter("redirect", HttpUtil.removeParameter(currentURL, liferayPortletResponse.getNamespace() + "ajax"));
										rowURL.setParameter("fileEntryId", String.valueOf(fileEntry.getFileEntryId()));
										
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-text
                              com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_2 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name_cssClass.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_2.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_12);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_2.setCssClass("table-cell-expand table-cell-minw-200 table-title");
                              _jspx_th_liferay$1ui_search$1container$1column$1text_2.setName("title");
                              int _jspx_eval_liferay$1ui_search$1container$1column$1text_2 = _jspx_th_liferay$1ui_search$1container$1column$1text_2.doStartTag();
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1ui_search$1container$1column$1text_2.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_2.doInitBody();
                              }
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-document-library:mime-type-sticker
                              com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag _jspx_th_liferay$1document$1library_mime$1type$1sticker_4 = (com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag) _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.get(com.liferay.document.library.taglib.servlet.taglib.MimeTypeStickerTag.class);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_4.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1column$1text_2);
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_4.setCssClass("sticker-secondary");
                              _jspx_th_liferay$1document$1library_mime$1type$1sticker_4.setFileVersion( latestFileVersion );
                              int _jspx_eval_liferay$1document$1library_mime$1type$1sticker_4 = _jspx_th_liferay$1document$1library_mime$1type$1sticker_4.doStartTag();
                              if (_jspx_th_liferay$1document$1library_mime$1type$1sticker_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.reuse(_jspx_th_liferay$1document$1library_mime$1type$1sticker_4);
                              return;
                              }
                              _jspx_tagPool_liferay$1document$1library_mime$1type$1sticker_fileVersion_cssClass_nobody.reuse(_jspx_th_liferay$1document$1library_mime$1type$1sticker_4);
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t");
                              //  aui:a
                              com.liferay.taglib.aui.ATag _jspx_th_aui_a_0 = (com.liferay.taglib.aui.ATag) _jspx_tagPool_aui_a_href.get(com.liferay.taglib.aui.ATag.class);
                              _jspx_th_aui_a_0.setPageContext(_jspx_page_context);
                              _jspx_th_aui_a_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1column$1text_2);
                              _jspx_th_aui_a_0.setHref( rowURL.toString() );
                              int _jspx_eval_aui_a_0 = _jspx_th_aui_a_0.doStartTag();
                              if (_jspx_eval_aui_a_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              out.print( latestFileVersion.getTitle() );
                              }
                              if (_jspx_th_aui_a_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_aui_a_href.reuse(_jspx_th_aui_a_0);
                              return;
                              }
                              _jspx_tagPool_aui_a_href.reuse(_jspx_th_aui_a_0);
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t");
                              //  c:if
                              org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_0 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                              _jspx_th_c_if_0.setPageContext(_jspx_page_context);
                              _jspx_th_c_if_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1column$1text_2);
                              _jspx_th_c_if_0.setTest( fileEntry.hasLock() || fileEntry.isCheckedOut() );
                              int _jspx_eval_c_if_0 = _jspx_th_c_if_0.doStartTag();
                              if (_jspx_eval_c_if_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                              if (_jspx_meth_aui_icon_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_0, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_if_0.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_if_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_0);
                              return;
                              }
                              _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_0);
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t");
                              //  c:if
                              org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_1 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                              _jspx_th_c_if_1.setPageContext(_jspx_page_context);
                              _jspx_th_c_if_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1column$1text_2);
                              _jspx_th_c_if_1.setTest( fileShortcut != null );
                              int _jspx_eval_c_if_1 = _jspx_th_c_if_1.doStartTag();
                              if (_jspx_eval_c_if_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                              if (_jspx_meth_aui_icon_7((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_1, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_if_1.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_if_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_1);
                              return;
                              }
                              _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_1);
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_liferay$1ui_search$1container$1column$1text_2.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1ui_search$1container$1column$1text_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name_cssClass.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_2);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name_cssClass.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_2);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_12.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_12.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_12);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_12);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_13 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_13.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_13.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_7);
                              _jspx_th_c_when_13.setTest( curEntryColumn.equals("description") );
                              int _jspx_eval_c_when_13 = _jspx_th_c_when_13.doStartTag();
                              if (_jspx_eval_c_when_13 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-text
                              com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_3 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_3.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_13);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_3.setCssClass("table-cell-expand table-cell-minw-200");
                              _jspx_th_liferay$1ui_search$1container$1column$1text_3.setName("description");
                              _jspx_th_liferay$1ui_search$1container$1column$1text_3.setValue( StringUtil.shorten(fileEntry.getDescription(), 100) );
                              int _jspx_eval_liferay$1ui_search$1container$1column$1text_3 = _jspx_th_liferay$1ui_search$1container$1column$1text_3.doStartTag();
                              if (_jspx_th_liferay$1ui_search$1container$1column$1text_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_3);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_3);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_13.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_13.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_13);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_13);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_14 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_14.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_14.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_7);
                              _jspx_th_c_when_14.setTest( curEntryColumn.equals("document-type") );
                              int _jspx_eval_c_when_14 = _jspx_th_c_when_14.doStartTag();
                              if (_jspx_eval_c_when_14 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  c:choose
                              org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_8 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                              _jspx_th_c_choose_8.setPageContext(_jspx_page_context);
                              _jspx_th_c_choose_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_14);
                              int _jspx_eval_c_choose_8 = _jspx_th_c_choose_8.doStartTag();
                              if (_jspx_eval_c_choose_8 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_15 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_15.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_15.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_8);
                              _jspx_th_c_when_15.setTest( latestFileVersion.getModel() instanceof DLFileVersion );
                              int _jspx_eval_c_when_15 = _jspx_th_c_when_15.doStartTag();
                              if (_jspx_eval_c_when_15 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t\t");

												DLFileVersion latestDLFileVersion = (DLFileVersion)latestFileVersion.getModel();

												DLFileEntryType dlFileEntryType = latestDLFileVersion.getDLFileEntryType();
												
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-text
                              com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_4 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_4.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_15);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_4.setCssClass("table-cell-expand-smaller table-cell-minw-150");
                              _jspx_th_liferay$1ui_search$1container$1column$1text_4.setName("document-type");
                              _jspx_th_liferay$1ui_search$1container$1column$1text_4.setValue( HtmlUtil.escape(dlFileEntryType.getName(locale)) );
                              int _jspx_eval_liferay$1ui_search$1container$1column$1text_4 = _jspx_th_liferay$1ui_search$1container$1column$1text_4.doStartTag();
                              if (_jspx_th_liferay$1ui_search$1container$1column$1text_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_4);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_4);
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_15.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_15.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_15);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_15);
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                              if (_jspx_meth_c_otherwise_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_choose_8, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_choose_8.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_choose_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_8);
                              return;
                              }
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_8);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_14.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_14.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_14);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_14);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_16 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_16.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_16.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_7);
                              _jspx_th_c_when_16.setTest( curEntryColumn.equals("size") );
                              int _jspx_eval_c_when_16 = _jspx_th_c_when_16.doStartTag();
                              if (_jspx_eval_c_when_16 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-text
                              com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_6 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_6.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_16);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_6.setCssClass("table-cell-expand-smallest");
                              _jspx_th_liferay$1ui_search$1container$1column$1text_6.setName("size");
                              _jspx_th_liferay$1ui_search$1container$1column$1text_6.setValue( LanguageUtil.formatStorageSize(latestFileVersion.getSize(), locale) );
                              int _jspx_eval_liferay$1ui_search$1container$1column$1text_6 = _jspx_th_liferay$1ui_search$1container$1column$1text_6.doStartTag();
                              if (_jspx_th_liferay$1ui_search$1container$1column$1text_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_6);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_6);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_16.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_16.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_16);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_16);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_17 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_17.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_17.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_7);
                              _jspx_th_c_when_17.setTest( curEntryColumn.equals("status") );
                              int _jspx_eval_c_when_17 = _jspx_th_c_when_17.doStartTag();
                              if (_jspx_eval_c_when_17 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-status
                              com.liferay.taglib.ui.SearchContainerColumnStatusTag _jspx_th_liferay$1ui_search$1container$1column$1status_0 = (com.liferay.taglib.ui.SearchContainerColumnStatusTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1status_status_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnStatusTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1status_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1status_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_17);
                              _jspx_th_liferay$1ui_search$1container$1column$1status_0.setCssClass("table-cell-expand-smallest");
                              _jspx_th_liferay$1ui_search$1container$1column$1status_0.setName("status");
                              _jspx_th_liferay$1ui_search$1container$1column$1status_0.setStatus( latestFileVersion.getStatus() );
                              int _jspx_eval_liferay$1ui_search$1container$1column$1status_0 = _jspx_th_liferay$1ui_search$1container$1column$1status_0.doStartTag();
                              if (_jspx_th_liferay$1ui_search$1container$1column$1status_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1status_status_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1status_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1status_status_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1status_0);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_17.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_17.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_17);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_17);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_18 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_18.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_18.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_7);
                              _jspx_th_c_when_18.setTest( curEntryColumn.equals("downloads") );
                              int _jspx_eval_c_when_18 = _jspx_th_c_when_18.doStartTag();
                              if (_jspx_eval_c_when_18 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-text
                              com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_7 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_7.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_18);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_7.setCssClass("table-cell-expand-smallest");
                              _jspx_th_liferay$1ui_search$1container$1column$1text_7.setName("downloads");
                              _jspx_th_liferay$1ui_search$1container$1column$1text_7.setValue( String.valueOf(fileEntry.getReadCount()) );
                              int _jspx_eval_liferay$1ui_search$1container$1column$1text_7 = _jspx_th_liferay$1ui_search$1container$1column$1text_7.doStartTag();
                              if (_jspx_th_liferay$1ui_search$1container$1column$1text_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_7);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_7);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_18.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_18.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_18);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_18);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_19 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_19.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_19.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_7);
                              _jspx_th_c_when_19.setTest( curEntryColumn.equals("create-date") );
                              int _jspx_eval_c_when_19 = _jspx_th_c_when_19.doStartTag();
                              if (_jspx_eval_c_when_19 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-date
                              com.liferay.taglib.ui.SearchContainerColumnDateTag _jspx_th_liferay$1ui_search$1container$1column$1date_0 = (com.liferay.taglib.ui.SearchContainerColumnDateTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnDateTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1date_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1date_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_19);
                              _jspx_th_liferay$1ui_search$1container$1column$1date_0.setCssClass("table-cell-expand-smallest table-cell-ws-nowrap");
                              _jspx_th_liferay$1ui_search$1container$1column$1date_0.setName("create-date");
                              _jspx_th_liferay$1ui_search$1container$1column$1date_0.setValue( fileEntry.getCreateDate() );
                              int _jspx_eval_liferay$1ui_search$1container$1column$1date_0 = _jspx_th_liferay$1ui_search$1container$1column$1date_0.doStartTag();
                              if (_jspx_th_liferay$1ui_search$1container$1column$1date_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1date_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1date_0);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_19.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_19.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_19);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_19);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_20 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_20.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_20.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_7);
                              _jspx_th_c_when_20.setTest( curEntryColumn.equals("modified-date") );
                              int _jspx_eval_c_when_20 = _jspx_th_c_when_20.doStartTag();
                              if (_jspx_eval_c_when_20 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-date
                              com.liferay.taglib.ui.SearchContainerColumnDateTag _jspx_th_liferay$1ui_search$1container$1column$1date_1 = (com.liferay.taglib.ui.SearchContainerColumnDateTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnDateTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1date_1.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1date_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_20);
                              _jspx_th_liferay$1ui_search$1container$1column$1date_1.setCssClass("table-cell-expand-smallest table-cell-ws-nowrap");
                              _jspx_th_liferay$1ui_search$1container$1column$1date_1.setName("modified-date");
                              _jspx_th_liferay$1ui_search$1container$1column$1date_1.setValue( latestFileVersion.getModifiedDate() );
                              int _jspx_eval_liferay$1ui_search$1container$1column$1date_1 = _jspx_th_liferay$1ui_search$1container$1column$1date_1.doStartTag();
                              if (_jspx_th_liferay$1ui_search$1container$1column$1date_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1date_1);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1date_1);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_20.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_20.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_20);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_20);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_21 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_21.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_21.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_7);
                              _jspx_th_c_when_21.setTest( curEntryColumn.equals("action") );
                              int _jspx_eval_c_when_21 = _jspx_th_c_when_21.doStartTag();
                              if (_jspx_eval_c_when_21 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              if (_jspx_meth_liferay$1ui_search$1container$1column$1jsp_2((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_21, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_21.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_21.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_21);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_21);
                              out.write("\n\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_choose_7.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                            }
                            if (_jspx_th_c_choose_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_7);
                              return;
                            }
                            _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_7);
                            out.write("\n\n\t\t\t\t\t\t\t");

							}
							
                            out.write("\n\n\t\t\t\t\t\t");
                            int evalDoAfterBody = _jspx_th_c_otherwise_2.doAfterBody();
                            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                          } while (true);
                        }
                        if (_jspx_th_c_otherwise_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_2);
                          return;
                        }
                        _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_2);
                        out.write("\n\t\t\t\t\t");
                        int evalDoAfterBody = _jspx_th_c_choose_1.doAfterBody();
                        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                          break;
                      } while (true);
                    }
                    if (_jspx_th_c_choose_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_1);
                      return;
                    }
                    _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_1);
                    out.write("\n\t\t\t\t");
                    int evalDoAfterBody = _jspx_th_c_when_0.doAfterBody();
                    if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                      break;
                  } while (true);
                }
                if (_jspx_th_c_when_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_0);
                  return;
                }
                _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_0);
                out.write("\n\t\t\t\t");
                //  c:otherwise
                org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_4 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                _jspx_th_c_otherwise_4.setPageContext(_jspx_page_context);
                _jspx_th_c_otherwise_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
                int _jspx_eval_c_otherwise_4 = _jspx_th_c_otherwise_4.doStartTag();
                if (_jspx_eval_c_otherwise_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\n\t\t\t\t\t");

					if (dlSearchContainer.getRowChecker() == null) {
						dlSearchContainer.setRowChecker(entriesChecker);
					}

					boolean draggable = false;

					if (!BrowserSnifferUtil.isMobile(request) && (DLFolderPermission.contains(permissionChecker, curFolder, ActionKeys.DELETE) || DLFolderPermission.contains(permissionChecker, curFolder, ActionKeys.UPDATE))) {
						draggable = true;

						if (dlSearchContainer.getRowMover() == null) {
							dlSearchContainer.setRowMover(entriesMover);
						}
					}

					Map<String, Object> rowData = new HashMap<String, Object>();

					rowData.put("actions", StringUtil.merge(dlAdminManagementToolbarDisplayContext.getAvailableActions(curFolder)));
					rowData.put("draggable", draggable);
					rowData.put("folder", true);
					rowData.put("folder-id", curFolder.getFolderId());
					rowData.put("title", curFolder.getName());

					row.setData(rowData);

					row.setPrimaryKey(String.valueOf(curFolder.getPrimaryKey()));
					
                    out.write("\n\n\t\t\t\t\t");
                    //  c:choose
                    org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_9 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                    _jspx_th_c_choose_9.setPageContext(_jspx_page_context);
                    _jspx_th_c_choose_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_4);
                    int _jspx_eval_c_choose_9 = _jspx_th_c_choose_9.doStartTag();
                    if (_jspx_eval_c_choose_9 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      do {
                        out.write("\n\t\t\t\t\t\t");
                        //  c:when
                        org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_22 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                        _jspx_th_c_when_22.setPageContext(_jspx_page_context);
                        _jspx_th_c_when_22.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_9);
                        _jspx_th_c_when_22.setTest( displayStyle.equals("descriptive") );
                        int _jspx_eval_c_when_22 = _jspx_th_c_when_22.doStartTag();
                        if (_jspx_eval_c_when_22 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                          do {
                            out.write("\n\t\t\t\t\t\t\t");
                            //  liferay-ui:search-container-column-icon
                            com.liferay.taglib.ui.SearchContainerColumnIconTag _jspx_th_liferay$1ui_search$1container$1column$1icon_0 = (com.liferay.taglib.ui.SearchContainerColumnIconTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1icon_toggleRowChecker_icon_nobody.get(com.liferay.taglib.ui.SearchContainerColumnIconTag.class);
                            _jspx_th_liferay$1ui_search$1container$1column$1icon_0.setPageContext(_jspx_page_context);
                            _jspx_th_liferay$1ui_search$1container$1column$1icon_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_22);
                            _jspx_th_liferay$1ui_search$1container$1column$1icon_0.setIcon( curFolder.isMountPoint() ? "repository" : "folder" );
                            _jspx_th_liferay$1ui_search$1container$1column$1icon_0.setToggleRowChecker( true );
                            int _jspx_eval_liferay$1ui_search$1container$1column$1icon_0 = _jspx_th_liferay$1ui_search$1container$1column$1icon_0.doStartTag();
                            if (_jspx_th_liferay$1ui_search$1container$1column$1icon_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1icon_toggleRowChecker_icon_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1icon_0);
                              return;
                            }
                            _jspx_tagPool_liferay$1ui_search$1container$1column$1icon_toggleRowChecker_icon_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1icon_0);
                            out.write("\n\n\t\t\t\t\t\t\t");
                            //  liferay-ui:search-container-column-jsp
                            com.liferay.taglib.ui.SearchContainerColumnJSPTag _jspx_th_liferay$1ui_search$1container$1column$1jsp_3 = (com.liferay.taglib.ui.SearchContainerColumnJSPTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_colspan_nobody.get(com.liferay.taglib.ui.SearchContainerColumnJSPTag.class);
                            _jspx_th_liferay$1ui_search$1container$1column$1jsp_3.setPageContext(_jspx_page_context);
                            _jspx_th_liferay$1ui_search$1container$1column$1jsp_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_22);
                            _jspx_th_liferay$1ui_search$1container$1column$1jsp_3.setColspan( 2 );
                            _jspx_th_liferay$1ui_search$1container$1column$1jsp_3.setPath("/document_library/view_folder_descriptive.jsp");
                            int _jspx_eval_liferay$1ui_search$1container$1column$1jsp_3 = _jspx_th_liferay$1ui_search$1container$1column$1jsp_3.doStartTag();
                            if (_jspx_th_liferay$1ui_search$1container$1column$1jsp_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_colspan_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1jsp_3);
                              return;
                            }
                            _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_colspan_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1jsp_3);
                            out.write("\n\n\t\t\t\t\t\t\t");
                            if (_jspx_meth_liferay$1ui_search$1container$1column$1jsp_4((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_22, _jspx_page_context))
                              return;
                            out.write("\n\t\t\t\t\t\t");
                            int evalDoAfterBody = _jspx_th_c_when_22.doAfterBody();
                            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                          } while (true);
                        }
                        if (_jspx_th_c_when_22.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_22);
                          return;
                        }
                        _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_22);
                        out.write("\n\t\t\t\t\t\t");
                        //  c:when
                        org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_23 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                        _jspx_th_c_when_23.setPageContext(_jspx_page_context);
                        _jspx_th_c_when_23.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_9);
                        _jspx_th_c_when_23.setTest( displayStyle.equals("icon") );
                        int _jspx_eval_c_when_23 = _jspx_th_c_when_23.doStartTag();
                        if (_jspx_eval_c_when_23 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                          do {
                            out.write("\n\n\t\t\t\t\t\t\t");

							row.setCssClass("entry-card lfr-asset-folder");

							PortletURL rowURL = liferayPortletResponse.createRenderURL();

							rowURL.setParameter("mvcRenderCommandName", "/document_library/view_folder");
							rowURL.setParameter("redirect", currentURL);
							rowURL.setParameter("folderId", String.valueOf(curFolder.getFolderId()));
							
                            out.write("\n\n\t\t\t\t\t\t\t");
                            //  liferay-ui:search-container-column-text
                            com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_8 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_colspan.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                            _jspx_th_liferay$1ui_search$1container$1column$1text_8.setPageContext(_jspx_page_context);
                            _jspx_th_liferay$1ui_search$1container$1column$1text_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_23);
                            _jspx_th_liferay$1ui_search$1container$1column$1text_8.setColspan( 2 );
                            int _jspx_eval_liferay$1ui_search$1container$1column$1text_8 = _jspx_th_liferay$1ui_search$1container$1column$1text_8.doStartTag();
                            if (_jspx_eval_liferay$1ui_search$1container$1column$1text_8 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_8 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1ui_search$1container$1column$1text_8.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_8.doInitBody();
                              }
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t");
                              //  liferay-frontend:horizontal-card
                              com.liferay.frontend.taglib.servlet.taglib.HorizontalCardTag _jspx_th_liferay$1frontend_horizontal$1card_0 = (com.liferay.frontend.taglib.servlet.taglib.HorizontalCardTag) _jspx_tagPool_liferay$1frontend_horizontal$1card_url_text_rowChecker_resultRow_actionJspServletContext_actionJsp.get(com.liferay.frontend.taglib.servlet.taglib.HorizontalCardTag.class);
                              _jspx_th_liferay$1frontend_horizontal$1card_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_horizontal$1card_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1column$1text_8);
                              _jspx_th_liferay$1frontend_horizontal$1card_0.setActionJsp("/document_library/folder_action.jsp");
                              _jspx_th_liferay$1frontend_horizontal$1card_0.setActionJspServletContext( application );
                              _jspx_th_liferay$1frontend_horizontal$1card_0.setResultRow( row );
                              _jspx_th_liferay$1frontend_horizontal$1card_0.setRowChecker( entriesChecker );
                              _jspx_th_liferay$1frontend_horizontal$1card_0.setText( curFolder.getName() );
                              _jspx_th_liferay$1frontend_horizontal$1card_0.setUrl( rowURL.toString() );
                              int _jspx_eval_liferay$1frontend_horizontal$1card_0 = _jspx_th_liferay$1frontend_horizontal$1card_0.doStartTag();
                              if (_jspx_eval_liferay$1frontend_horizontal$1card_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  liferay-frontend:horizontal-card-col
                              com.liferay.frontend.taglib.servlet.taglib.HorizontalCardColTag _jspx_th_liferay$1frontend_horizontal$1card$1col_0 = (com.liferay.frontend.taglib.servlet.taglib.HorizontalCardColTag) _jspx_tagPool_liferay$1frontend_horizontal$1card$1col.get(com.liferay.frontend.taglib.servlet.taglib.HorizontalCardColTag.class);
                              _jspx_th_liferay$1frontend_horizontal$1card$1col_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_horizontal$1card$1col_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_horizontal$1card_0);
                              int _jspx_eval_liferay$1frontend_horizontal$1card$1col_0 = _jspx_th_liferay$1frontend_horizontal$1card$1col_0.doStartTag();
                              if (_jspx_eval_liferay$1frontend_horizontal$1card$1col_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1frontend_horizontal$1card$1col_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1frontend_horizontal$1card$1col_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1frontend_horizontal$1card$1col_0.doInitBody();
                              }
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-frontend:horizontal-card-icon
                              com.liferay.frontend.taglib.servlet.taglib.HorizontalCardIconTag _jspx_th_liferay$1frontend_horizontal$1card$1icon_0 = (com.liferay.frontend.taglib.servlet.taglib.HorizontalCardIconTag) _jspx_tagPool_liferay$1frontend_horizontal$1card$1icon_icon_nobody.get(com.liferay.frontend.taglib.servlet.taglib.HorizontalCardIconTag.class);
                              _jspx_th_liferay$1frontend_horizontal$1card$1icon_0.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1frontend_horizontal$1card$1icon_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_horizontal$1card$1col_0);
                              _jspx_th_liferay$1frontend_horizontal$1card$1icon_0.setIcon( curFolder.isMountPoint() ? "repository" : "folder" );
                              int _jspx_eval_liferay$1frontend_horizontal$1card$1icon_0 = _jspx_th_liferay$1frontend_horizontal$1card$1icon_0.doStartTag();
                              if (_jspx_th_liferay$1frontend_horizontal$1card$1icon_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_horizontal$1card$1icon_icon_nobody.reuse(_jspx_th_liferay$1frontend_horizontal$1card$1icon_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_horizontal$1card$1icon_icon_nobody.reuse(_jspx_th_liferay$1frontend_horizontal$1card$1icon_0);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_liferay$1frontend_horizontal$1card$1col_0.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1frontend_horizontal$1card$1col_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1frontend_horizontal$1card$1col_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_horizontal$1card$1col.reuse(_jspx_th_liferay$1frontend_horizontal$1card$1col_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_horizontal$1card$1col.reuse(_jspx_th_liferay$1frontend_horizontal$1card$1col_0);
                              out.write("\n\t\t\t\t\t\t\t\t");
                              }
                              if (_jspx_th_liferay$1frontend_horizontal$1card_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1frontend_horizontal$1card_url_text_rowChecker_resultRow_actionJspServletContext_actionJsp.reuse(_jspx_th_liferay$1frontend_horizontal$1card_0);
                              return;
                              }
                              _jspx_tagPool_liferay$1frontend_horizontal$1card_url_text_rowChecker_resultRow_actionJspServletContext_actionJsp.reuse(_jspx_th_liferay$1frontend_horizontal$1card_0);
                              out.write("\n\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_liferay$1ui_search$1container$1column$1text_8.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_8 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                            }
                            if (_jspx_th_liferay$1ui_search$1container$1column$1text_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_colspan.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_8);
                              return;
                            }
                            _jspx_tagPool_liferay$1ui_search$1container$1column$1text_colspan.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_8);
                            out.write("\n\t\t\t\t\t\t");
                            int evalDoAfterBody = _jspx_th_c_when_23.doAfterBody();
                            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                          } while (true);
                        }
                        if (_jspx_th_c_when_23.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_23);
                          return;
                        }
                        _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_23);
                        out.write("\n\t\t\t\t\t\t");
                        //  c:otherwise
                        org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_5 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                        _jspx_th_c_otherwise_5.setPageContext(_jspx_page_context);
                        _jspx_th_c_otherwise_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_9);
                        int _jspx_eval_c_otherwise_5 = _jspx_th_c_otherwise_5.doStartTag();
                        if (_jspx_eval_c_otherwise_5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                          do {
                            out.write("\n\n\t\t\t\t\t\t\t");

							for (int i = 0; i < entryColumns.length; i++) {
								String curEntryColumn = entryColumns[i];
							
                            out.write("\n\n\t\t\t\t\t\t\t\t");
                            //  c:choose
                            org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_10 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                            _jspx_th_c_choose_10.setPageContext(_jspx_page_context);
                            _jspx_th_c_choose_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_5);
                            int _jspx_eval_c_choose_10 = _jspx_th_c_choose_10.doStartTag();
                            if (_jspx_eval_c_choose_10 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_24 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_24.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_24.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_10);
                              _jspx_th_c_when_24.setTest( curEntryColumn.equals("name") );
                              int _jspx_eval_c_when_24 = _jspx_th_c_when_24.doStartTag();
                              if (_jspx_eval_c_when_24 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t");

										PortletURL rowURL = liferayPortletResponse.createRenderURL();

										rowURL.setParameter("mvcRenderCommandName", "/document_library/view_folder");
										rowURL.setParameter("redirect", currentURL);
										rowURL.setParameter("folderId", String.valueOf(curFolder.getFolderId()));
										
                              out.write("\n\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-text
                              com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_9 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name_cssClass.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_9.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_24);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_9.setCssClass("table-cell-expand table-cell-minw-200 table-title");
                              _jspx_th_liferay$1ui_search$1container$1column$1text_9.setName("title");
                              int _jspx_eval_liferay$1ui_search$1container$1column$1text_9 = _jspx_th_liferay$1ui_search$1container$1column$1text_9.doStartTag();
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_9 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_9 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_liferay$1ui_search$1container$1column$1text_9.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_9.doInitBody();
                              }
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t<div class=\"sticker sticker-document sticker-secondary\">\n\t\t\t\t\t\t\t\t\t\t\t\t");
                              //  clay:icon
                              com.liferay.frontend.taglib.clay.servlet.taglib.soy.IconTag _jspx_th_clay_icon_0 = (com.liferay.frontend.taglib.clay.servlet.taglib.soy.IconTag) _jspx_tagPool_clay_icon_symbol_nobody.get(com.liferay.frontend.taglib.clay.servlet.taglib.soy.IconTag.class);
                              _jspx_th_clay_icon_0.setPageContext(_jspx_page_context);
                              _jspx_th_clay_icon_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1column$1text_9);
                              _jspx_th_clay_icon_0.setSymbol( curFolder.isMountPoint() ? "repository" : "folder" );
                              int _jspx_eval_clay_icon_0 = _jspx_th_clay_icon_0.doStartTag();
                              if (_jspx_th_clay_icon_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_clay_icon_symbol_nobody.reuse(_jspx_th_clay_icon_0);
                              return;
                              }
                              _jspx_tagPool_clay_icon_symbol_nobody.reuse(_jspx_th_clay_icon_0);
                              out.write("\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\n\t\t\t\t\t\t\t\t\t\t\t");
                              //  aui:a
                              com.liferay.taglib.aui.ATag _jspx_th_aui_a_1 = (com.liferay.taglib.aui.ATag) _jspx_tagPool_aui_a_href.get(com.liferay.taglib.aui.ATag.class);
                              _jspx_th_aui_a_1.setPageContext(_jspx_page_context);
                              _jspx_th_aui_a_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container$1column$1text_9);
                              _jspx_th_aui_a_1.setHref( rowURL.toString() );
                              int _jspx_eval_aui_a_1 = _jspx_th_aui_a_1.doStartTag();
                              if (_jspx_eval_aui_a_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              out.print( HtmlUtil.escape(curFolder.getName()) );
                              }
                              if (_jspx_th_aui_a_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_aui_a_href.reuse(_jspx_th_aui_a_1);
                              return;
                              }
                              _jspx_tagPool_aui_a_href.reuse(_jspx_th_aui_a_1);
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_liferay$1ui_search$1container$1column$1text_9.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_liferay$1ui_search$1container$1column$1text_9 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_liferay$1ui_search$1container$1column$1text_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name_cssClass.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_9);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_name_cssClass.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_9);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_24.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_24.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_24);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_24);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_25 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_25.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_25.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_10);
                              _jspx_th_c_when_25.setTest( curEntryColumn.equals("description") );
                              int _jspx_eval_c_when_25 = _jspx_th_c_when_25.doStartTag();
                              if (_jspx_eval_c_when_25 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-text
                              com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_10 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_10.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_25);
                              _jspx_th_liferay$1ui_search$1container$1column$1text_10.setCssClass("table-cell-expand table-cell-minw-200");
                              _jspx_th_liferay$1ui_search$1container$1column$1text_10.setName("description");
                              _jspx_th_liferay$1ui_search$1container$1column$1text_10.setValue( StringUtil.shorten(curFolder.getDescription(), 100) );
                              int _jspx_eval_liferay$1ui_search$1container$1column$1text_10 = _jspx_th_liferay$1ui_search$1container$1column$1text_10.doStartTag();
                              if (_jspx_th_liferay$1ui_search$1container$1column$1text_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_10);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_10);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_25.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_25.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_25);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_25);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_26 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_26.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_26.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_10);
                              _jspx_th_c_when_26.setTest( curEntryColumn.equals("document-type") );
                              int _jspx_eval_c_when_26 = _jspx_th_c_when_26.doStartTag();
                              if (_jspx_eval_c_when_26 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              if (_jspx_meth_liferay$1ui_search$1container$1column$1text_11((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_26, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_26.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_26.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_26);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_26);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_27 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_27.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_27.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_10);
                              _jspx_th_c_when_27.setTest( curEntryColumn.equals("size") );
                              int _jspx_eval_c_when_27 = _jspx_th_c_when_27.doStartTag();
                              if (_jspx_eval_c_when_27 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              if (_jspx_meth_liferay$1ui_search$1container$1column$1text_12((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_27, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_27.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_27.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_27);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_27);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_28 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_28.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_28.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_10);
                              _jspx_th_c_when_28.setTest( curEntryColumn.equals("status") );
                              int _jspx_eval_c_when_28 = _jspx_th_c_when_28.doStartTag();
                              if (_jspx_eval_c_when_28 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              if (_jspx_meth_liferay$1ui_search$1container$1column$1text_13((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_28, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_28.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_28.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_28);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_28);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_29 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_29.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_29.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_10);
                              _jspx_th_c_when_29.setTest( curEntryColumn.equals("downloads") );
                              int _jspx_eval_c_when_29 = _jspx_th_c_when_29.doStartTag();
                              if (_jspx_eval_c_when_29 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              if (_jspx_meth_liferay$1ui_search$1container$1column$1text_14((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_29, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_29.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_29.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_29);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_29);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_30 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_30.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_30.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_10);
                              _jspx_th_c_when_30.setTest( curEntryColumn.equals("create-date") );
                              int _jspx_eval_c_when_30 = _jspx_th_c_when_30.doStartTag();
                              if (_jspx_eval_c_when_30 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-date
                              com.liferay.taglib.ui.SearchContainerColumnDateTag _jspx_th_liferay$1ui_search$1container$1column$1date_2 = (com.liferay.taglib.ui.SearchContainerColumnDateTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnDateTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1date_2.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1date_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_30);
                              _jspx_th_liferay$1ui_search$1container$1column$1date_2.setCssClass("table-cell-expand-smallest table-cell-ws-nowrap");
                              _jspx_th_liferay$1ui_search$1container$1column$1date_2.setName("create-date");
                              _jspx_th_liferay$1ui_search$1container$1column$1date_2.setValue( curFolder.getCreateDate() );
                              int _jspx_eval_liferay$1ui_search$1container$1column$1date_2 = _jspx_th_liferay$1ui_search$1container$1column$1date_2.doStartTag();
                              if (_jspx_th_liferay$1ui_search$1container$1column$1date_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1date_2);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1date_2);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_30.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_30.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_30);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_30);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_31 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_31.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_31.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_10);
                              _jspx_th_c_when_31.setTest( curEntryColumn.equals("modified-date") );
                              int _jspx_eval_c_when_31 = _jspx_th_c_when_31.doStartTag();
                              if (_jspx_eval_c_when_31 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              //  liferay-ui:search-container-column-date
                              com.liferay.taglib.ui.SearchContainerColumnDateTag _jspx_th_liferay$1ui_search$1container$1column$1date_3 = (com.liferay.taglib.ui.SearchContainerColumnDateTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnDateTag.class);
                              _jspx_th_liferay$1ui_search$1container$1column$1date_3.setPageContext(_jspx_page_context);
                              _jspx_th_liferay$1ui_search$1container$1column$1date_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_31);
                              _jspx_th_liferay$1ui_search$1container$1column$1date_3.setCssClass("table-cell-expand-smallest table-cell-ws-nowrap");
                              _jspx_th_liferay$1ui_search$1container$1column$1date_3.setName("modified-date");
                              _jspx_th_liferay$1ui_search$1container$1column$1date_3.setValue( curFolder.getLastPostDate() );
                              int _jspx_eval_liferay$1ui_search$1container$1column$1date_3 = _jspx_th_liferay$1ui_search$1container$1column$1date_3.doStartTag();
                              if (_jspx_th_liferay$1ui_search$1container$1column$1date_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1date_3);
                              return;
                              }
                              _jspx_tagPool_liferay$1ui_search$1container$1column$1date_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1date_3);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_31.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_31.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_31);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_31);
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              //  c:when
                              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_32 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                              _jspx_th_c_when_32.setPageContext(_jspx_page_context);
                              _jspx_th_c_when_32.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_10);
                              _jspx_th_c_when_32.setTest( curEntryColumn.equals("action") );
                              int _jspx_eval_c_when_32 = _jspx_th_c_when_32.doStartTag();
                              if (_jspx_eval_c_when_32 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\n\t\t\t\t\t\t\t\t\t\t");
                              if (_jspx_meth_liferay$1ui_search$1container$1column$1jsp_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_32, _jspx_page_context))
                              return;
                              out.write("\n\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_when_32.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_c_when_32.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_32);
                              return;
                              }
                              _jspx_tagPool_c_when_test.reuse(_jspx_th_c_when_32);
                              out.write("\n\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_c_choose_10.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                            }
                            if (_jspx_th_c_choose_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_10);
                              return;
                            }
                            _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_10);
                            out.write("\n\n\t\t\t\t\t\t\t");

							}
							
                            out.write("\n\n\t\t\t\t\t\t");
                            int evalDoAfterBody = _jspx_th_c_otherwise_5.doAfterBody();
                            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                          } while (true);
                        }
                        if (_jspx_th_c_otherwise_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                          _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_5);
                          return;
                        }
                        _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_5);
                        out.write("\n\t\t\t\t\t");
                        int evalDoAfterBody = _jspx_th_c_choose_9.doAfterBody();
                        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                          break;
                      } while (true);
                    }
                    if (_jspx_th_c_choose_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_9);
                      return;
                    }
                    _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_9);
                    out.write("\n\t\t\t\t");
                    int evalDoAfterBody = _jspx_th_c_otherwise_4.doAfterBody();
                    if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                      break;
                  } while (true);
                }
                if (_jspx_th_c_otherwise_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_4);
                  return;
                }
                _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_4);
                out.write("\n\t\t\t");
                int evalDoAfterBody = _jspx_th_c_choose_0.doAfterBody();
                if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                  break;
              } while (true);
            }
            if (_jspx_th_c_choose_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_0);
              return;
            }
            _jspx_tagPool_c_choose.reuse(_jspx_th_c_choose_0);
            out.write("\n\t\t");
            int evalDoAfterBody = _jspx_th_liferay$1ui_search$1container$1row_0.doAfterBody();
            index = (java.lang.Integer) _jspx_page_context.findAttribute("index");
            result = (Object) _jspx_page_context.findAttribute("result");
            row = (com.liferay.portal.kernel.dao.search.ResultRow) _jspx_page_context.findAttribute("row");
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_liferay$1ui_search$1container$1row_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
            out = _jspx_page_context.popBody();
        }
        if (_jspx_th_liferay$1ui_search$1container$1row_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_className.reuse(_jspx_th_liferay$1ui_search$1container$1row_0);
          return;
        }
        _jspx_tagPool_liferay$1ui_search$1container$1row_modelVar_className.reuse(_jspx_th_liferay$1ui_search$1container$1row_0);
        out.write("\n\n\t\t");
        //  liferay-ui:search-iterator
        com.liferay.taglib.ui.SearchIteratorTag _jspx_th_liferay$1ui_search$1iterator_0 = (com.liferay.taglib.ui.SearchIteratorTag) _jspx_tagPool_liferay$1ui_search$1iterator_searchContainer_resultRowSplitter_markupView_displayStyle_nobody.get(com.liferay.taglib.ui.SearchIteratorTag.class);
        _jspx_th_liferay$1ui_search$1iterator_0.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1ui_search$1iterator_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_search$1container_0);
        _jspx_th_liferay$1ui_search$1iterator_0.setDisplayStyle( displayStyle );
        _jspx_th_liferay$1ui_search$1iterator_0.setMarkupView("lexicon");
        _jspx_th_liferay$1ui_search$1iterator_0.setResultRowSplitter( new DLResultRowSplitter() );
        _jspx_th_liferay$1ui_search$1iterator_0.setSearchContainer( dlSearchContainer );
        int _jspx_eval_liferay$1ui_search$1iterator_0 = _jspx_th_liferay$1ui_search$1iterator_0.doStartTag();
        if (_jspx_th_liferay$1ui_search$1iterator_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1ui_search$1iterator_searchContainer_resultRowSplitter_markupView_displayStyle_nobody.reuse(_jspx_th_liferay$1ui_search$1iterator_0);
          return;
        }
        _jspx_tagPool_liferay$1ui_search$1iterator_searchContainer_resultRowSplitter_markupView_displayStyle_nobody.reuse(_jspx_th_liferay$1ui_search$1iterator_0);
        out.write('\n');
        out.write('	');
      }
      if (_jspx_th_liferay$1ui_search$1container_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_liferay$1ui_search$1container_total_searchContainer_id.reuse(_jspx_th_liferay$1ui_search$1container_0);
        return;
      }
      _jspx_tagPool_liferay$1ui_search$1container_total_searchContainer_id.reuse(_jspx_th_liferay$1ui_search$1container_0);
      out.write("\n</div>\n\n");

request.setAttribute("edit_file_entry.jsp-checkedOut", true);

      out.write('\n');
      out.write('\n');
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_2 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_2.setPageContext(_jspx_page_context);
      _jspx_th_c_if_2.setParent(null);
      _jspx_th_c_if_2.setTest( dlAdminDisplayContext.isVersioningStrategyOverridable() );
      int _jspx_eval_c_if_2 = _jspx_th_c_if_2.doStartTag();
      if (_jspx_eval_c_if_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write('\n');
          out.write('	');
          //  liferay-util:include
          com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_0 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
          _jspx_th_liferay$1util_include_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1util_include_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_2);
          _jspx_th_liferay$1util_include_0.setPage("/document_library/version_details.jsp");
          _jspx_th_liferay$1util_include_0.setServletContext( application );
          int _jspx_eval_liferay$1util_include_0 = _jspx_th_liferay$1util_include_0.doStartTag();
          if (_jspx_th_liferay$1util_include_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
            return;
          }
          _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
          out.write('\n');
          int evalDoAfterBody = _jspx_th_c_if_2.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_c_if_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_2);
        return;
      }
      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_2);
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }

  private boolean _jspx_meth_portlet_namespace_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_0 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_0.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_0.setParent(null);
    int _jspx_eval_portlet_namespace_0 = _jspx_th_portlet_namespace_0.doStartTag();
    if (_jspx_th_portlet_namespace_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_search$1container$1column$1jsp_1(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:search-container-column-jsp
    com.liferay.taglib.ui.SearchContainerColumnJSPTag _jspx_th_liferay$1ui_search$1container$1column$1jsp_1 = (com.liferay.taglib.ui.SearchContainerColumnJSPTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.get(com.liferay.taglib.ui.SearchContainerColumnJSPTag.class);
    _jspx_th_liferay$1ui_search$1container$1column$1jsp_1.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_search$1container$1column$1jsp_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
    _jspx_th_liferay$1ui_search$1container$1column$1jsp_1.setPath("/document_library/file_entry_action.jsp");
    int _jspx_eval_liferay$1ui_search$1container$1column$1jsp_1 = _jspx_th_liferay$1ui_search$1container$1column$1jsp_1.doStartTag();
    if (_jspx_th_liferay$1ui_search$1container$1column$1jsp_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1jsp_1);
      return true;
    }
    _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1jsp_1);
    return false;
  }

  private boolean _jspx_meth_aui_icon_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_5, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:icon
    com.liferay.taglib.aui.IconTag _jspx_th_aui_icon_0 = (com.liferay.taglib.aui.IconTag) _jspx_tagPool_aui_icon_message_markupView_image_nobody.get(com.liferay.taglib.aui.IconTag.class);
    _jspx_th_aui_icon_0.setPageContext(_jspx_page_context);
    _jspx_th_aui_icon_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_5);
    _jspx_th_aui_icon_0.setImage("shortcut");
    _jspx_th_aui_icon_0.setMarkupView("lexicon");
    _jspx_th_aui_icon_0.setDynamicAttribute(null, "message", new String("shortcut"));
    int _jspx_eval_aui_icon_0 = _jspx_th_aui_icon_0.doStartTag();
    if (_jspx_th_aui_icon_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_icon_message_markupView_image_nobody.reuse(_jspx_th_aui_icon_0);
      return true;
    }
    _jspx_tagPool_aui_icon_message_markupView_image_nobody.reuse(_jspx_th_aui_icon_0);
    return false;
  }

  private boolean _jspx_meth_aui_icon_1(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_6, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:icon
    com.liferay.taglib.aui.IconTag _jspx_th_aui_icon_1 = (com.liferay.taglib.aui.IconTag) _jspx_tagPool_aui_icon_message_markupView_image_nobody.get(com.liferay.taglib.aui.IconTag.class);
    _jspx_th_aui_icon_1.setPageContext(_jspx_page_context);
    _jspx_th_aui_icon_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_6);
    _jspx_th_aui_icon_1.setImage("lock");
    _jspx_th_aui_icon_1.setMarkupView("lexicon");
    _jspx_th_aui_icon_1.setDynamicAttribute(null, "message", new String("locked"));
    int _jspx_eval_aui_icon_1 = _jspx_th_aui_icon_1.doStartTag();
    if (_jspx_th_aui_icon_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_icon_message_markupView_image_nobody.reuse(_jspx_th_aui_icon_1);
      return true;
    }
    _jspx_tagPool_aui_icon_message_markupView_image_nobody.reuse(_jspx_th_aui_icon_1);
    return false;
  }

  private boolean _jspx_meth_aui_icon_2(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_8, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:icon
    com.liferay.taglib.aui.IconTag _jspx_th_aui_icon_2 = (com.liferay.taglib.aui.IconTag) _jspx_tagPool_aui_icon_message_markupView_image_nobody.get(com.liferay.taglib.aui.IconTag.class);
    _jspx_th_aui_icon_2.setPageContext(_jspx_page_context);
    _jspx_th_aui_icon_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_8);
    _jspx_th_aui_icon_2.setImage("shortcut");
    _jspx_th_aui_icon_2.setMarkupView("lexicon");
    _jspx_th_aui_icon_2.setDynamicAttribute(null, "message", new String("shortcut"));
    int _jspx_eval_aui_icon_2 = _jspx_th_aui_icon_2.doStartTag();
    if (_jspx_th_aui_icon_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_icon_message_markupView_image_nobody.reuse(_jspx_th_aui_icon_2);
      return true;
    }
    _jspx_tagPool_aui_icon_message_markupView_image_nobody.reuse(_jspx_th_aui_icon_2);
    return false;
  }

  private boolean _jspx_meth_aui_icon_3(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_9, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:icon
    com.liferay.taglib.aui.IconTag _jspx_th_aui_icon_3 = (com.liferay.taglib.aui.IconTag) _jspx_tagPool_aui_icon_message_markupView_image_nobody.get(com.liferay.taglib.aui.IconTag.class);
    _jspx_th_aui_icon_3.setPageContext(_jspx_page_context);
    _jspx_th_aui_icon_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_9);
    _jspx_th_aui_icon_3.setImage("lock");
    _jspx_th_aui_icon_3.setMarkupView("lexicon");
    _jspx_th_aui_icon_3.setDynamicAttribute(null, "message", new String("locked"));
    int _jspx_eval_aui_icon_3 = _jspx_th_aui_icon_3.doStartTag();
    if (_jspx_th_aui_icon_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_icon_message_markupView_image_nobody.reuse(_jspx_th_aui_icon_3);
      return true;
    }
    _jspx_tagPool_aui_icon_message_markupView_image_nobody.reuse(_jspx_th_aui_icon_3);
    return false;
  }

  private boolean _jspx_meth_aui_icon_4(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_10, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:icon
    com.liferay.taglib.aui.IconTag _jspx_th_aui_icon_4 = (com.liferay.taglib.aui.IconTag) _jspx_tagPool_aui_icon_message_markupView_image_nobody.get(com.liferay.taglib.aui.IconTag.class);
    _jspx_th_aui_icon_4.setPageContext(_jspx_page_context);
    _jspx_th_aui_icon_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_10);
    _jspx_th_aui_icon_4.setImage("shortcut");
    _jspx_th_aui_icon_4.setMarkupView("lexicon");
    _jspx_th_aui_icon_4.setDynamicAttribute(null, "message", new String("shortcut"));
    int _jspx_eval_aui_icon_4 = _jspx_th_aui_icon_4.doStartTag();
    if (_jspx_th_aui_icon_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_icon_message_markupView_image_nobody.reuse(_jspx_th_aui_icon_4);
      return true;
    }
    _jspx_tagPool_aui_icon_message_markupView_image_nobody.reuse(_jspx_th_aui_icon_4);
    return false;
  }

  private boolean _jspx_meth_aui_icon_5(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_11, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:icon
    com.liferay.taglib.aui.IconTag _jspx_th_aui_icon_5 = (com.liferay.taglib.aui.IconTag) _jspx_tagPool_aui_icon_message_markupView_image_nobody.get(com.liferay.taglib.aui.IconTag.class);
    _jspx_th_aui_icon_5.setPageContext(_jspx_page_context);
    _jspx_th_aui_icon_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_11);
    _jspx_th_aui_icon_5.setImage("lock");
    _jspx_th_aui_icon_5.setMarkupView("lexicon");
    _jspx_th_aui_icon_5.setDynamicAttribute(null, "message", new String("locked"));
    int _jspx_eval_aui_icon_5 = _jspx_th_aui_icon_5.doStartTag();
    if (_jspx_th_aui_icon_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_icon_message_markupView_image_nobody.reuse(_jspx_th_aui_icon_5);
      return true;
    }
    _jspx_tagPool_aui_icon_message_markupView_image_nobody.reuse(_jspx_th_aui_icon_5);
    return false;
  }

  private boolean _jspx_meth_aui_icon_6(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:icon
    com.liferay.taglib.aui.IconTag _jspx_th_aui_icon_6 = (com.liferay.taglib.aui.IconTag) _jspx_tagPool_aui_icon_message_markupView_image_cssClass_nobody.get(com.liferay.taglib.aui.IconTag.class);
    _jspx_th_aui_icon_6.setPageContext(_jspx_page_context);
    _jspx_th_aui_icon_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
    _jspx_th_aui_icon_6.setCssClass("inline-item inline-item-after");
    _jspx_th_aui_icon_6.setImage("lock");
    _jspx_th_aui_icon_6.setMarkupView("lexicon");
    _jspx_th_aui_icon_6.setDynamicAttribute(null, "message", new String("locked"));
    int _jspx_eval_aui_icon_6 = _jspx_th_aui_icon_6.doStartTag();
    if (_jspx_th_aui_icon_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_icon_message_markupView_image_cssClass_nobody.reuse(_jspx_th_aui_icon_6);
      return true;
    }
    _jspx_tagPool_aui_icon_message_markupView_image_cssClass_nobody.reuse(_jspx_th_aui_icon_6);
    return false;
  }

  private boolean _jspx_meth_aui_icon_7(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:icon
    com.liferay.taglib.aui.IconTag _jspx_th_aui_icon_7 = (com.liferay.taglib.aui.IconTag) _jspx_tagPool_aui_icon_message_markupView_image_cssClass_nobody.get(com.liferay.taglib.aui.IconTag.class);
    _jspx_th_aui_icon_7.setPageContext(_jspx_page_context);
    _jspx_th_aui_icon_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
    _jspx_th_aui_icon_7.setCssClass("inline-item inline-item-after");
    _jspx_th_aui_icon_7.setImage("shortcut");
    _jspx_th_aui_icon_7.setMarkupView("lexicon");
    _jspx_th_aui_icon_7.setDynamicAttribute(null, "message", new String("shortcut"));
    int _jspx_eval_aui_icon_7 = _jspx_th_aui_icon_7.doStartTag();
    if (_jspx_th_aui_icon_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_icon_message_markupView_image_cssClass_nobody.reuse(_jspx_th_aui_icon_7);
      return true;
    }
    _jspx_tagPool_aui_icon_message_markupView_image_cssClass_nobody.reuse(_jspx_th_aui_icon_7);
    return false;
  }

  private boolean _jspx_meth_c_otherwise_3(javax.servlet.jsp.tagext.JspTag _jspx_th_c_choose_8, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:otherwise
    org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_3 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
    _jspx_th_c_otherwise_3.setPageContext(_jspx_page_context);
    _jspx_th_c_otherwise_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_8);
    int _jspx_eval_c_otherwise_3 = _jspx_th_c_otherwise_3.doStartTag();
    if (_jspx_eval_c_otherwise_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
        if (_jspx_meth_liferay$1ui_search$1container$1column$1text_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_otherwise_3, _jspx_page_context))
          return true;
        out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
        int evalDoAfterBody = _jspx_th_c_otherwise_3.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_c_otherwise_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_3);
      return true;
    }
    _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_3);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_search$1container$1column$1text_5(javax.servlet.jsp.tagext.JspTag _jspx_th_c_otherwise_3, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:search-container-column-text
    com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_5 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
    _jspx_th_liferay$1ui_search$1container$1column$1text_5.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_search$1container$1column$1text_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_3);
    _jspx_th_liferay$1ui_search$1container$1column$1text_5.setCssClass("table-cell-expand-smaller table-cell-minw-150");
    _jspx_th_liferay$1ui_search$1container$1column$1text_5.setName("document-type");
    _jspx_th_liferay$1ui_search$1container$1column$1text_5.setValue("--");
    int _jspx_eval_liferay$1ui_search$1container$1column$1text_5 = _jspx_th_liferay$1ui_search$1container$1column$1text_5.doStartTag();
    if (_jspx_th_liferay$1ui_search$1container$1column$1text_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_5);
      return true;
    }
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_5);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_search$1container$1column$1jsp_2(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_21, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:search-container-column-jsp
    com.liferay.taglib.ui.SearchContainerColumnJSPTag _jspx_th_liferay$1ui_search$1container$1column$1jsp_2 = (com.liferay.taglib.ui.SearchContainerColumnJSPTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.get(com.liferay.taglib.ui.SearchContainerColumnJSPTag.class);
    _jspx_th_liferay$1ui_search$1container$1column$1jsp_2.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_search$1container$1column$1jsp_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_21);
    _jspx_th_liferay$1ui_search$1container$1column$1jsp_2.setPath("/document_library/file_entry_action.jsp");
    int _jspx_eval_liferay$1ui_search$1container$1column$1jsp_2 = _jspx_th_liferay$1ui_search$1container$1column$1jsp_2.doStartTag();
    if (_jspx_th_liferay$1ui_search$1container$1column$1jsp_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1jsp_2);
      return true;
    }
    _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1jsp_2);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_search$1container$1column$1jsp_4(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_22, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:search-container-column-jsp
    com.liferay.taglib.ui.SearchContainerColumnJSPTag _jspx_th_liferay$1ui_search$1container$1column$1jsp_4 = (com.liferay.taglib.ui.SearchContainerColumnJSPTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.get(com.liferay.taglib.ui.SearchContainerColumnJSPTag.class);
    _jspx_th_liferay$1ui_search$1container$1column$1jsp_4.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_search$1container$1column$1jsp_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_22);
    _jspx_th_liferay$1ui_search$1container$1column$1jsp_4.setPath("/document_library/folder_action.jsp");
    int _jspx_eval_liferay$1ui_search$1container$1column$1jsp_4 = _jspx_th_liferay$1ui_search$1container$1column$1jsp_4.doStartTag();
    if (_jspx_th_liferay$1ui_search$1container$1column$1jsp_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1jsp_4);
      return true;
    }
    _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1jsp_4);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_search$1container$1column$1text_11(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_26, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:search-container-column-text
    com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_11 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
    _jspx_th_liferay$1ui_search$1container$1column$1text_11.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_search$1container$1column$1text_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_26);
    _jspx_th_liferay$1ui_search$1container$1column$1text_11.setCssClass("table-cell-expand-smaller");
    _jspx_th_liferay$1ui_search$1container$1column$1text_11.setName("document-type");
    _jspx_th_liferay$1ui_search$1container$1column$1text_11.setValue("--");
    int _jspx_eval_liferay$1ui_search$1container$1column$1text_11 = _jspx_th_liferay$1ui_search$1container$1column$1text_11.doStartTag();
    if (_jspx_th_liferay$1ui_search$1container$1column$1text_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_11);
      return true;
    }
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_11);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_search$1container$1column$1text_12(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_27, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:search-container-column-text
    com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_12 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
    _jspx_th_liferay$1ui_search$1container$1column$1text_12.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_search$1container$1column$1text_12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_27);
    _jspx_th_liferay$1ui_search$1container$1column$1text_12.setCssClass("table-cell-expand-smallest");
    _jspx_th_liferay$1ui_search$1container$1column$1text_12.setName("size");
    _jspx_th_liferay$1ui_search$1container$1column$1text_12.setValue("--");
    int _jspx_eval_liferay$1ui_search$1container$1column$1text_12 = _jspx_th_liferay$1ui_search$1container$1column$1text_12.doStartTag();
    if (_jspx_th_liferay$1ui_search$1container$1column$1text_12.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_12);
      return true;
    }
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_12);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_search$1container$1column$1text_13(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_28, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:search-container-column-text
    com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_13 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
    _jspx_th_liferay$1ui_search$1container$1column$1text_13.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_search$1container$1column$1text_13.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_28);
    _jspx_th_liferay$1ui_search$1container$1column$1text_13.setCssClass("table-cell-expand-smallest");
    _jspx_th_liferay$1ui_search$1container$1column$1text_13.setName("status");
    _jspx_th_liferay$1ui_search$1container$1column$1text_13.setValue("--");
    int _jspx_eval_liferay$1ui_search$1container$1column$1text_13 = _jspx_th_liferay$1ui_search$1container$1column$1text_13.doStartTag();
    if (_jspx_th_liferay$1ui_search$1container$1column$1text_13.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_13);
      return true;
    }
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_13);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_search$1container$1column$1text_14(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_29, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:search-container-column-text
    com.liferay.taglib.ui.SearchContainerColumnTextTag _jspx_th_liferay$1ui_search$1container$1column$1text_14 = (com.liferay.taglib.ui.SearchContainerColumnTextTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.get(com.liferay.taglib.ui.SearchContainerColumnTextTag.class);
    _jspx_th_liferay$1ui_search$1container$1column$1text_14.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_search$1container$1column$1text_14.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_29);
    _jspx_th_liferay$1ui_search$1container$1column$1text_14.setCssClass("table-cell-expand-smallest");
    _jspx_th_liferay$1ui_search$1container$1column$1text_14.setName("downloads");
    _jspx_th_liferay$1ui_search$1container$1column$1text_14.setValue("--");
    int _jspx_eval_liferay$1ui_search$1container$1column$1text_14 = _jspx_th_liferay$1ui_search$1container$1column$1text_14.doStartTag();
    if (_jspx_th_liferay$1ui_search$1container$1column$1text_14.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_14);
      return true;
    }
    _jspx_tagPool_liferay$1ui_search$1container$1column$1text_value_name_cssClass_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1text_14);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_search$1container$1column$1jsp_5(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_32, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:search-container-column-jsp
    com.liferay.taglib.ui.SearchContainerColumnJSPTag _jspx_th_liferay$1ui_search$1container$1column$1jsp_5 = (com.liferay.taglib.ui.SearchContainerColumnJSPTag) _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.get(com.liferay.taglib.ui.SearchContainerColumnJSPTag.class);
    _jspx_th_liferay$1ui_search$1container$1column$1jsp_5.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_search$1container$1column$1jsp_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_32);
    _jspx_th_liferay$1ui_search$1container$1column$1jsp_5.setPath("/document_library/folder_action.jsp");
    int _jspx_eval_liferay$1ui_search$1container$1column$1jsp_5 = _jspx_th_liferay$1ui_search$1container$1column$1jsp_5.doStartTag();
    if (_jspx_th_liferay$1ui_search$1container$1column$1jsp_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1jsp_5);
      return true;
    }
    _jspx_tagPool_liferay$1ui_search$1container$1column$1jsp_path_nobody.reuse(_jspx_th_liferay$1ui_search$1container$1column$1jsp_5);
    return false;
  }
}
