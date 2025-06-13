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

public final class view_005ffile_005fentry_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList<String>(4);
    _jspx_dependants.add("/document_library/init.jsp");
    _jspx_dependants.add("/init.jsp");
    _jspx_dependants.add("/init-ext.jsp");
    _jspx_dependants.add("/document_library/init-ext.jsp");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_toolbar$1item_toolbarItem_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_param_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_if_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1comment_discussion_userId_redirect_ratingsEnabled_formName_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_script;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_type_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_buffer_var;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_choose;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_include_servletContext_page_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_when_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_namespace_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_key_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_input_value_type_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_otherwise;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_renderURL_windowState_var;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_form_name_method_action;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_sidebar$1panel;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1theme_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_label_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_actionURL_var_name_nobody;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_liferay$1ui_toolbar$1item_toolbarItem_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_param_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_if_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1comment_discussion_userId_redirect_ratingsEnabled_formName_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_script = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_type_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_buffer_var = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_choose = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_when_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_namespace_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_key_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_input_value_type_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_otherwise = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_renderURL_windowState_var = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_form_name_method_action = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_sidebar$1panel = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1theme_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_label_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_actionURL_var_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_liferay$1ui_toolbar$1item_toolbarItem_nobody.release();
    _jspx_tagPool_portlet_param_value_name_nobody.release();
    _jspx_tagPool_c_if_test.release();
    _jspx_tagPool_liferay$1comment_discussion_userId_redirect_ratingsEnabled_formName_classPK_className_nobody.release();
    _jspx_tagPool_aui_script.release();
    _jspx_tagPool_aui_input_type_name_nobody.release();
    _jspx_tagPool_liferay$1util_buffer_var.release();
    _jspx_tagPool_c_choose.release();
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.release();
    _jspx_tagPool_c_when_test.release();
    _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.release();
    _jspx_tagPool_portlet_namespace_nobody.release();
    _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody.release();
    _jspx_tagPool_liferay$1ui_message_key_nobody.release();
    _jspx_tagPool_aui_input_value_type_name_nobody.release();
    _jspx_tagPool_c_otherwise.release();
    _jspx_tagPool_portlet_renderURL_windowState_var.release();
    _jspx_tagPool_portlet_defineObjects_nobody.release();
    _jspx_tagPool_aui_form_name_method_action.release();
    _jspx_tagPool_liferay$1frontend_sidebar$1panel.release();
    _jspx_tagPool_liferay$1theme_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_label_nobody.release();
    _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.release();
    _jspx_tagPool_portlet_actionURL_var_name_nobody.release();
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
      if (_jspx_meth_liferay$1util_dynamic$1include_0(_jspx_page_context))
        return;
      out.write('\n');
      out.write('\n');

String redirect = ParamUtil.getString(request, "redirect");

FileEntry fileEntry = (FileEntry)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY);

long folderId = fileEntry.getFolderId();

if (Validator.isNull(redirect)) {
	PortletURL portletURL = renderResponse.createRenderURL();

	long parentFolderId = folderId;

	if (!DLFolderPermission.contains(permissionChecker, fileEntry.getGroupId(), parentFolderId, ActionKeys.VIEW)) {
		parentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}

	portletURL.setParameter("mvcRenderCommandName", (parentFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) ? "/document_library/view" : "/document_library/view_folder");
	portletURL.setParameter("folderId", String.valueOf(parentFolderId));

	redirect = portletURL.toString();
}

Folder folder = fileEntry.getFolder();
FileVersion fileVersion = (FileVersion)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_VERSION);

boolean versionSpecific = false;

if (fileVersion != null) {
	versionSpecific = true;
}
else if ((user.getUserId() == fileEntry.getUserId()) || permissionChecker.isContentReviewer(user.getCompanyId(), scopeGroupId) || DLFileEntryPermission.contains(permissionChecker, fileEntry, ActionKeys.UPDATE)) {
	fileVersion = fileEntry.getLatestFileVersion();
}
else {
	fileVersion = fileEntry.getFileVersion();
}

com.liferay.portal.kernel.lock.Lock lock = fileEntry.getLock();

AssetEntry layoutAssetEntry = AssetEntryLocalServiceUtil.fetchEntry(DLFileEntryConstants.getClassName(), DLAssetHelperUtil.getAssetClassPK(fileEntry, fileVersion));

request.setAttribute(WebKeys.LAYOUT_ASSET_ENTRY, layoutAssetEntry);

DLPortletInstanceSettingsHelper dlPortletInstanceSettingsHelper = new DLPortletInstanceSettingsHelper(dlRequestHelper);
final DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext = dlDisplayContextProvider.getDLViewFileVersionDisplayContext(request, response, fileVersion);

boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation"));

if (portletTitleBasedNavigation) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);

	renderResponse.setTitle(fileVersion.getTitle());
}

      out.write('\n');
      out.write('\n');
      //  liferay-util:buffer
      com.liferay.taglib.util.BufferTag _jspx_th_liferay$1util_buffer_0 = (com.liferay.taglib.util.BufferTag) _jspx_tagPool_liferay$1util_buffer_var.get(com.liferay.taglib.util.BufferTag.class);
      _jspx_th_liferay$1util_buffer_0.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1util_buffer_0.setParent(null);
      _jspx_th_liferay$1util_buffer_0.setVar("documentTitle");
      int _jspx_eval_liferay$1util_buffer_0 = _jspx_th_liferay$1util_buffer_0.doStartTag();
      if (_jspx_eval_liferay$1util_buffer_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_liferay$1util_buffer_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_liferay$1util_buffer_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_liferay$1util_buffer_0.doInitBody();
        }
        do {
          out.write('\n');
          out.write('	');
          out.print( fileVersion.getTitle() );
          out.write("\n\n\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_0 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_0.setPageContext(_jspx_page_context);
          _jspx_th_c_if_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1util_buffer_0);
          _jspx_th_c_if_0.setTest( versionSpecific );
          int _jspx_eval_c_if_0 = _jspx_th_c_if_0.doStartTag();
          if (_jspx_eval_c_if_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t(");
              if (_jspx_meth_liferay$1ui_message_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_0, _jspx_page_context))
                return;
              out.write(' ');
              out.print( fileVersion.getVersion() );
              out.write(")\n\t");
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
          out.write('\n');
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
      java.lang.String documentTitle = null;
      documentTitle = (java.lang.String) _jspx_page_context.findAttribute("documentTitle");
      out.write('\n');
      out.write('\n');
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_1 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_1.setPageContext(_jspx_page_context);
      _jspx_th_c_if_1.setParent(null);
      _jspx_th_c_if_1.setTest( portletTitleBasedNavigation );
      int _jspx_eval_c_if_1 = _jspx_th_c_if_1.doStartTag();
      if (_jspx_eval_c_if_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\n\t");

	request.setAttribute("file_entry_upper_tbar.jsp-dlViewFileVersionDisplayContext", dlViewFileVersionDisplayContext);
	request.setAttribute("file_entry_upper_tbar.jsp-documentTitle", documentTitle);
	request.setAttribute("file_entry_upper_tbar.jsp-fileEntry", fileEntry);
	request.setAttribute("file_entry_upper_tbar.jsp-fileVersion", fileVersion);
	request.setAttribute("file_entry_upper_tbar.jsp-versionSpecific", versionSpecific);
	
          out.write("\n\n\t");
          //  liferay-util:include
          com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_0 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
          _jspx_th_liferay$1util_include_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1util_include_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
          _jspx_th_liferay$1util_include_0.setPage("/document_library/file_entry_upper_tbar.jsp");
          _jspx_th_liferay$1util_include_0.setServletContext( application );
          int _jspx_eval_liferay$1util_include_0 = _jspx_th_liferay$1util_include_0.doStartTag();
          if (_jspx_th_liferay$1util_include_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
            return;
          }
          _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
          out.write('\n');
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
      out.write('\n');
      out.write('\n');
      //  c:choose
      org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_0 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
      _jspx_th_c_choose_0.setPageContext(_jspx_page_context);
      _jspx_th_c_choose_0.setParent(null);
      int _jspx_eval_c_choose_0 = _jspx_th_c_choose_0.doStartTag();
      if (_jspx_eval_c_choose_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write('\n');
          out.write('	');
          //  c:when
          org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_0 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
          _jspx_th_c_when_0.setPageContext(_jspx_page_context);
          _jspx_th_c_when_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
          _jspx_th_c_when_0.setTest( portletTitleBasedNavigation );
          int _jspx_eval_c_when_0 = _jspx_th_c_when_0.doStartTag();
          if (_jspx_eval_c_when_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n<div class=\"container-fluid-1280\" id=\"");
              if (_jspx_meth_portlet_namespace_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_0, _jspx_page_context))
                return;
              out.write("FileEntry\">\n\t");
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
          out.write('\n');
          out.write('	');
          if (_jspx_meth_c_otherwise_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_choose_0, _jspx_page_context))
            return;
          out.write('\n');
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
      out.write("\n\n\t");
      //  portlet:actionURL
      com.liferay.taglib.portlet.ActionURLTag _jspx_th_portlet_actionURL_0 = (com.liferay.taglib.portlet.ActionURLTag) _jspx_tagPool_portlet_actionURL_var_name_nobody.get(com.liferay.taglib.portlet.ActionURLTag.class);
      _jspx_th_portlet_actionURL_0.setPageContext(_jspx_page_context);
      _jspx_th_portlet_actionURL_0.setParent(null);
      _jspx_th_portlet_actionURL_0.setName("/document_library/edit_file_entry");
      _jspx_th_portlet_actionURL_0.setVar("editFileEntry");
      int _jspx_eval_portlet_actionURL_0 = _jspx_th_portlet_actionURL_0.doStartTag();
      if (_jspx_th_portlet_actionURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_portlet_actionURL_var_name_nobody.reuse(_jspx_th_portlet_actionURL_0);
        return;
      }
      _jspx_tagPool_portlet_actionURL_var_name_nobody.reuse(_jspx_th_portlet_actionURL_0);
      java.lang.String editFileEntry = null;
      editFileEntry = (java.lang.String) _jspx_page_context.findAttribute("editFileEntry");
      out.write("\n\n\t");
      //  aui:form
      com.liferay.taglib.aui.FormTag _jspx_th_aui_form_0 = (com.liferay.taglib.aui.FormTag) _jspx_tagPool_aui_form_name_method_action.get(com.liferay.taglib.aui.FormTag.class);
      _jspx_th_aui_form_0.setPageContext(_jspx_page_context);
      _jspx_th_aui_form_0.setParent(null);
      _jspx_th_aui_form_0.setAction( editFileEntry );
      _jspx_th_aui_form_0.setMethod("post");
      _jspx_th_aui_form_0.setName("fm");
      int _jspx_eval_aui_form_0 = _jspx_th_aui_form_0.doStartTag();
      if (_jspx_eval_aui_form_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n\t\t");
        //  aui:input
        com.liferay.taglib.aui.InputTag _jspx_th_aui_input_0 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
        _jspx_th_aui_input_0.setPageContext(_jspx_page_context);
        _jspx_th_aui_input_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_input_0.setName( Constants.CMD );
        _jspx_th_aui_input_0.setType("hidden");
        int _jspx_eval_aui_input_0 = _jspx_th_aui_input_0.doStartTag();
        if (_jspx_th_aui_input_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_0);
          return;
        }
        _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_0);
        out.write("\n\t\t");
        //  aui:input
        com.liferay.taglib.aui.InputTag _jspx_th_aui_input_1 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
        _jspx_th_aui_input_1.setPageContext(_jspx_page_context);
        _jspx_th_aui_input_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_input_1.setName("redirect");
        _jspx_th_aui_input_1.setType("hidden");
        _jspx_th_aui_input_1.setValue( currentURL );
        int _jspx_eval_aui_input_1 = _jspx_th_aui_input_1.doStartTag();
        if (_jspx_th_aui_input_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_1);
          return;
        }
        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_1);
        out.write("\n\t\t");
        //  aui:input
        com.liferay.taglib.aui.InputTag _jspx_th_aui_input_2 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_value_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
        _jspx_th_aui_input_2.setPageContext(_jspx_page_context);
        _jspx_th_aui_input_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
        _jspx_th_aui_input_2.setName("fileEntryId");
        _jspx_th_aui_input_2.setType("hidden");
        _jspx_th_aui_input_2.setValue( String.valueOf(fileEntry.getFileEntryId()) );
        int _jspx_eval_aui_input_2 = _jspx_th_aui_input_2.doStartTag();
        if (_jspx_th_aui_input_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_2);
          return;
        }
        _jspx_tagPool_aui_input_value_type_name_nobody.reuse(_jspx_th_aui_input_2);
        out.write("\n\t\t");
        if (_jspx_meth_aui_input_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
          return;
        out.write("\n\t\t");
        if (_jspx_meth_aui_input_4((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
          return;
        out.write("\n\t\t");
        if (_jspx_meth_aui_input_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
          return;
        out.write("\n\t\t");
        if (_jspx_meth_aui_input_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_form_0, _jspx_page_context))
          return;
        out.write('\n');
        out.write('	');
      }
      if (_jspx_th_aui_form_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_aui_form_name_method_action.reuse(_jspx_th_aui_form_0);
        return;
      }
      _jspx_tagPool_aui_form_name_method_action.reuse(_jspx_th_aui_form_0);
      out.write("\n\n\t");
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_2 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_2.setPageContext(_jspx_page_context);
      _jspx_th_c_if_2.setParent(null);
      _jspx_th_c_if_2.setTest( !portletTitleBasedNavigation && showHeader && (folder != null) );
      int _jspx_eval_c_if_2 = _jspx_th_c_if_2.doStartTag();
      if (_jspx_eval_c_if_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\t\t");
          //  liferay-ui:header
          com.liferay.taglib.ui.HeaderTag _jspx_th_liferay$1ui_header_0 = (com.liferay.taglib.ui.HeaderTag) _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody.get(com.liferay.taglib.ui.HeaderTag.class);
          _jspx_th_liferay$1ui_header_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1ui_header_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_2);
          _jspx_th_liferay$1ui_header_0.setBackURL( redirect );
          _jspx_th_liferay$1ui_header_0.setLocalizeTitle( false );
          _jspx_th_liferay$1ui_header_0.setTitle( documentTitle );
          int _jspx_eval_liferay$1ui_header_0 = _jspx_th_liferay$1ui_header_0.doStartTag();
          if (_jspx_th_liferay$1ui_header_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody.reuse(_jspx_th_liferay$1ui_header_0);
            return;
          }
          _jspx_tagPool_liferay$1ui_header_title_localizeTitle_backURL_nobody.reuse(_jspx_th_liferay$1ui_header_0);
          out.write('\n');
          out.write('	');
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
      out.write("\n\n\t");
      //  c:choose
      org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_1 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
      _jspx_th_c_choose_1.setPageContext(_jspx_page_context);
      _jspx_th_c_choose_1.setParent(null);
      int _jspx_eval_c_choose_1 = _jspx_th_c_choose_1.doStartTag();
      if (_jspx_eval_c_choose_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\t\t");
          //  c:when
          org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_1 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
          _jspx_th_c_when_1.setPageContext(_jspx_page_context);
          _jspx_th_c_when_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
          _jspx_th_c_when_1.setTest( portletTitleBasedNavigation );
          int _jspx_eval_c_when_1 = _jspx_th_c_when_1.doStartTag();
          if (_jspx_eval_c_when_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t<div class=\"contextual-sidebar sidebar-light sidebar-preview\">\n\n\t\t\t\t");

				request.setAttribute("info_panel.jsp-fileEntry", fileEntry);
				request.setAttribute("info_panel.jsp-fileVersion", fileVersion);
				request.setAttribute("info_panel_file_entry.jsp-hideActions", true);
				
              out.write("\n\n\t\t\t\t");
              //  liferay-util:include
              com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_1 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
              _jspx_th_liferay$1util_include_1.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1util_include_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
              _jspx_th_liferay$1util_include_1.setPage("/document_library/info_panel_file_entry.jsp");
              _jspx_th_liferay$1util_include_1.setServletContext( application );
              int _jspx_eval_liferay$1util_include_1 = _jspx_th_liferay$1util_include_1.doStartTag();
              if (_jspx_th_liferay$1util_include_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_1);
                return;
              }
              _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_1);
              out.write("\n\t\t\t</div>\n\t\t");
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
          out.write("\n\t\t");
          //  c:otherwise
          org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_1 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
          _jspx_th_c_otherwise_1.setPageContext(_jspx_page_context);
          _jspx_th_c_otherwise_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
          int _jspx_eval_c_otherwise_1 = _jspx_th_c_otherwise_1.doStartTag();
          if (_jspx_eval_c_otherwise_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t");
              //  liferay-frontend:sidebar-panel
              com.liferay.frontend.taglib.servlet.taglib.SidebarPanelTag _jspx_th_liferay$1frontend_sidebar$1panel_0 = (com.liferay.frontend.taglib.servlet.taglib.SidebarPanelTag) _jspx_tagPool_liferay$1frontend_sidebar$1panel.get(com.liferay.frontend.taglib.servlet.taglib.SidebarPanelTag.class);
              _jspx_th_liferay$1frontend_sidebar$1panel_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1frontend_sidebar$1panel_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_1);
              int _jspx_eval_liferay$1frontend_sidebar$1panel_0 = _jspx_th_liferay$1frontend_sidebar$1panel_0.doStartTag();
              if (_jspx_eval_liferay$1frontend_sidebar$1panel_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\n\t\t\t\t");

				request.setAttribute("info_panel.jsp-fileEntry", fileEntry);
				request.setAttribute("info_panel.jsp-fileVersion", fileVersion);
				
                out.write("\n\n\t\t\t\t");
                //  liferay-util:include
                com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_2 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
                _jspx_th_liferay$1util_include_2.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1util_include_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1frontend_sidebar$1panel_0);
                _jspx_th_liferay$1util_include_2.setPage("/document_library/info_panel_file_entry.jsp");
                _jspx_th_liferay$1util_include_2.setServletContext( application );
                int _jspx_eval_liferay$1util_include_2 = _jspx_th_liferay$1util_include_2.doStartTag();
                if (_jspx_th_liferay$1util_include_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_2);
                  return;
                }
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_2);
                out.write("\n\t\t\t");
              }
              if (_jspx_th_liferay$1frontend_sidebar$1panel_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1frontend_sidebar$1panel.reuse(_jspx_th_liferay$1frontend_sidebar$1panel_0);
                return;
              }
              _jspx_tagPool_liferay$1frontend_sidebar$1panel.reuse(_jspx_th_liferay$1frontend_sidebar$1panel_0);
              out.write("\n\t\t");
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
          out.write('\n');
          out.write('	');
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
      out.write("\n\n\t<div class=\"");
      out.print( portletTitleBasedNavigation ? "contextual-sidebar-content" : "sidenav-content" );
      out.write("\">\n\t\t<div class=\"alert alert-danger hide\" id=\"");
      if (_jspx_meth_portlet_namespace_2(_jspx_page_context))
        return;
      out.write("openMSOfficeError\"></div>\n\n\t\t");
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_3 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_3.setPageContext(_jspx_page_context);
      _jspx_th_c_if_3.setParent(null);
      _jspx_th_c_if_3.setTest( !portletTitleBasedNavigation );
      int _jspx_eval_c_if_3 = _jspx_th_c_if_3.doStartTag();
      if (_jspx_eval_c_if_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\t\t\t<div class=\"file-entry-actions\">\n\t\t\t\t");
          if (_jspx_meth_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_3, _jspx_page_context))
            return;
          out.write("\n\n\t\t\t\t");
          //  c:if
          org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_4 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
          _jspx_th_c_if_4.setPageContext(_jspx_page_context);
          _jspx_th_c_if_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_3);
          _jspx_th_c_if_4.setTest( dlPortletInstanceSettingsHelper.isShowActions() );
          int _jspx_eval_c_if_4 = _jspx_th_c_if_4.doStartTag();
          if (_jspx_eval_c_if_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\n\t\t\t\t\t");

					for (ToolbarItem toolbarItem : dlViewFileVersionDisplayContext.getToolbarItems()) {
					
              out.write("\n\n\t\t\t\t\t");
              //  liferay-ui:toolbar-item
              com.liferay.taglib.ui.ToolbarItemTag _jspx_th_liferay$1ui_toolbar$1item_0 = (com.liferay.taglib.ui.ToolbarItemTag) _jspx_tagPool_liferay$1ui_toolbar$1item_toolbarItem_nobody.get(com.liferay.taglib.ui.ToolbarItemTag.class);
              _jspx_th_liferay$1ui_toolbar$1item_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1ui_toolbar$1item_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_4);
              _jspx_th_liferay$1ui_toolbar$1item_0.setToolbarItem( toolbarItem );
              int _jspx_eval_liferay$1ui_toolbar$1item_0 = _jspx_th_liferay$1ui_toolbar$1item_0.doStartTag();
              if (_jspx_th_liferay$1ui_toolbar$1item_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1ui_toolbar$1item_toolbarItem_nobody.reuse(_jspx_th_liferay$1ui_toolbar$1item_0);
                return;
              }
              _jspx_tagPool_liferay$1ui_toolbar$1item_toolbarItem_nobody.reuse(_jspx_th_liferay$1ui_toolbar$1item_0);
              out.write("\n\n\t\t\t\t\t");

					}
					
              out.write("\n\n\t\t\t\t");
              int evalDoAfterBody = _jspx_th_c_if_4.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_c_if_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_4);
            return;
          }
          _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_4);
          out.write("\n\t\t\t</div>\n\t\t");
          int evalDoAfterBody = _jspx_th_c_if_3.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_c_if_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_3);
        return;
      }
      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_3);
      out.write("\n\n\t\t");
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_5 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_5.setPageContext(_jspx_page_context);
      _jspx_th_c_if_5.setParent(null);
      _jspx_th_c_if_5.setTest( (lock != null) && DLFileEntryPermission.contains(permissionChecker, fileEntry, ActionKeys.UPDATE) );
      int _jspx_eval_c_if_5 = _jspx_th_c_if_5.doStartTag();
      if (_jspx_eval_c_if_5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\t\t\t");
          //  c:choose
          org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_2 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
          _jspx_th_c_choose_2.setPageContext(_jspx_page_context);
          _jspx_th_c_choose_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_5);
          int _jspx_eval_c_choose_2 = _jspx_th_c_choose_2.doStartTag();
          if (_jspx_eval_c_choose_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t\t\t");
              //  c:when
              org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_2 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
              _jspx_th_c_when_2.setPageContext(_jspx_page_context);
              _jspx_th_c_when_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_2);
              _jspx_th_c_when_2.setTest( fileEntry.hasLock() );
              int _jspx_eval_c_when_2 = _jspx_th_c_when_2.doStartTag();
              if (_jspx_eval_c_when_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t\t<div class=\"alert alert-success\">\n\t\t\t\t\t\t");
                  //  c:choose
                  org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_3 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                  _jspx_th_c_choose_3.setPageContext(_jspx_page_context);
                  _jspx_th_c_choose_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_2);
                  int _jspx_eval_c_choose_3 = _jspx_th_c_choose_3.doStartTag();
                  if (_jspx_eval_c_choose_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    do {
                      out.write("\n\t\t\t\t\t\t\t");
                      //  c:when
                      org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_3 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                      _jspx_th_c_when_3.setPageContext(_jspx_page_context);
                      _jspx_th_c_when_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_3);
                      _jspx_th_c_when_3.setTest( lock.isNeverExpires() );
                      int _jspx_eval_c_when_3 = _jspx_th_c_when_3.doStartTag();
                      if (_jspx_eval_c_when_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        do {
                          out.write("\n\t\t\t\t\t\t\t\t");
                          if (_jspx_meth_liferay$1ui_message_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_3, _jspx_page_context))
                            return;
                          out.write("\n\t\t\t\t\t\t\t");
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
                      out.write("\n\t\t\t\t\t\t\t");
                      //  c:otherwise
                      org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_2 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                      _jspx_th_c_otherwise_2.setPageContext(_jspx_page_context);
                      _jspx_th_c_otherwise_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_3);
                      int _jspx_eval_c_otherwise_2 = _jspx_th_c_otherwise_2.doStartTag();
                      if (_jspx_eval_c_otherwise_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        do {
                          out.write("\n\t\t\t\t\t\t\t\t");
                          //  liferay-ui:message
                          com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_2 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
                          _jspx_th_liferay$1ui_message_2.setPageContext(_jspx_page_context);
                          _jspx_th_liferay$1ui_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_2);
                          _jspx_th_liferay$1ui_message_2.setArguments( StringUtil.toLowerCase(LanguageUtil.getTimeDescription(request, DLFileEntryConstants.LOCK_EXPIRATION_TIME)) );
                          _jspx_th_liferay$1ui_message_2.setKey("you-now-have-a-lock-on-this-document");
                          _jspx_th_liferay$1ui_message_2.setTranslateArguments( false );
                          int _jspx_eval_liferay$1ui_message_2 = _jspx_th_liferay$1ui_message_2.doStartTag();
                          if (_jspx_th_liferay$1ui_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                            _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_2);
                            return;
                          }
                          _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_2);
                          out.write("\n\t\t\t\t\t\t\t");
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
                      out.write("\n\t\t\t\t\t\t");
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
                  out.write("\n\t\t\t\t\t</div>\n\t\t\t\t");
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
              out.write("\n\t\t\t\t");
              //  c:otherwise
              org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_3 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
              _jspx_th_c_otherwise_3.setPageContext(_jspx_page_context);
              _jspx_th_c_otherwise_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_2);
              int _jspx_eval_c_otherwise_3 = _jspx_th_c_otherwise_3.doStartTag();
              if (_jspx_eval_c_otherwise_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n\t\t\t\t\t<div class=\"alert alert-danger\">\n\t\t\t\t\t\t");
                  //  liferay-ui:message
                  com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_3 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
                  _jspx_th_liferay$1ui_message_3.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1ui_message_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_3);
                  _jspx_th_liferay$1ui_message_3.setArguments( new Object[] {HtmlUtil.escape(PortalUtil.getUserName(lock.getUserId(), String.valueOf(lock.getUserId()))), dateFormatDateTime.format(lock.getCreateDate())} );
                  _jspx_th_liferay$1ui_message_3.setKey("you-cannot-modify-this-document-because-it-was-locked-by-x-on-x");
                  _jspx_th_liferay$1ui_message_3.setTranslateArguments( false );
                  int _jspx_eval_liferay$1ui_message_3 = _jspx_th_liferay$1ui_message_3.doStartTag();
                  if (_jspx_th_liferay$1ui_message_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_3);
                    return;
                  }
                  _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_3);
                  out.write("\n\t\t\t\t\t</div>\n\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_c_otherwise_3.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
              }
              if (_jspx_th_c_otherwise_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_3);
                return;
              }
              _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_3);
              out.write("\n\t\t\t");
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
          out.write("\n\t\t");
          int evalDoAfterBody = _jspx_th_c_if_5.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_c_if_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_5);
        return;
      }
      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_5);
      out.write("\n\n\t\t<div class=\"body-row\">\n\t\t\t");
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_6 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_6.setPageContext(_jspx_page_context);
      _jspx_th_c_if_6.setParent(null);
      _jspx_th_c_if_6.setTest( PropsValues.DL_FILE_ENTRY_PREVIEW_ENABLED );
      int _jspx_eval_c_if_6 = _jspx_th_c_if_6.doStartTag();
      if (_jspx_eval_c_if_6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\n\t\t\t\t");

				PortalIncludeUtil.include(
					pageContext,
					new PortalIncludeUtil.HTMLRenderer() {

						@Override
						public void renderHTML(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
							dlViewFileVersionDisplayContext.renderPreview(request, response);
						}

					});
				
          out.write("\n\n\t\t\t");
          int evalDoAfterBody = _jspx_th_c_if_6.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_c_if_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_6);
        return;
      }
      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_6);
      out.write("\n\n\t\t\t");
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_7 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_7.setPageContext(_jspx_page_context);
      _jspx_th_c_if_7.setParent(null);
      _jspx_th_c_if_7.setTest( showComments && fileEntry.isRepositoryCapabilityProvided(CommentCapability.class) );
      int _jspx_eval_c_if_7 = _jspx_th_c_if_7.doStartTag();
      if (_jspx_eval_c_if_7 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\t\t\t\t");
          //  liferay-comment:discussion
          com.liferay.comment.taglib.servlet.taglib.DiscussionTag _jspx_th_liferay$1comment_discussion_0 = (com.liferay.comment.taglib.servlet.taglib.DiscussionTag) _jspx_tagPool_liferay$1comment_discussion_userId_redirect_ratingsEnabled_formName_classPK_className_nobody.get(com.liferay.comment.taglib.servlet.taglib.DiscussionTag.class);
          _jspx_th_liferay$1comment_discussion_0.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1comment_discussion_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_7);
          _jspx_th_liferay$1comment_discussion_0.setClassName( dlViewFileVersionDisplayContext.getDiscussionClassName() );
          _jspx_th_liferay$1comment_discussion_0.setClassPK( dlViewFileVersionDisplayContext.getDiscussionClassPK() );
          _jspx_th_liferay$1comment_discussion_0.setFormName("fm2");
          _jspx_th_liferay$1comment_discussion_0.setRatingsEnabled( dlPortletInstanceSettings.isEnableCommentRatings() );
          _jspx_th_liferay$1comment_discussion_0.setRedirect( currentURL );
          _jspx_th_liferay$1comment_discussion_0.setUserId( PortalUtil.getValidUserId(fileEntry.getCompanyId(), fileEntry.getUserId()) );
          int _jspx_eval_liferay$1comment_discussion_0 = _jspx_th_liferay$1comment_discussion_0.doStartTag();
          if (_jspx_th_liferay$1comment_discussion_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1comment_discussion_userId_redirect_ratingsEnabled_formName_classPK_className_nobody.reuse(_jspx_th_liferay$1comment_discussion_0);
            return;
          }
          _jspx_tagPool_liferay$1comment_discussion_userId_redirect_ratingsEnabled_formName_classPK_className_nobody.reuse(_jspx_th_liferay$1comment_discussion_0);
          out.write("\n\t\t\t");
          int evalDoAfterBody = _jspx_th_c_if_7.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_c_if_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_7);
        return;
      }
      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_7);
      out.write("\n\t\t</div>\n\t</div>\n</div>\n\n");
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_8 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_8.setPageContext(_jspx_page_context);
      _jspx_th_c_if_8.setParent(null);
      _jspx_th_c_if_8.setTest( dlPortletInstanceSettingsHelper.isShowActions() && dlAdminDisplayContext.isVersioningStrategyOverridable() );
      int _jspx_eval_c_if_8 = _jspx_th_c_if_8.doStartTag();
      if (_jspx_eval_c_if_8 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\n\n\t");

	request.setAttribute("edit_file_entry.jsp-checkedOut", fileEntry.isCheckedOut());
	
          out.write("\n\n\t");
          //  liferay-util:include
          com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_3 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
          _jspx_th_liferay$1util_include_3.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1util_include_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_8);
          _jspx_th_liferay$1util_include_3.setPage("/document_library/version_details.jsp");
          _jspx_th_liferay$1util_include_3.setServletContext( application );
          int _jspx_eval_liferay$1util_include_3 = _jspx_th_liferay$1util_include_3.doStartTag();
          if (_jspx_th_liferay$1util_include_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_3);
            return;
          }
          _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_3);
          out.write('\n');
          int evalDoAfterBody = _jspx_th_c_if_8.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_c_if_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_8);
        return;
      }
      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_8);
      out.write('\n');
      out.write('\n');
      //  portlet:renderURL
      com.liferay.taglib.portlet.RenderURLTag _jspx_th_portlet_renderURL_0 = (com.liferay.taglib.portlet.RenderURLTag) _jspx_tagPool_portlet_renderURL_windowState_var.get(com.liferay.taglib.portlet.RenderURLTag.class);
      _jspx_th_portlet_renderURL_0.setPageContext(_jspx_page_context);
      _jspx_th_portlet_renderURL_0.setParent(null);
      _jspx_th_portlet_renderURL_0.setVar("selectFolderURL");
      _jspx_th_portlet_renderURL_0.setWindowState( LiferayWindowState.POP_UP.toString() );
      int _jspx_eval_portlet_renderURL_0 = _jspx_th_portlet_renderURL_0.doStartTag();
      if (_jspx_eval_portlet_renderURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_meth_portlet_param_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_portlet_renderURL_0, _jspx_page_context))
          return;
        //  portlet:param
        com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_1 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
        _jspx_th_portlet_param_1.setPageContext(_jspx_page_context);
        _jspx_th_portlet_param_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_0);
        _jspx_th_portlet_param_1.setName("folderId");
        _jspx_th_portlet_param_1.setValue( String.valueOf(folderId) );
        int _jspx_eval_portlet_param_1 = _jspx_th_portlet_param_1.doStartTag();
        if (_jspx_th_portlet_param_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_1);
          return;
        }
        _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_1);
      }
      if (_jspx_th_portlet_renderURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_portlet_renderURL_windowState_var.reuse(_jspx_th_portlet_renderURL_0);
        return;
      }
      _jspx_tagPool_portlet_renderURL_windowState_var.reuse(_jspx_th_portlet_renderURL_0);
      java.lang.String selectFolderURL = null;
      selectFolderURL = (java.lang.String) _jspx_page_context.findAttribute("selectFolderURL");
      out.write('\n');
      out.write('\n');
      //  portlet:actionURL
      com.liferay.taglib.portlet.ActionURLTag _jspx_th_portlet_actionURL_1 = (com.liferay.taglib.portlet.ActionURLTag) _jspx_tagPool_portlet_actionURL_var_name_nobody.get(com.liferay.taglib.portlet.ActionURLTag.class);
      _jspx_th_portlet_actionURL_1.setPageContext(_jspx_page_context);
      _jspx_th_portlet_actionURL_1.setParent(null);
      _jspx_th_portlet_actionURL_1.setName("/document_library/edit_entry");
      _jspx_th_portlet_actionURL_1.setVar("editEntryURL");
      int _jspx_eval_portlet_actionURL_1 = _jspx_th_portlet_actionURL_1.doStartTag();
      if (_jspx_th_portlet_actionURL_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_portlet_actionURL_var_name_nobody.reuse(_jspx_th_portlet_actionURL_1);
        return;
      }
      _jspx_tagPool_portlet_actionURL_var_name_nobody.reuse(_jspx_th_portlet_actionURL_1);
      java.lang.String editEntryURL = null;
      editEntryURL = (java.lang.String) _jspx_page_context.findAttribute("editEntryURL");
      out.write('\n');
      out.write('\n');
      //  aui:script
      com.liferay.taglib.aui.ScriptTag _jspx_th_aui_script_0 = (com.liferay.taglib.aui.ScriptTag) _jspx_tagPool_aui_script.get(com.liferay.taglib.aui.ScriptTag.class);
      _jspx_th_aui_script_0.setPageContext(_jspx_page_context);
      _jspx_th_aui_script_0.setParent(null);
      int _jspx_eval_aui_script_0 = _jspx_th_aui_script_0.doStartTag();
      if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_aui_script_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_aui_script_0.doInitBody();
        }
        do {
          out.write("\n\tfunction ");
          if (_jspx_meth_portlet_namespace_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("move(\n\t\tselectedItems,\n\t\tparameterName,\n\t\tparameterValue\n\t) {\n\t\tvar namespace = '");
          if (_jspx_meth_portlet_namespace_4((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_0, _jspx_page_context))
            return;
          out.write("';\n\n\t\tLiferay.Util.selectEntity(\n\t\t\t{\n\t\t\t\tdialog: {\n\t\t\t\t\tconstrain: true,\n\t\t\t\t\tdestroyOnHide: true,\n\t\t\t\t\tmodal: true,\n\t\t\t\t\twidth: 680\n\t\t\t\t},\n\t\t\t\tid: namespace + 'selectFolder',\n\t\t\t\ttitle:\n\t\t\t\t\t'");
          //  liferay-ui:message
          com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_4 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
          _jspx_th_liferay$1ui_message_4.setPageContext(_jspx_page_context);
          _jspx_th_liferay$1ui_message_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
          _jspx_th_liferay$1ui_message_4.setArguments( 1 );
          _jspx_th_liferay$1ui_message_4.setKey("select-destination-folder-for-x-items");
          _jspx_th_liferay$1ui_message_4.setTranslateArguments( false );
          int _jspx_eval_liferay$1ui_message_4 = _jspx_th_liferay$1ui_message_4.doStartTag();
          if (_jspx_th_liferay$1ui_message_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_4);
            return;
          }
          _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_4);
          out.write("',\n\t\t\t\turi: '");
          out.print( selectFolderURL.toString() );
          out.write("'\n\t\t\t},\n\t\t\tfunction(event) {\n\t\t\t\tvar form = document.getElementById(namespace + 'fm');\n\n\t\t\t\tif (parameterName && parameterValue) {\n\t\t\t\t\tform.elements[namespace + parameterName].value = parameterValue;\n\t\t\t\t}\n\n\t\t\t\tvar actionUrl = '");
          out.print( editEntryURL.toString() );
          out.write("';\n\n\t\t\t\tform.setAttribute('action', actionUrl);\n\t\t\t\tform.setAttribute('enctype', 'multipart/form-data');\n\n\t\t\t\tform.elements[namespace + 'cmd'].value = 'move';\n\t\t\t\tform.elements[namespace + 'newFolderId'].value = event.folderid;\n\n\t\t\t\tsubmitForm(form, actionUrl, false);\n\t\t\t}\n\t\t);\n\t}\n");
          int evalDoAfterBody = _jspx_th_aui_script_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_aui_script_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_aui_script_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_aui_script.reuse(_jspx_th_aui_script_0);
        return;
      }
      _jspx_tagPool_aui_script.reuse(_jspx_th_aui_script_0);
      out.write('\n');
      out.write('\n');

boolean addPortletBreadcrumbEntries = ParamUtil.getBoolean(request, "addPortletBreadcrumbEntries", true);

if (addPortletBreadcrumbEntries) {
	DLBreadcrumbUtil.addPortletBreadcrumbEntries(fileEntry, request, renderResponse);
}

      out.write('\n');
      out.write('\n');
      //  c:if
      org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_9 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
      _jspx_th_c_if_9.setPageContext(_jspx_page_context);
      _jspx_th_c_if_9.setParent(null);
      _jspx_th_c_if_9.setTest( portletTitleBasedNavigation );
      int _jspx_eval_c_if_9 = _jspx_th_c_if_9.doStartTag();
      if (_jspx_eval_c_if_9 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write('\n');
          out.write('	');
          if (_jspx_meth_aui_script_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_9, _jspx_page_context))
            return;
          out.write('\n');
          int evalDoAfterBody = _jspx_th_c_if_9.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_c_if_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_9);
        return;
      }
      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_9);
      out.write('\n');
      out.write('\n');
      if (_jspx_meth_liferay$1util_dynamic$1include_1(_jspx_page_context))
        return;
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

  private boolean _jspx_meth_liferay$1util_dynamic$1include_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-util:dynamic-include
    com.liferay.taglib.util.DynamicIncludeTag _jspx_th_liferay$1util_dynamic$1include_0 = (com.liferay.taglib.util.DynamicIncludeTag) _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.get(com.liferay.taglib.util.DynamicIncludeTag.class);
    _jspx_th_liferay$1util_dynamic$1include_0.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1util_dynamic$1include_0.setParent(null);
    _jspx_th_liferay$1util_dynamic$1include_0.setKey("com.liferay.document.library.web#/document_library/view_file_entry.jsp#pre");
    int _jspx_eval_liferay$1util_dynamic$1include_0 = _jspx_th_liferay$1util_dynamic$1include_0.doStartTag();
    if (_jspx_th_liferay$1util_dynamic$1include_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.reuse(_jspx_th_liferay$1util_dynamic$1include_0);
      return true;
    }
    _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.reuse(_jspx_th_liferay$1util_dynamic$1include_0);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_0 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_0.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
    _jspx_th_liferay$1ui_message_0.setKey("version");
    int _jspx_eval_liferay$1ui_message_0 = _jspx_th_liferay$1ui_message_0.doStartTag();
    if (_jspx_th_liferay$1ui_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_0);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_0);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_0 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_0.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
    int _jspx_eval_portlet_namespace_0 = _jspx_th_portlet_namespace_0.doStartTag();
    if (_jspx_th_portlet_namespace_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
    return false;
  }

  private boolean _jspx_meth_c_otherwise_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_choose_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:otherwise
    org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_0 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
    _jspx_th_c_otherwise_0.setPageContext(_jspx_page_context);
    _jspx_th_c_otherwise_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
    int _jspx_eval_c_otherwise_0 = _jspx_th_c_otherwise_0.doStartTag();
    if (_jspx_eval_c_otherwise_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\n<div class=\"closed container-fluid-1280 sidenav-container sidenav-right\" id=\"");
        if (_jspx_meth_portlet_namespace_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_otherwise_0, _jspx_page_context))
          return true;
        out.write("infoPanelId\">\n\t");
        int evalDoAfterBody = _jspx_th_c_otherwise_0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_c_otherwise_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_0);
      return true;
    }
    _jspx_tagPool_c_otherwise.reuse(_jspx_th_c_otherwise_0);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_1(javax.servlet.jsp.tagext.JspTag _jspx_th_c_otherwise_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_1 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_1.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_0);
    int _jspx_eval_portlet_namespace_1 = _jspx_th_portlet_namespace_1.doStartTag();
    if (_jspx_th_portlet_namespace_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_1);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_1);
    return false;
  }

  private boolean _jspx_meth_aui_input_3(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_3 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_3.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    _jspx_th_aui_input_3.setName("newFolderId");
    _jspx_th_aui_input_3.setType("hidden");
    int _jspx_eval_aui_input_3 = _jspx_th_aui_input_3.doStartTag();
    if (_jspx_th_aui_input_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_3);
      return true;
    }
    _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_3);
    return false;
  }

  private boolean _jspx_meth_aui_input_4(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_4 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_4.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    _jspx_th_aui_input_4.setName("rowIdsDLFileShortcut");
    _jspx_th_aui_input_4.setType("hidden");
    int _jspx_eval_aui_input_4 = _jspx_th_aui_input_4.doStartTag();
    if (_jspx_th_aui_input_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_4);
      return true;
    }
    _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_4);
    return false;
  }

  private boolean _jspx_meth_aui_input_5(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_5 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_5.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    _jspx_th_aui_input_5.setName("rowIdsFileEntry");
    _jspx_th_aui_input_5.setType("hidden");
    int _jspx_eval_aui_input_5 = _jspx_th_aui_input_5.doStartTag();
    if (_jspx_th_aui_input_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_5);
      return true;
    }
    _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_5);
    return false;
  }

  private boolean _jspx_meth_aui_input_6(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:input
    com.liferay.taglib.aui.InputTag _jspx_th_aui_input_6 = (com.liferay.taglib.aui.InputTag) _jspx_tagPool_aui_input_type_name_nobody.get(com.liferay.taglib.aui.InputTag.class);
    _jspx_th_aui_input_6.setPageContext(_jspx_page_context);
    _jspx_th_aui_input_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_form_0);
    _jspx_th_aui_input_6.setName("rowIdsFolder");
    _jspx_th_aui_input_6.setType("hidden");
    int _jspx_eval_aui_input_6 = _jspx_th_aui_input_6.doStartTag();
    if (_jspx_th_aui_input_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_6);
      return true;
    }
    _jspx_tagPool_aui_input_type_name_nobody.reuse(_jspx_th_aui_input_6);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_2(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_2 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_2.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_2.setParent(null);
    int _jspx_eval_portlet_namespace_2 = _jspx_th_portlet_namespace_2.doStartTag();
    if (_jspx_th_portlet_namespace_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_2);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_2);
    return false;
  }

  private boolean _jspx_meth_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_3, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-frontend:management-bar-sidenav-toggler-button
    com.liferay.frontend.taglib.servlet.taglib.ManagementBarSidenavTogglerButtonTag _jspx_th_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_0 = (com.liferay.frontend.taglib.servlet.taglib.ManagementBarSidenavTogglerButtonTag) _jspx_tagPool_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_label_nobody.get(com.liferay.frontend.taglib.servlet.taglib.ManagementBarSidenavTogglerButtonTag.class);
    _jspx_th_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_0.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_3);
    _jspx_th_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_0.setLabel("info");
    int _jspx_eval_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_0 = _jspx_th_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_0.doStartTag();
    if (_jspx_th_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_label_nobody.reuse(_jspx_th_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_0);
      return true;
    }
    _jspx_tagPool_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_label_nobody.reuse(_jspx_th_liferay$1frontend_management$1bar$1sidenav$1toggler$1button_0);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_1(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_3, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_1 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_1.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_3);
    _jspx_th_liferay$1ui_message_1.setKey("you-now-have-an-indefinite-lock-on-this-document");
    int _jspx_eval_liferay$1ui_message_1 = _jspx_th_liferay$1ui_message_1.doStartTag();
    if (_jspx_th_liferay$1ui_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_1);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_1);
    return false;
  }

  private boolean _jspx_meth_portlet_param_0(javax.servlet.jsp.tagext.JspTag _jspx_th_portlet_renderURL_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:param
    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_0 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
    _jspx_th_portlet_param_0.setPageContext(_jspx_page_context);
    _jspx_th_portlet_param_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_0);
    _jspx_th_portlet_param_0.setName("mvcRenderCommandName");
    _jspx_th_portlet_param_0.setValue("/document_library/select_folder");
    int _jspx_eval_portlet_param_0 = _jspx_th_portlet_param_0.doStartTag();
    if (_jspx_th_portlet_param_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_3(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_3 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_3.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_3 = _jspx_th_portlet_namespace_3.doStartTag();
    if (_jspx_th_portlet_namespace_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_3);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_3);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_4(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_4 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_4.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_0);
    int _jspx_eval_portlet_namespace_4 = _jspx_th_portlet_namespace_4.doStartTag();
    if (_jspx_th_portlet_namespace_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_4);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_4);
    return false;
  }

  private boolean _jspx_meth_aui_script_1(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_9, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:script
    com.liferay.taglib.aui.ScriptTag _jspx_th_aui_script_1 = (com.liferay.taglib.aui.ScriptTag) _jspx_tagPool_aui_script.get(com.liferay.taglib.aui.ScriptTag.class);
    _jspx_th_aui_script_1.setPageContext(_jspx_page_context);
    _jspx_th_aui_script_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_9);
    int _jspx_eval_aui_script_1 = _jspx_th_aui_script_1.doStartTag();
    if (_jspx_eval_aui_script_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      if (_jspx_eval_aui_script_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.pushBody();
        _jspx_th_aui_script_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
        _jspx_th_aui_script_1.doInitBody();
      }
      do {
        out.write("\n\t\tvar openContextualSidebarButton = document.getElementById(\n\t\t\t'");
        if (_jspx_meth_portlet_namespace_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
          return true;
        out.write("OpenContextualSidebar'\n\t\t);\n\n\t\tif (openContextualSidebarButton) {\n\t\t\topenContextualSidebarButton.addEventListener('click', function(event) {\n\t\t\t\tevent.currentTarget.classList.toggle('active');\n\n\t\t\t\tdocument\n\t\t\t\t\t.querySelector(\n\t\t\t\t\t\t'#");
        if (_jspx_meth_portlet_namespace_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_aui_script_1, _jspx_page_context))
          return true;
        out.write("FileEntry .contextual-sidebar'\n\t\t\t\t\t)\n\t\t\t\t\t.classList.toggle('contextual-sidebar-visible');\n\t\t\t});\n\t\t}\n\t");
        int evalDoAfterBody = _jspx_th_aui_script_1.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
      if (_jspx_eval_aui_script_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
        out = _jspx_page_context.popBody();
    }
    if (_jspx_th_aui_script_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_aui_script.reuse(_jspx_th_aui_script_1);
      return true;
    }
    _jspx_tagPool_aui_script.reuse(_jspx_th_aui_script_1);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_5(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_5 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_5.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_5 = _jspx_th_portlet_namespace_5.doStartTag();
    if (_jspx_th_portlet_namespace_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_5);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_5);
    return false;
  }

  private boolean _jspx_meth_portlet_namespace_6(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_script_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_6 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_6.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_script_1);
    int _jspx_eval_portlet_namespace_6 = _jspx_th_portlet_namespace_6.doStartTag();
    if (_jspx_th_portlet_namespace_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_6);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_6);
    return false;
  }

  private boolean _jspx_meth_liferay$1util_dynamic$1include_1(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-util:dynamic-include
    com.liferay.taglib.util.DynamicIncludeTag _jspx_th_liferay$1util_dynamic$1include_1 = (com.liferay.taglib.util.DynamicIncludeTag) _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.get(com.liferay.taglib.util.DynamicIncludeTag.class);
    _jspx_th_liferay$1util_dynamic$1include_1.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1util_dynamic$1include_1.setParent(null);
    _jspx_th_liferay$1util_dynamic$1include_1.setKey("com.liferay.document.library.web#/document_library/view_file_entry.jsp#post");
    int _jspx_eval_liferay$1util_dynamic$1include_1 = _jspx_th_liferay$1util_dynamic$1include_1.doStartTag();
    if (_jspx_th_liferay$1util_dynamic$1include_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.reuse(_jspx_th_liferay$1util_dynamic$1include_1);
      return true;
    }
    _jspx_tagPool_liferay$1util_dynamic$1include_key_nobody.reuse(_jspx_th_liferay$1util_dynamic$1include_1);
    return false;
  }
}
