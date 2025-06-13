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

public final class info_005fpanel_jsp extends org.apache.jasper.runtime.HttpJspBase
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

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_section;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_key_arguments_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_key_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_param_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_if_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_renderURL_var;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_otherwise;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1theme_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_choose;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_include_servletContext_page_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_when_test;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_section = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_key_arguments_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_key_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_param_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_if_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_renderURL_var = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_otherwise = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1theme_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_choose = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_when_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.release();
    _jspx_tagPool_liferay$1ui_section.release();
    _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.release();
    _jspx_tagPool_liferay$1ui_message_key_nobody.release();
    _jspx_tagPool_portlet_param_value_name_nobody.release();
    _jspx_tagPool_c_if_test.release();
    _jspx_tagPool_portlet_renderURL_var.release();
    _jspx_tagPool_c_otherwise.release();
    _jspx_tagPool_portlet_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1theme_defineObjects_nobody.release();
    _jspx_tagPool_c_choose.release();
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.release();
    _jspx_tagPool_c_when_test.release();
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

long repositoryId = GetterUtil.getLong((String)request.getAttribute("view.jsp-repositoryId"), ParamUtil.getLong(request, "repositoryId"));

request.setAttribute("view.jsp-repositoryId", String.valueOf(repositoryId));

List<Folder> folders = (List<Folder>)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FOLDERS);
List<FileEntry> fileEntries = (List<FileEntry>)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_ENTRIES);
List<FileShortcut> fileShortcuts = (List<FileShortcut>)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_SHORTCUTS);

Map<String, Object> infoPanelToggleData = new HashMap<>();

infoPanelToggleData.put("toggle", liferayPortletResponse.getNamespace() + "infoPanelId");

if (ListUtil.isEmpty(folders) && ListUtil.isEmpty(fileEntries) && ListUtil.isEmpty(fileShortcuts)) {
	long folderId = GetterUtil.getLong((String)request.getAttribute("view.jsp-folderId"), ParamUtil.getLong(request, "folderId"));

	folders = new ArrayList<>();

	Folder folder = (Folder)request.getAttribute("view.jsp-folder");

	if (folder != null) {
		folders.add(folder);
	}
	else if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
		folders.add(DLAppLocalServiceUtil.getFolder(folderId));
	}
	else {
		folders.add(null);
	}
}

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
          _jspx_th_c_when_0.setTest( ListUtil.isEmpty(fileEntries) && ListUtil.isEmpty(fileShortcuts) && ListUtil.isNotEmpty(folders) && (folders.size() == 1) );
          int _jspx_eval_c_when_0 = _jspx_th_c_when_0.doStartTag();
          if (_jspx_eval_c_when_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\n\t\t");

		Folder folder = folders.get(0);

		request.setAttribute("info_panel.jsp-folder", folder);
		
              out.write("\n\n\t\t<div class=\"sidebar-header\">\n\t\t\t<ul class=\"sidebar-actions\">\n\t\t\t\t<li>\n\t\t\t\t\t");
              //  liferay-util:include
              com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_0 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
              _jspx_th_liferay$1util_include_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1util_include_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
              _jspx_th_liferay$1util_include_0.setPage("/document_library/subscribe.jsp");
              _jspx_th_liferay$1util_include_0.setServletContext( application );
              int _jspx_eval_liferay$1util_include_0 = _jspx_th_liferay$1util_include_0.doStartTag();
              if (_jspx_th_liferay$1util_include_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
                return;
              }
              _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
              out.write("\n\t\t\t\t</li>\n\t\t\t\t<li>\n\t\t\t\t\t");
              //  liferay-util:include
              com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_1 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
              _jspx_th_liferay$1util_include_1.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1util_include_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
              _jspx_th_liferay$1util_include_1.setPage("/document_library/folder_action.jsp");
              _jspx_th_liferay$1util_include_1.setServletContext( application );
              int _jspx_eval_liferay$1util_include_1 = _jspx_th_liferay$1util_include_1.doStartTag();
              if (_jspx_th_liferay$1util_include_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_1);
                return;
              }
              _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_1);
              out.write("\n\t\t\t\t</li>\n\t\t\t</ul>\n\n\t\t\t<h1 class=\"sidebar-title\">\n\t\t\t\t");
              out.print( (folder != null) ? HtmlUtil.escape(folder.getName()) : LanguageUtil.get(request, "home") );
              out.write("\n\t\t\t</h1>\n\n\t\t\t<h2 class=\"sidebar-subtitle\">\n\t\t\t\t");
              if (_jspx_meth_liferay$1ui_message_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_0, _jspx_page_context))
                return;
              out.write("\n\t\t\t</h2>\n\t\t</div>\n\n\t\t<div class=\"sidebar-body\">\n\t\t\t");
              //  liferay-ui:tabs
              com.liferay.taglib.ui.TabsTag _jspx_th_liferay$1ui_tabs_0 = (com.liferay.taglib.ui.TabsTag) _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.get(com.liferay.taglib.ui.TabsTag.class);
              _jspx_th_liferay$1ui_tabs_0.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1ui_tabs_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
              _jspx_th_liferay$1ui_tabs_0.setCssClass("navbar-no-collapse");
              _jspx_th_liferay$1ui_tabs_0.setNames("details");
              _jspx_th_liferay$1ui_tabs_0.setRefresh( false );
              int _jspx_eval_liferay$1ui_tabs_0 = _jspx_th_liferay$1ui_tabs_0.doStartTag();
              if (_jspx_eval_liferay$1ui_tabs_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t");
                //  liferay-ui:section
                com.liferay.taglib.ui.SectionTag _jspx_th_liferay$1ui_section_0 = (com.liferay.taglib.ui.SectionTag) _jspx_tagPool_liferay$1ui_section.get(com.liferay.taglib.ui.SectionTag.class);
                _jspx_th_liferay$1ui_section_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_section_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_tabs_0);
                int _jspx_eval_liferay$1ui_section_0 = _jspx_th_liferay$1ui_section_0.doStartTag();
                if (_jspx_eval_liferay$1ui_section_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  java.lang.String sectionParam = null;
                  java.lang.String sectionName = null;
                  java.lang.Boolean sectionSelected = null;
                  java.lang.String sectionScroll = null;
                  java.lang.String sectionRedirectParams = null;
                  sectionParam = (java.lang.String) _jspx_page_context.findAttribute("sectionParam");
                  sectionName = (java.lang.String) _jspx_page_context.findAttribute("sectionName");
                  sectionSelected = (java.lang.Boolean) _jspx_page_context.findAttribute("sectionSelected");
                  sectionScroll = (java.lang.String) _jspx_page_context.findAttribute("sectionScroll");
                  sectionRedirectParams = (java.lang.String) _jspx_page_context.findAttribute("sectionRedirectParams");
                  out.write("\n\t\t\t\t\t<dl class=\"sidebar-block\">\n\t\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t\t");
                  if (_jspx_meth_liferay$1ui_message_1((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_section_0, _jspx_page_context))
                    return;
                  out.write("\n\t\t\t\t\t\t</dt>\n\n\t\t\t\t\t\t");

						long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

						if (folder != null) {
							folderId = folder.getFolderId();
						}
						
                  out.write("\n\n\t\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t\t");
                  out.print( DLAppServiceUtil.getFoldersAndFileEntriesAndFileShortcutsCount(repositoryId, folderId, WorkflowConstants.STATUS_APPROVED, true) );
                  out.write("\n\t\t\t\t\t\t</dd>\n\n\t\t\t\t\t\t");
                  //  c:if
                  org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_0 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                  _jspx_th_c_if_0.setPageContext(_jspx_page_context);
                  _jspx_th_c_if_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
                  _jspx_th_c_if_0.setTest( folder != null );
                  int _jspx_eval_c_if_0 = _jspx_th_c_if_0.doStartTag();
                  if (_jspx_eval_c_if_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    do {
                      out.write("\n\t\t\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t\t\t");
                      if (_jspx_meth_liferay$1ui_message_2((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_0, _jspx_page_context))
                        return;
                      out.write("\n\t\t\t\t\t\t\t</dt>\n\t\t\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t\t\t");
                      out.print( HtmlUtil.escape(folder.getUserName()) );
                      out.write("\n\t\t\t\t\t\t\t</dd>\n\n\t\t\t\t\t\t\t");

							request.setAttribute("info_panel_location.jsp-parentFolder", folder.getParentFolder());
							
                      out.write("\n\n\t\t\t\t\t\t\t");
                      //  liferay-util:include
                      com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_2 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
                      _jspx_th_liferay$1util_include_2.setPageContext(_jspx_page_context);
                      _jspx_th_liferay$1util_include_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
                      _jspx_th_liferay$1util_include_2.setPage("/document_library/info_panel_location.jsp");
                      _jspx_th_liferay$1util_include_2.setServletContext( application );
                      int _jspx_eval_liferay$1util_include_2 = _jspx_th_liferay$1util_include_2.doStartTag();
                      if (_jspx_th_liferay$1util_include_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_2);
                        return;
                      }
                      _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_2);
                      out.write("\n\t\t\t\t\t\t");
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
                  out.write("\n\t\t\t\t\t</dl>\n\t\t\t\t");
                }
                if (_jspx_th_liferay$1ui_section_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_section.reuse(_jspx_th_liferay$1ui_section_0);
                  return;
                }
                _jspx_tagPool_liferay$1ui_section.reuse(_jspx_th_liferay$1ui_section_0);
                out.write("\n\t\t\t");
              }
              if (_jspx_th_liferay$1ui_tabs_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.reuse(_jspx_th_liferay$1ui_tabs_0);
                return;
              }
              _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.reuse(_jspx_th_liferay$1ui_tabs_0);
              out.write("\n\t\t</div>\n\t");
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
          //  c:when
          org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_1 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
          _jspx_th_c_when_1.setPageContext(_jspx_page_context);
          _jspx_th_c_when_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
          _jspx_th_c_when_1.setTest( ListUtil.isEmpty(folders) && ListUtil.isEmpty(fileShortcuts) && ListUtil.isNotEmpty(fileEntries) && (fileEntries.size() == 1) );
          int _jspx_eval_c_when_1 = _jspx_th_c_when_1.doStartTag();
          if (_jspx_eval_c_when_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\n\t\t");

		FileEntry fileEntry = fileEntries.get(0);

		FileVersion fileVersion = null;

		if ((user.getUserId() == fileEntry.getUserId()) || permissionChecker.isContentReviewer(user.getCompanyId(), scopeGroupId) || DLFileEntryPermission.contains(permissionChecker, fileEntry, ActionKeys.UPDATE)) {
			fileVersion = fileEntry.getLatestFileVersion();
		}
		else {
			fileVersion = fileEntry.getFileVersion();
		}

		request.setAttribute("info_panel.jsp-fileEntry", fileEntry);
		request.setAttribute("info_panel.jsp-fileVersion", fileVersion);
		
              out.write("\n\n\t\t");
              //  liferay-util:include
              com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_3 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
              _jspx_th_liferay$1util_include_3.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1util_include_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
              _jspx_th_liferay$1util_include_3.setPage("/document_library/info_panel_file_entry.jsp");
              _jspx_th_liferay$1util_include_3.setServletContext( application );
              int _jspx_eval_liferay$1util_include_3 = _jspx_th_liferay$1util_include_3.doStartTag();
              if (_jspx_th_liferay$1util_include_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_3);
                return;
              }
              _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_3);
              out.write('\n');
              out.write('	');
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
          out.write('\n');
          out.write('	');
          //  c:when
          org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_2 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
          _jspx_th_c_when_2.setPageContext(_jspx_page_context);
          _jspx_th_c_when_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
          _jspx_th_c_when_2.setTest( ListUtil.isEmpty(folders) && ListUtil.isEmpty(fileEntries) && ListUtil.isNotEmpty(fileShortcuts) && (fileShortcuts.size() == 1) );
          int _jspx_eval_c_when_2 = _jspx_th_c_when_2.doStartTag();
          if (_jspx_eval_c_when_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\n\t\t");

		FileShortcut fileShortcut = fileShortcuts.get(0);

		request.setAttribute("info_panel.jsp-fileShortcut", fileShortcut);
		
              out.write("\n\n\t\t<div class=\"sidebar-header\">\n\t\t\t<ul class=\"sidebar-actions\">\n\t\t\t\t<li>\n\t\t\t\t\t");
              //  liferay-util:include
              com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_4 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
              _jspx_th_liferay$1util_include_4.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1util_include_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_2);
              _jspx_th_liferay$1util_include_4.setPage("/document_library/file_entry_action.jsp");
              _jspx_th_liferay$1util_include_4.setServletContext( application );
              int _jspx_eval_liferay$1util_include_4 = _jspx_th_liferay$1util_include_4.doStartTag();
              if (_jspx_th_liferay$1util_include_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_4);
                return;
              }
              _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_4);
              out.write("\n\t\t\t\t</li>\n\t\t\t</ul>\n\n\t\t\t<h1 class=\"sidebar-title\">\n\t\t\t\t");
              out.print( HtmlUtil.escape(fileShortcut.getToTitle()) );
              out.write("\n\t\t\t</h1>\n\n\t\t\t<h2 class=\"sidebar-subtitle\">\n\t\t\t\t");
              if (_jspx_meth_liferay$1ui_message_3((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_2, _jspx_page_context))
                return;
              out.write("\n\t\t\t</h2>\n\t\t</div>\n\n\t\t<div class=\"sidebar-body\">\n\t\t\t");
              //  liferay-ui:tabs
              com.liferay.taglib.ui.TabsTag _jspx_th_liferay$1ui_tabs_1 = (com.liferay.taglib.ui.TabsTag) _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.get(com.liferay.taglib.ui.TabsTag.class);
              _jspx_th_liferay$1ui_tabs_1.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1ui_tabs_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_2);
              _jspx_th_liferay$1ui_tabs_1.setCssClass("navbar-no-collapse");
              _jspx_th_liferay$1ui_tabs_1.setNames("details");
              _jspx_th_liferay$1ui_tabs_1.setRefresh( false );
              int _jspx_eval_liferay$1ui_tabs_1 = _jspx_th_liferay$1ui_tabs_1.doStartTag();
              if (_jspx_eval_liferay$1ui_tabs_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t");
                //  liferay-ui:section
                com.liferay.taglib.ui.SectionTag _jspx_th_liferay$1ui_section_1 = (com.liferay.taglib.ui.SectionTag) _jspx_tagPool_liferay$1ui_section.get(com.liferay.taglib.ui.SectionTag.class);
                _jspx_th_liferay$1ui_section_1.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_section_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_tabs_1);
                int _jspx_eval_liferay$1ui_section_1 = _jspx_th_liferay$1ui_section_1.doStartTag();
                if (_jspx_eval_liferay$1ui_section_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  java.lang.String sectionParam = null;
                  java.lang.String sectionName = null;
                  java.lang.Boolean sectionSelected = null;
                  java.lang.String sectionScroll = null;
                  java.lang.String sectionRedirectParams = null;
                  sectionParam = (java.lang.String) _jspx_page_context.findAttribute("sectionParam");
                  sectionName = (java.lang.String) _jspx_page_context.findAttribute("sectionName");
                  sectionSelected = (java.lang.Boolean) _jspx_page_context.findAttribute("sectionSelected");
                  sectionScroll = (java.lang.String) _jspx_page_context.findAttribute("sectionScroll");
                  sectionRedirectParams = (java.lang.String) _jspx_page_context.findAttribute("sectionRedirectParams");
                  out.write("\n\n\t\t\t\t\t");

					FileEntry fileEntry = DLAppServiceUtil.getFileEntry(fileShortcut.getToFileEntryId());
					
                  out.write("\n\n\t\t\t\t\t<dl class=\"sidebar-block\">\n\t\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t\t");
                  if (_jspx_meth_liferay$1ui_message_4((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_section_1, _jspx_page_context))
                    return;
                  out.write("\n\t\t\t\t\t\t</dt>\n\t\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t\t");
                  out.print( HtmlUtil.replaceNewLine(HtmlUtil.escape(fileEntry.getDescription())) );
                  out.write("\n\t\t\t\t\t\t</dd>\n\n\t\t\t\t\t\t");

						Group fileEntryGroup = GroupLocalServiceUtil.getGroup(fileEntry.getGroupId());

						Group fileEntrySiteGroup = fileEntryGroup;

						while ((fileEntrySiteGroup != null) && !fileEntrySiteGroup.isSite()) {
							fileEntrySiteGroup = fileEntrySiteGroup.getParentGroup();
						}
						
                  out.write("\n\n\t\t\t\t\t\t");
                  //  c:if
                  org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_1 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                  _jspx_th_c_if_1.setPageContext(_jspx_page_context);
                  _jspx_th_c_if_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_1);
                  _jspx_th_c_if_1.setTest( fileEntrySiteGroup != null );
                  int _jspx_eval_c_if_1 = _jspx_th_c_if_1.doStartTag();
                  if (_jspx_eval_c_if_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    do {
                      out.write("\n\t\t\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t\t\t");
                      if (_jspx_meth_liferay$1ui_message_5((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_1, _jspx_page_context))
                        return;
                      out.write("\n\t\t\t\t\t\t\t</dt>\n\t\t\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t\t\t");
                      out.print( HtmlUtil.escape(fileEntrySiteGroup.getName(locale)) );
                      out.write("\n\t\t\t\t\t\t\t</dd>\n\t\t\t\t\t\t");
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
                  out.write("\n\n\t\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t\t");
                  if (_jspx_meth_liferay$1ui_message_6((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_section_1, _jspx_page_context))
                    return;
                  out.write("\n\t\t\t\t\t\t</dt>\n\t\t\t\t\t\t<dd class=\"sidebar-dd\">\n\n\t\t\t\t\t\t\t");

							Folder folder = fileEntry.getFolder();
							
                  out.write("\n\n\t\t\t\t\t\t\t");
                  //  portlet:renderURL
                  com.liferay.taglib.portlet.RenderURLTag _jspx_th_portlet_renderURL_0 = (com.liferay.taglib.portlet.RenderURLTag) _jspx_tagPool_portlet_renderURL_var.get(com.liferay.taglib.portlet.RenderURLTag.class);
                  _jspx_th_portlet_renderURL_0.setPageContext(_jspx_page_context);
                  _jspx_th_portlet_renderURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_1);
                  _jspx_th_portlet_renderURL_0.setVar("targetFolderURL");
                  int _jspx_eval_portlet_renderURL_0 = _jspx_th_portlet_renderURL_0.doStartTag();
                  if (_jspx_eval_portlet_renderURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    out.write("\n\t\t\t\t\t\t\t\t");
                    if (_jspx_meth_portlet_param_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_portlet_renderURL_0, _jspx_page_context))
                      return;
                    out.write("\n\t\t\t\t\t\t\t\t");
                    //  portlet:param
                    com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_1 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
                    _jspx_th_portlet_param_1.setPageContext(_jspx_page_context);
                    _jspx_th_portlet_param_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_renderURL_0);
                    _jspx_th_portlet_param_1.setName("folderId");
                    _jspx_th_portlet_param_1.setValue( String.valueOf(folder.getFolderId()) );
                    int _jspx_eval_portlet_param_1 = _jspx_th_portlet_param_1.doStartTag();
                    if (_jspx_th_portlet_param_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_1);
                      return;
                    }
                    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_1);
                    out.write("\n\t\t\t\t\t\t\t");
                  }
                  if (_jspx_th_portlet_renderURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_portlet_renderURL_var.reuse(_jspx_th_portlet_renderURL_0);
                    return;
                  }
                  _jspx_tagPool_portlet_renderURL_var.reuse(_jspx_th_portlet_renderURL_0);
                  java.lang.String targetFolderURL = null;
                  targetFolderURL = (java.lang.String) _jspx_page_context.findAttribute("targetFolderURL");
                  out.write("\n\n\t\t\t\t\t\t\t<a href=\"");
                  out.print( targetFolderURL );
                  out.write("\">\n\t\t\t\t\t\t\t\t");
                  //  c:choose
                  org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_1 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
                  _jspx_th_c_choose_1.setPageContext(_jspx_page_context);
                  _jspx_th_c_choose_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_1);
                  int _jspx_eval_c_choose_1 = _jspx_th_c_choose_1.doStartTag();
                  if (_jspx_eval_c_choose_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    do {
                      out.write("\n\t\t\t\t\t\t\t\t\t");
                      //  c:when
                      org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_3 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
                      _jspx_th_c_when_3.setPageContext(_jspx_page_context);
                      _jspx_th_c_when_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                      _jspx_th_c_when_3.setTest( folder.getFolderId() == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID );
                      int _jspx_eval_c_when_3 = _jspx_th_c_when_3.doStartTag();
                      if (_jspx_eval_c_when_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        do {
                          out.write("\n\t\t\t\t\t\t\t\t\t\t");
                          if (_jspx_meth_liferay$1ui_message_7((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_3, _jspx_page_context))
                            return;
                          out.write("\n\t\t\t\t\t\t\t\t\t");
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
                      out.write("\n\t\t\t\t\t\t\t\t\t");
                      //  c:otherwise
                      org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_0 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
                      _jspx_th_c_otherwise_0.setPageContext(_jspx_page_context);
                      _jspx_th_c_otherwise_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_1);
                      int _jspx_eval_c_otherwise_0 = _jspx_th_c_otherwise_0.doStartTag();
                      if (_jspx_eval_c_otherwise_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        do {
                          out.write("\n\t\t\t\t\t\t\t\t\t\t");
                          out.print( HtmlUtil.escape(folder.getName()) );
                          out.write("\n\t\t\t\t\t\t\t\t\t");
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
                      out.write("\n\t\t\t\t\t\t\t\t");
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
                  out.write("\n\t\t\t\t\t\t\t</a>\n\t\t\t\t\t\t</dd>\n\t\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t\t");
                  if (_jspx_meth_liferay$1ui_message_8((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_section_1, _jspx_page_context))
                    return;
                  out.write("\n\t\t\t\t\t\t</dt>\n\t\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t\t");
                  out.print( LanguageUtil.formatStorageSize(fileEntry.getSize(), locale) );
                  out.write("\n\t\t\t\t\t\t</dd>\n\n\t\t\t\t\t\t");
                  //  c:if
                  org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_2 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                  _jspx_th_c_if_2.setPageContext(_jspx_page_context);
                  _jspx_th_c_if_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_1);
                  _jspx_th_c_if_2.setTest( fileEntry.getModel() instanceof DLFileEntry );
                  int _jspx_eval_c_if_2 = _jspx_th_c_if_2.doStartTag();
                  if (_jspx_eval_c_if_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    do {
                      out.write("\n\n\t\t\t\t\t\t\t");

							DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

							DLFileEntryType dlFileEntryType = dlFileEntry.getDLFileEntryType();
							
                      out.write("\n\n\t\t\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t\t\t");
                      if (_jspx_meth_liferay$1ui_message_9((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_if_2, _jspx_page_context))
                        return;
                      out.write("\n\t\t\t\t\t\t\t</dt>\n\t\t\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t\t\t");
                      out.print( HtmlUtil.escape(dlFileEntryType.getName(locale)) );
                      out.write("\n\t\t\t\t\t\t\t</dd>\n\t\t\t\t\t\t");
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
                  out.write("\n\n\t\t\t\t\t\t<dt class=\"sidebar-dt\">\n\t\t\t\t\t\t\t");
                  if (_jspx_meth_liferay$1ui_message_10((javax.servlet.jsp.tagext.JspTag) _jspx_th_liferay$1ui_section_1, _jspx_page_context))
                    return;
                  out.write("\n\t\t\t\t\t\t</dt>\n\t\t\t\t\t\t<dd class=\"sidebar-dd\">\n\t\t\t\t\t\t\t");
                  out.print( HtmlUtil.escape(fileEntry.getMimeType()) );
                  out.write("\n\t\t\t\t\t\t</dd>\n\t\t\t\t\t</dl>\n\t\t\t\t");
                }
                if (_jspx_th_liferay$1ui_section_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_section.reuse(_jspx_th_liferay$1ui_section_1);
                  return;
                }
                _jspx_tagPool_liferay$1ui_section.reuse(_jspx_th_liferay$1ui_section_1);
                out.write("\n\t\t\t");
              }
              if (_jspx_th_liferay$1ui_tabs_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.reuse(_jspx_th_liferay$1ui_tabs_1);
                return;
              }
              _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.reuse(_jspx_th_liferay$1ui_tabs_1);
              out.write("\n\t\t</div>\n\t");
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
          out.write('\n');
          out.write('	');
          //  c:otherwise
          org.apache.taglibs.standard.tag.common.core.OtherwiseTag _jspx_th_c_otherwise_1 = (org.apache.taglibs.standard.tag.common.core.OtherwiseTag) _jspx_tagPool_c_otherwise.get(org.apache.taglibs.standard.tag.common.core.OtherwiseTag.class);
          _jspx_th_c_otherwise_1.setPageContext(_jspx_page_context);
          _jspx_th_c_otherwise_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
          int _jspx_eval_c_otherwise_1 = _jspx_th_c_otherwise_1.doStartTag();
          if (_jspx_eval_c_otherwise_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n\t\t<div class=\"sidebar-header\">\n\t\t\t<h1 class=\"sidebar-title\">\n\t\t\t\t");
              //  liferay-ui:message
              com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_11 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
              _jspx_th_liferay$1ui_message_11.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1ui_message_11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_1);
              _jspx_th_liferay$1ui_message_11.setArguments( folders.size() + fileEntries.size() + fileShortcuts.size() );
              _jspx_th_liferay$1ui_message_11.setKey("x-items-are-selected");
              int _jspx_eval_liferay$1ui_message_11 = _jspx_th_liferay$1ui_message_11.doStartTag();
              if (_jspx_th_liferay$1ui_message_11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_11);
                return;
              }
              _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_11);
              out.write("\n\t\t\t</h1>\n\t\t</div>\n\n\t\t<div class=\"sidebar-body\">\n\t\t\t");
              //  liferay-ui:tabs
              com.liferay.taglib.ui.TabsTag _jspx_th_liferay$1ui_tabs_2 = (com.liferay.taglib.ui.TabsTag) _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.get(com.liferay.taglib.ui.TabsTag.class);
              _jspx_th_liferay$1ui_tabs_2.setPageContext(_jspx_page_context);
              _jspx_th_liferay$1ui_tabs_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_otherwise_1);
              _jspx_th_liferay$1ui_tabs_2.setCssClass("navbar-no-collapse");
              _jspx_th_liferay$1ui_tabs_2.setNames("details");
              _jspx_th_liferay$1ui_tabs_2.setRefresh( false );
              int _jspx_eval_liferay$1ui_tabs_2 = _jspx_th_liferay$1ui_tabs_2.doStartTag();
              if (_jspx_eval_liferay$1ui_tabs_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n\t\t\t\t");
                //  liferay-ui:section
                com.liferay.taglib.ui.SectionTag _jspx_th_liferay$1ui_section_2 = (com.liferay.taglib.ui.SectionTag) _jspx_tagPool_liferay$1ui_section.get(com.liferay.taglib.ui.SectionTag.class);
                _jspx_th_liferay$1ui_section_2.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1ui_section_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_tabs_2);
                int _jspx_eval_liferay$1ui_section_2 = _jspx_th_liferay$1ui_section_2.doStartTag();
                if (_jspx_eval_liferay$1ui_section_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  java.lang.String sectionParam = null;
                  java.lang.String sectionName = null;
                  java.lang.Boolean sectionSelected = null;
                  java.lang.String sectionScroll = null;
                  java.lang.String sectionRedirectParams = null;
                  sectionParam = (java.lang.String) _jspx_page_context.findAttribute("sectionParam");
                  sectionName = (java.lang.String) _jspx_page_context.findAttribute("sectionName");
                  sectionSelected = (java.lang.Boolean) _jspx_page_context.findAttribute("sectionSelected");
                  sectionScroll = (java.lang.String) _jspx_page_context.findAttribute("sectionScroll");
                  sectionRedirectParams = (java.lang.String) _jspx_page_context.findAttribute("sectionRedirectParams");
                  out.write("\n\t\t\t\t\t<strong>\n\t\t\t\t\t\t");
                  //  liferay-ui:message
                  com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_12 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
                  _jspx_th_liferay$1ui_message_12.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1ui_message_12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_2);
                  _jspx_th_liferay$1ui_message_12.setArguments( folders.size() + fileEntries.size() + fileShortcuts.size() );
                  _jspx_th_liferay$1ui_message_12.setKey("x-items-are-selected");
                  int _jspx_eval_liferay$1ui_message_12 = _jspx_th_liferay$1ui_message_12.doStartTag();
                  if (_jspx_th_liferay$1ui_message_12.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_12);
                    return;
                  }
                  _jspx_tagPool_liferay$1ui_message_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_12);
                  out.write("\n\t\t\t\t\t</strong>\n\t\t\t\t");
                }
                if (_jspx_th_liferay$1ui_section_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1ui_section.reuse(_jspx_th_liferay$1ui_section_2);
                  return;
                }
                _jspx_tagPool_liferay$1ui_section.reuse(_jspx_th_liferay$1ui_section_2);
                out.write("\n\t\t\t");
              }
              if (_jspx_th_liferay$1ui_tabs_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.reuse(_jspx_th_liferay$1ui_tabs_2);
                return;
              }
              _jspx_tagPool_liferay$1ui_tabs_refresh_names_cssClass.reuse(_jspx_th_liferay$1ui_tabs_2);
              out.write("\n\t\t</div>\n\t");
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

  private boolean _jspx_meth_liferay$1ui_message_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_0 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_0.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
    _jspx_th_liferay$1ui_message_0.setKey("folder");
    int _jspx_eval_liferay$1ui_message_0 = _jspx_th_liferay$1ui_message_0.doStartTag();
    if (_jspx_th_liferay$1ui_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_0);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_0);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_1(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_section_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_1 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_1.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_0);
    _jspx_th_liferay$1ui_message_1.setKey("num-of-items");
    int _jspx_eval_liferay$1ui_message_1 = _jspx_th_liferay$1ui_message_1.doStartTag();
    if (_jspx_th_liferay$1ui_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_1);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_1);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_2(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_2 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_2.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
    _jspx_th_liferay$1ui_message_2.setKey("created");
    int _jspx_eval_liferay$1ui_message_2 = _jspx_th_liferay$1ui_message_2.doStartTag();
    if (_jspx_th_liferay$1ui_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_2);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_2);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_3(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_2, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_3 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_3.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_2);
    _jspx_th_liferay$1ui_message_3.setKey("shortcut");
    int _jspx_eval_liferay$1ui_message_3 = _jspx_th_liferay$1ui_message_3.doStartTag();
    if (_jspx_th_liferay$1ui_message_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_3);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_3);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_4(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_section_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_4 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_4.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_1);
    _jspx_th_liferay$1ui_message_4.setKey("description");
    int _jspx_eval_liferay$1ui_message_4 = _jspx_th_liferay$1ui_message_4.doStartTag();
    if (_jspx_th_liferay$1ui_message_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_4);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_4);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_5(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_5 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_5.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
    _jspx_th_liferay$1ui_message_5.setKey("target-site");
    int _jspx_eval_liferay$1ui_message_5 = _jspx_th_liferay$1ui_message_5.doStartTag();
    if (_jspx_th_liferay$1ui_message_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_5);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_5);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_6(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_section_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_6 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_6.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_1);
    _jspx_th_liferay$1ui_message_6.setKey("target-folder");
    int _jspx_eval_liferay$1ui_message_6 = _jspx_th_liferay$1ui_message_6.doStartTag();
    if (_jspx_th_liferay$1ui_message_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_6);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_6);
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
    _jspx_th_portlet_param_0.setName("mvcRenderCommand");
    _jspx_th_portlet_param_0.setValue("/document_library/view");
    int _jspx_eval_portlet_param_0 = _jspx_th_portlet_param_0.doStartTag();
    if (_jspx_th_portlet_param_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
      return true;
    }
    _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_7(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_3, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_7 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_7.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_3);
    _jspx_th_liferay$1ui_message_7.setKey("home");
    int _jspx_eval_liferay$1ui_message_7 = _jspx_th_liferay$1ui_message_7.doStartTag();
    if (_jspx_th_liferay$1ui_message_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_7);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_7);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_8(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_section_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_8 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_8.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_1);
    _jspx_th_liferay$1ui_message_8.setKey("size");
    int _jspx_eval_liferay$1ui_message_8 = _jspx_th_liferay$1ui_message_8.doStartTag();
    if (_jspx_th_liferay$1ui_message_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_8);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_8);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_9(javax.servlet.jsp.tagext.JspTag _jspx_th_c_if_2, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_9 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_9.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_2);
    _jspx_th_liferay$1ui_message_9.setKey("document-type");
    int _jspx_eval_liferay$1ui_message_9 = _jspx_th_liferay$1ui_message_9.doStartTag();
    if (_jspx_th_liferay$1ui_message_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_9);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_9);
    return false;
  }

  private boolean _jspx_meth_liferay$1ui_message_10(javax.servlet.jsp.tagext.JspTag _jspx_th_liferay$1ui_section_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_10 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay$1ui_message_10.setPageContext(_jspx_page_context);
    _jspx_th_liferay$1ui_message_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ui_section_1);
    _jspx_th_liferay$1ui_message_10.setKey("content-type");
    int _jspx_eval_liferay$1ui_message_10 = _jspx_th_liferay$1ui_message_10.doStartTag();
    if (_jspx_th_liferay$1ui_message_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_10);
      return true;
    }
    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_10);
    return false;
  }
}
