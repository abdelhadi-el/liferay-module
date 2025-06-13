package org.apache.jsp.image_005fgallery_005fdisplay;

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

public final class view_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList<String>(4);
    _jspx_dependants.add("/image_gallery_display/init.jsp");
    _jspx_dependants.add("/init.jsp");
    _jspx_dependants.add("/init-ext.jsp");
    _jspx_dependants.add("/image_gallery_display/init-ext.jsp");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_actionURL_var_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_namespace_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_header_title_localizeTitle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1asset_categorization$1filter_portletURL_assetType_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_param_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_key_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_aui_row;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1trash_undo_portletURL_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_if_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_header_title_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_icon_markupView_icon_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_portlet_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ddm_template$1renderer_entries_displayStyleGroupId_displayStyle_contextObjects_className;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1theme_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_choose;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1frontend_defineObjects_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_liferay$1util_include_servletContext_page_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_when_test;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_portlet_actionURL_var_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_namespace_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_header_title_localizeTitle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1asset_categorization$1filter_portletURL_assetType_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_param_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_key_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_aui_row = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1trash_undo_portletURL_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_if_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_header_title_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_icon_markupView_icon_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_portlet_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ddm_template$1renderer_entries_displayStyleGroupId_displayStyle_contextObjects_className = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1theme_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_choose = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_when_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_portlet_actionURL_var_name.release();
    _jspx_tagPool_portlet_namespace_nobody.release();
    _jspx_tagPool_liferay$1ui_header_title_localizeTitle_nobody.release();
    _jspx_tagPool_liferay$1asset_categorization$1filter_portletURL_assetType_nobody.release();
    _jspx_tagPool_portlet_param_value_name_nobody.release();
    _jspx_tagPool_liferay$1ui_message_key_nobody.release();
    _jspx_tagPool_aui_row.release();
    _jspx_tagPool_liferay$1trash_undo_portletURL_nobody.release();
    _jspx_tagPool_c_if_test.release();
    _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.release();
    _jspx_tagPool_liferay$1ui_header_title_nobody.release();
    _jspx_tagPool_liferay$1ui_icon_markupView_icon_nobody.release();
    _jspx_tagPool_portlet_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1ddm_template$1renderer_entries_displayStyleGroupId_displayStyle_contextObjects_className.release();
    _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.release();
    _jspx_tagPool_liferay$1theme_defineObjects_nobody.release();
    _jspx_tagPool_c_choose.release();
    _jspx_tagPool_liferay$1frontend_defineObjects_nobody.release();
    _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.release();
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
      out.write('\n');
      out.write('\n');

if (layout.isTypeControlPanel()) {
	portletPreferences = PortletPreferencesLocalServiceUtil.getPreferences(themeDisplay.getCompanyId(), scopeGroupId, PortletKeys.PREFS_OWNER_TYPE_GROUP, 0, DLPortletKeys.DOCUMENT_LIBRARY, null);
}

IGRequestHelper igRequestHelper = new IGRequestHelper(request);

String portletResource = igRequestHelper.getPortletResource();

DLPortletInstanceSettings dlPortletInstanceSettings = igRequestHelper.getDLPortletInstanceSettings();

long rootFolderId = dlPortletInstanceSettings.getRootFolderId();
String rootFolderName = StringPool.BLANK;

if (rootFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
	try {
		Folder rootFolder = DLAppLocalServiceUtil.getFolder(rootFolderId);

		rootFolderName = rootFolder.getName();

		if (rootFolder.getGroupId() != scopeGroupId) {
			rootFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
			rootFolderName = StringPool.BLANK;
		}
	}
	catch (NoSuchFolderException nsfe) {
		rootFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}
}

String displayStyle = portletPreferences.getValue("displayStyle", StringPool.BLANK);
long displayStyleGroupId = GetterUtil.getLong(portletPreferences.getValue("displayStyleGroupId", null), themeDisplay.getScopeGroupId());

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');

String redirect = ParamUtil.getString(request, "redirect");

Folder folder = (Folder)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FOLDER);

long folderId = BeanParamUtil.getLong(folder, request, "folderId", rootFolderId);

boolean defaultFolderView = false;

if ((folder == null) && (rootFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {
	defaultFolderView = true;
}

if (defaultFolderView) {
	try {
		folder = DLAppLocalServiceUtil.getFolder(folderId);
	}
	catch (NoSuchFolderException nsfe) {
		folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}
}

long repositoryId = scopeGroupId;

if (folder != null) {
	repositoryId = folder.getRepositoryId();
}

int status = WorkflowConstants.STATUS_APPROVED;

if (permissionChecker.isContentReviewer(user.getCompanyId(), scopeGroupId)) {
	status = WorkflowConstants.STATUS_ANY;
}

Map<String, Object> contextObjects = new HashMap<String, Object>();

contextObjects.put("dlPortletInstanceSettings", dlPortletInstanceSettings);

String[] mediaGalleryMimeTypes = dlPortletInstanceSettings.getMimeTypes();

      out.write('\n');
      out.write('\n');
      //  liferay-ddm:template-renderer
      com.liferay.dynamic.data.mapping.taglib.servlet.taglib.TemplateRendererTag _jspx_th_liferay$1ddm_template$1renderer_0 = (com.liferay.dynamic.data.mapping.taglib.servlet.taglib.TemplateRendererTag) _jspx_tagPool_liferay$1ddm_template$1renderer_entries_displayStyleGroupId_displayStyle_contextObjects_className.get(com.liferay.dynamic.data.mapping.taglib.servlet.taglib.TemplateRendererTag.class);
      _jspx_th_liferay$1ddm_template$1renderer_0.setPageContext(_jspx_page_context);
      _jspx_th_liferay$1ddm_template$1renderer_0.setParent(null);
      _jspx_th_liferay$1ddm_template$1renderer_0.setClassName( FileEntry.class.getName() );
      _jspx_th_liferay$1ddm_template$1renderer_0.setContextObjects( contextObjects );
      _jspx_th_liferay$1ddm_template$1renderer_0.setDisplayStyle( displayStyle );
      _jspx_th_liferay$1ddm_template$1renderer_0.setDisplayStyleGroupId( displayStyleGroupId );
      _jspx_th_liferay$1ddm_template$1renderer_0.setEntries( DLAppServiceUtil.getGroupFileEntries(scopeGroupId, 0, folderId, mediaGalleryMimeTypes, status, 0, SearchContainer.MAX_DELTA, null) );
      int _jspx_eval_liferay$1ddm_template$1renderer_0 = _jspx_th_liferay$1ddm_template$1renderer_0.doStartTag();
      if (_jspx_eval_liferay$1ddm_template$1renderer_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n\n\t");

	String topLink = ParamUtil.getString(request, "topLink", "home");

	long assetCategoryId = ParamUtil.getLong(request, "categoryId");
	String assetTagName = ParamUtil.getString(request, "tag");

	boolean useAssetEntryQuery = (assetCategoryId > 0) || Validator.isNotNull(assetTagName);

	PortletURL portletURL = renderResponse.createRenderURL();

	portletURL.setParameter("mvcRenderCommandName", "/image_gallery_display/view");

	if (Validator.isNotNull(redirect)) {
		portletURL.setParameter("redirect", redirect);
	}

	portletURL.setParameter("topLink", topLink);
	portletURL.setParameter("folderId", String.valueOf(folderId));

	request.setAttribute("view.jsp-folder", folder);

	request.setAttribute("view.jsp-rootFolderId", String.valueOf(rootFolderId));

	request.setAttribute("view.jsp-folderId", String.valueOf(folderId));

	request.setAttribute("view.jsp-repositoryId", String.valueOf(repositoryId));

	request.setAttribute("view.jsp-viewFolder", Boolean.TRUE.toString());

	request.setAttribute("view.jsp-useAssetEntryQuery", String.valueOf(useAssetEntryQuery));

	request.setAttribute("view.jsp-portletURL", portletURL);
	
        out.write("\n\n\t");
        //  portlet:actionURL
        com.liferay.taglib.portlet.ActionURLTag _jspx_th_portlet_actionURL_0 = (com.liferay.taglib.portlet.ActionURLTag) _jspx_tagPool_portlet_actionURL_var_name.get(com.liferay.taglib.portlet.ActionURLTag.class);
        _jspx_th_portlet_actionURL_0.setPageContext(_jspx_page_context);
        _jspx_th_portlet_actionURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ddm_template$1renderer_0);
        _jspx_th_portlet_actionURL_0.setName("/document_library/edit_file_entry");
        _jspx_th_portlet_actionURL_0.setVar("restoreTrashEntriesURL");
        int _jspx_eval_portlet_actionURL_0 = _jspx_th_portlet_actionURL_0.doStartTag();
        if (_jspx_eval_portlet_actionURL_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n\t\t");
          //  portlet:param
          com.liferay.taglib.util.ParamTag _jspx_th_portlet_param_0 = (com.liferay.taglib.util.ParamTag) _jspx_tagPool_portlet_param_value_name_nobody.get(com.liferay.taglib.util.ParamTag.class);
          _jspx_th_portlet_param_0.setPageContext(_jspx_page_context);
          _jspx_th_portlet_param_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_portlet_actionURL_0);
          _jspx_th_portlet_param_0.setName( Constants.CMD );
          _jspx_th_portlet_param_0.setValue( Constants.RESTORE );
          int _jspx_eval_portlet_param_0 = _jspx_th_portlet_param_0.doStartTag();
          if (_jspx_th_portlet_param_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
            return;
          }
          _jspx_tagPool_portlet_param_value_name_nobody.reuse(_jspx_th_portlet_param_0);
          out.write('\n');
          out.write('	');
        }
        if (_jspx_th_portlet_actionURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_portlet_actionURL_var_name.reuse(_jspx_th_portlet_actionURL_0);
          return;
        }
        _jspx_tagPool_portlet_actionURL_var_name.reuse(_jspx_th_portlet_actionURL_0);
        java.lang.String restoreTrashEntriesURL = null;
        restoreTrashEntriesURL = (java.lang.String) _jspx_page_context.findAttribute("restoreTrashEntriesURL");
        out.write("\n\n\t");
        //  liferay-trash:undo
        com.liferay.trash.taglib.servlet.taglib.UndoTag _jspx_th_liferay$1trash_undo_0 = (com.liferay.trash.taglib.servlet.taglib.UndoTag) _jspx_tagPool_liferay$1trash_undo_portletURL_nobody.get(com.liferay.trash.taglib.servlet.taglib.UndoTag.class);
        _jspx_th_liferay$1trash_undo_0.setPageContext(_jspx_page_context);
        _jspx_th_liferay$1trash_undo_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ddm_template$1renderer_0);
        _jspx_th_liferay$1trash_undo_0.setPortletURL( restoreTrashEntriesURL );
        int _jspx_eval_liferay$1trash_undo_0 = _jspx_th_liferay$1trash_undo_0.doStartTag();
        if (_jspx_th_liferay$1trash_undo_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _jspx_tagPool_liferay$1trash_undo_portletURL_nobody.reuse(_jspx_th_liferay$1trash_undo_0);
          return;
        }
        _jspx_tagPool_liferay$1trash_undo_portletURL_nobody.reuse(_jspx_th_liferay$1trash_undo_0);
        out.write("\n\n\t");
        //  c:choose
        org.apache.taglibs.standard.tag.common.core.ChooseTag _jspx_th_c_choose_0 = (org.apache.taglibs.standard.tag.common.core.ChooseTag) _jspx_tagPool_c_choose.get(org.apache.taglibs.standard.tag.common.core.ChooseTag.class);
        _jspx_th_c_choose_0.setPageContext(_jspx_page_context);
        _jspx_th_c_choose_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1ddm_template$1renderer_0);
        int _jspx_eval_c_choose_0 = _jspx_th_c_choose_0.doStartTag();
        if (_jspx_eval_c_choose_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          do {
            out.write("\n\t\t");
            //  c:when
            org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_0 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
            _jspx_th_c_when_0.setPageContext(_jspx_page_context);
            _jspx_th_c_when_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
            _jspx_th_c_when_0.setTest( useAssetEntryQuery );
            int _jspx_eval_c_when_0 = _jspx_th_c_when_0.doStartTag();
            if (_jspx_eval_c_when_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              do {
                out.write("\n\t\t\t");
                //  liferay-asset:categorization-filter
                com.liferay.asset.taglib.servlet.taglib.CategorizationFilterTag _jspx_th_liferay$1asset_categorization$1filter_0 = (com.liferay.asset.taglib.servlet.taglib.CategorizationFilterTag) _jspx_tagPool_liferay$1asset_categorization$1filter_portletURL_assetType_nobody.get(com.liferay.asset.taglib.servlet.taglib.CategorizationFilterTag.class);
                _jspx_th_liferay$1asset_categorization$1filter_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1asset_categorization$1filter_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
                _jspx_th_liferay$1asset_categorization$1filter_0.setAssetType("images");
                _jspx_th_liferay$1asset_categorization$1filter_0.setPortletURL( portletURL );
                int _jspx_eval_liferay$1asset_categorization$1filter_0 = _jspx_th_liferay$1asset_categorization$1filter_0.doStartTag();
                if (_jspx_th_liferay$1asset_categorization$1filter_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1asset_categorization$1filter_portletURL_assetType_nobody.reuse(_jspx_th_liferay$1asset_categorization$1filter_0);
                  return;
                }
                _jspx_tagPool_liferay$1asset_categorization$1filter_portletURL_assetType_nobody.reuse(_jspx_th_liferay$1asset_categorization$1filter_0);
                out.write("\n\n\t\t\t");

			SearchContainer igSearchContainer = new SearchContainer(renderRequest, null, null, "cur2", SearchContainer.DEFAULT_DELTA, portletURL, null, null);

			long[] classNameIds = {PortalUtil.getClassNameId(DLFileEntryConstants.getClassName()), PortalUtil.getClassNameId(DLFileShortcutConstants.getClassName())};

			AssetEntryQuery assetEntryQuery = new AssetEntryQuery(classNameIds, igSearchContainer);

			assetEntryQuery.setEnablePermissions(true);
			assetEntryQuery.setExcludeZeroViewCount(false);

			int total = AssetEntryServiceUtil.getEntriesCount(assetEntryQuery);

			igSearchContainer.setTotal(total);

			List results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

			igSearchContainer.setResults(results);

			mediaGalleryMimeTypes = null;

			request.setAttribute("view.jsp-igSearchContainer", igSearchContainer);
			request.setAttribute("view.jsp-mediaGalleryMimeTypes", mediaGalleryMimeTypes);
			
                out.write("\n\n\t\t\t");
                //  liferay-util:include
                com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_0 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
                _jspx_th_liferay$1util_include_0.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1util_include_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_0);
                _jspx_th_liferay$1util_include_0.setPage("/image_gallery_display/view_images.jsp");
                _jspx_th_liferay$1util_include_0.setServletContext( application );
                int _jspx_eval_liferay$1util_include_0 = _jspx_th_liferay$1util_include_0.doStartTag();
                if (_jspx_th_liferay$1util_include_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
                  return;
                }
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_0);
                out.write("\n\t\t");
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
            out.write("\n\t\t");
            //  c:when
            org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_1 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
            _jspx_th_c_when_1.setPageContext(_jspx_page_context);
            _jspx_th_c_when_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
            _jspx_th_c_when_1.setTest( topLink.equals("home") );
            int _jspx_eval_c_when_1 = _jspx_th_c_when_1.doStartTag();
            if (_jspx_eval_c_when_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              do {
                out.write("\n\t\t\t");
                //  c:if
                org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_0 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                _jspx_th_c_if_0.setPageContext(_jspx_page_context);
                _jspx_th_c_if_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
                _jspx_th_c_if_0.setTest( folder != null );
                int _jspx_eval_c_if_0 = _jspx_th_c_if_0.doStartTag();
                if (_jspx_eval_c_if_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\t\t\t\t");
                    //  liferay-ui:header
                    com.liferay.taglib.ui.HeaderTag _jspx_th_liferay$1ui_header_0 = (com.liferay.taglib.ui.HeaderTag) _jspx_tagPool_liferay$1ui_header_title_localizeTitle_nobody.get(com.liferay.taglib.ui.HeaderTag.class);
                    _jspx_th_liferay$1ui_header_0.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ui_header_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_0);
                    _jspx_th_liferay$1ui_header_0.setLocalizeTitle( false );
                    _jspx_th_liferay$1ui_header_0.setTitle( folder.getName() );
                    int _jspx_eval_liferay$1ui_header_0 = _jspx_th_liferay$1ui_header_0.doStartTag();
                    if (_jspx_th_liferay$1ui_header_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ui_header_title_localizeTitle_nobody.reuse(_jspx_th_liferay$1ui_header_0);
                      return;
                    }
                    _jspx_tagPool_liferay$1ui_header_title_localizeTitle_nobody.reuse(_jspx_th_liferay$1ui_header_0);
                    out.write("\n\t\t\t");
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
                out.write("\n\n\t\t\t");

			SearchContainer igSearchContainer = new SearchContainer(renderRequest, null, null, "cur2", SearchContainer.DEFAULT_DELTA, portletURL, null, null);

			int foldersCount = DLAppServiceUtil.getFoldersCount(repositoryId, folderId, true);

			int total = DLAppServiceUtil.getFoldersAndFileEntriesAndFileShortcutsCount(repositoryId, folderId, status, mediaGalleryMimeTypes, true);

			int imagesCount = total - foldersCount;

			igSearchContainer.setTotal(total);

			List results = DLAppServiceUtil.getFoldersAndFileEntriesAndFileShortcuts(repositoryId, folderId, status, mediaGalleryMimeTypes, true, igSearchContainer.getStart(), igSearchContainer.getEnd(), igSearchContainer.getOrderByComparator());

			igSearchContainer.setResults(results);

			request.setAttribute("view.jsp-igSearchContainer", igSearchContainer);
			request.setAttribute("view.jsp-mediaGalleryMimeTypes", mediaGalleryMimeTypes);
			
                out.write("\n\n\t\t\t<div id=\"");
                if (_jspx_meth_portlet_namespace_0((javax.servlet.jsp.tagext.JspTag) _jspx_th_c_when_1, _jspx_page_context))
                  return;
                out.write("imageGalleryAssetInfo\">\n\t\t\t\t");
                //  c:if
                org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_1 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
                _jspx_th_c_if_1.setPageContext(_jspx_page_context);
                _jspx_th_c_if_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
                _jspx_th_c_if_1.setTest( folder != null );
                int _jspx_eval_c_if_1 = _jspx_th_c_if_1.doStartTag();
                if (_jspx_eval_c_if_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  do {
                    out.write("\n\t\t\t\t\t<div class=\"lfr-asset-description\">\n\t\t\t\t\t\t");
                    out.print( HtmlUtil.escape(folder.getDescription()) );
                    out.write("\n\t\t\t\t\t</div>\n\n\t\t\t\t\t<div class=\"lfr-asset-metadata\">\n\t\t\t\t\t\t<div class=\"icon-calendar lfr-asset-icon\">\n\t\t\t\t\t\t\t");
                    //  liferay-ui:message
                    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_0 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.get(com.liferay.taglib.ui.MessageTag.class);
                    _jspx_th_liferay$1ui_message_0.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ui_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
                    _jspx_th_liferay$1ui_message_0.setArguments( dateFormatDate.format(folder.getModifiedDate()) );
                    _jspx_th_liferay$1ui_message_0.setKey("last-updated-x");
                    _jspx_th_liferay$1ui_message_0.setTranslateArguments( false );
                    int _jspx_eval_liferay$1ui_message_0 = _jspx_th_liferay$1ui_message_0.doStartTag();
                    if (_jspx_th_liferay$1ui_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_0);
                      return;
                    }
                    _jspx_tagPool_liferay$1ui_message_translateArguments_key_arguments_nobody.reuse(_jspx_th_liferay$1ui_message_0);
                    out.write("\n\t\t\t\t\t\t</div>\n\n\t\t\t\t\t\t");

						AssetRendererFactory<?> dlFolderAssetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(DLFolder.class.getName());
						
                    out.write("\n\n\t\t\t\t\t\t<div class=\"lfr-asset-icon\">\n\t\t\t\t\t\t\t");
                    //  liferay-ui:icon
                    com.liferay.taglib.ui.IconTag _jspx_th_liferay$1ui_icon_0 = (com.liferay.taglib.ui.IconTag) _jspx_tagPool_liferay$1ui_icon_markupView_icon_nobody.get(com.liferay.taglib.ui.IconTag.class);
                    _jspx_th_liferay$1ui_icon_0.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ui_icon_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
                    _jspx_th_liferay$1ui_icon_0.setIcon( dlFolderAssetRendererFactory.getIconCssClass() );
                    _jspx_th_liferay$1ui_icon_0.setMarkupView("lexicon");
                    int _jspx_eval_liferay$1ui_icon_0 = _jspx_th_liferay$1ui_icon_0.doStartTag();
                    if (_jspx_th_liferay$1ui_icon_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ui_icon_markupView_icon_nobody.reuse(_jspx_th_liferay$1ui_icon_0);
                      return;
                    }
                    _jspx_tagPool_liferay$1ui_icon_markupView_icon_nobody.reuse(_jspx_th_liferay$1ui_icon_0);
                    out.write("\n\n\t\t\t\t\t\t\t");
                    out.print( foldersCount );
                    out.write(' ');
                    //  liferay-ui:message
                    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_1 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
                    _jspx_th_liferay$1ui_message_1.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ui_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
                    _jspx_th_liferay$1ui_message_1.setKey( (foldersCount == 1) ? "folder" : "folders" );
                    int _jspx_eval_liferay$1ui_message_1 = _jspx_th_liferay$1ui_message_1.doStartTag();
                    if (_jspx_th_liferay$1ui_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_1);
                      return;
                    }
                    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_1);
                    out.write("\n\t\t\t\t\t\t</div>\n\n\t\t\t\t\t\t");

						AssetRendererFactory<?> dlFileEntryAssetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(DLFileEntry.class.getName());
						
                    out.write("\n\n\t\t\t\t\t\t<div class=\"last lfr-asset-icon\">\n\t\t\t\t\t\t\t");
                    //  liferay-ui:icon
                    com.liferay.taglib.ui.IconTag _jspx_th_liferay$1ui_icon_1 = (com.liferay.taglib.ui.IconTag) _jspx_tagPool_liferay$1ui_icon_markupView_icon_nobody.get(com.liferay.taglib.ui.IconTag.class);
                    _jspx_th_liferay$1ui_icon_1.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ui_icon_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
                    _jspx_th_liferay$1ui_icon_1.setIcon( dlFileEntryAssetRendererFactory.getIconCssClass() );
                    _jspx_th_liferay$1ui_icon_1.setMarkupView("lexicon");
                    int _jspx_eval_liferay$1ui_icon_1 = _jspx_th_liferay$1ui_icon_1.doStartTag();
                    if (_jspx_th_liferay$1ui_icon_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ui_icon_markupView_icon_nobody.reuse(_jspx_th_liferay$1ui_icon_1);
                      return;
                    }
                    _jspx_tagPool_liferay$1ui_icon_markupView_icon_nobody.reuse(_jspx_th_liferay$1ui_icon_1);
                    out.write("\n\n\t\t\t\t\t\t\t");
                    out.print( imagesCount );
                    out.write(' ');
                    //  liferay-ui:message
                    com.liferay.taglib.ui.MessageTag _jspx_th_liferay$1ui_message_2 = (com.liferay.taglib.ui.MessageTag) _jspx_tagPool_liferay$1ui_message_key_nobody.get(com.liferay.taglib.ui.MessageTag.class);
                    _jspx_th_liferay$1ui_message_2.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1ui_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
                    _jspx_th_liferay$1ui_message_2.setKey( (imagesCount == 1) ? "image" : "images" );
                    int _jspx_eval_liferay$1ui_message_2 = _jspx_th_liferay$1ui_message_2.doStartTag();
                    if (_jspx_th_liferay$1ui_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_2);
                      return;
                    }
                    _jspx_tagPool_liferay$1ui_message_key_nobody.reuse(_jspx_th_liferay$1ui_message_2);
                    out.write("\n\t\t\t\t\t\t</div>\n\t\t\t\t\t</div>\n\n\t\t\t\t\t");
                    //  liferay-expando:custom-attributes-available
                    com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag _jspx_th_liferay$1expando_custom$1attributes$1available_0 = (com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag) _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.get(com.liferay.expando.taglib.servlet.taglib.CustomAttributesAvailableTag.class);
                    _jspx_th_liferay$1expando_custom$1attributes$1available_0.setPageContext(_jspx_page_context);
                    _jspx_th_liferay$1expando_custom$1attributes$1available_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_if_1);
                    _jspx_th_liferay$1expando_custom$1attributes$1available_0.setClassName( DLFolderConstants.getClassName() );
                    int _jspx_eval_liferay$1expando_custom$1attributes$1available_0 = _jspx_th_liferay$1expando_custom$1attributes$1available_0.doStartTag();
                    if (_jspx_eval_liferay$1expando_custom$1attributes$1available_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                      out.write("\n\t\t\t\t\t\t");
                      //  liferay-expando:custom-attribute-list
                      com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag _jspx_th_liferay$1expando_custom$1attribute$1list_0 = (com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag) _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.get(com.liferay.expando.taglib.servlet.taglib.CustomAttributeListTag.class);
                      _jspx_th_liferay$1expando_custom$1attribute$1list_0.setPageContext(_jspx_page_context);
                      _jspx_th_liferay$1expando_custom$1attribute$1list_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_liferay$1expando_custom$1attributes$1available_0);
                      _jspx_th_liferay$1expando_custom$1attribute$1list_0.setClassName( DLFolderConstants.getClassName() );
                      _jspx_th_liferay$1expando_custom$1attribute$1list_0.setClassPK( (folder != null) ? folder.getFolderId() : 0 );
                      _jspx_th_liferay$1expando_custom$1attribute$1list_0.setEditable( false );
                      _jspx_th_liferay$1expando_custom$1attribute$1list_0.setLabel( true );
                      int _jspx_eval_liferay$1expando_custom$1attribute$1list_0 = _jspx_th_liferay$1expando_custom$1attribute$1list_0.doStartTag();
                      if (_jspx_th_liferay$1expando_custom$1attribute$1list_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                        _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.reuse(_jspx_th_liferay$1expando_custom$1attribute$1list_0);
                        return;
                      }
                      _jspx_tagPool_liferay$1expando_custom$1attribute$1list_label_editable_classPK_className_nobody.reuse(_jspx_th_liferay$1expando_custom$1attribute$1list_0);
                      out.write("\n\t\t\t\t\t");
                    }
                    if (_jspx_th_liferay$1expando_custom$1attributes$1available_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                      _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.reuse(_jspx_th_liferay$1expando_custom$1attributes$1available_0);
                      return;
                    }
                    _jspx_tagPool_liferay$1expando_custom$1attributes$1available_className.reuse(_jspx_th_liferay$1expando_custom$1attributes$1available_0);
                    out.write("\n\t\t\t\t");
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
                out.write("\n\n\t\t\t\t");
                //  liferay-util:include
                com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_1 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
                _jspx_th_liferay$1util_include_1.setPageContext(_jspx_page_context);
                _jspx_th_liferay$1util_include_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
                _jspx_th_liferay$1util_include_1.setPage("/image_gallery_display/view_images.jsp");
                _jspx_th_liferay$1util_include_1.setServletContext( application );
                int _jspx_eval_liferay$1util_include_1 = _jspx_th_liferay$1util_include_1.doStartTag();
                if (_jspx_th_liferay$1util_include_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_1);
                  return;
                }
                _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_1);
                out.write("\n\t\t\t</div>\n\n\t\t\t");

			if (folder != null) {
				IGUtil.addPortletBreadcrumbEntries(folder, request, renderResponse);

				if (!defaultFolderView && portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {
					PortalUtil.setPageSubtitle(folder.getName(), request);
					PortalUtil.setPageDescription(folder.getDescription(), request);
				}
			}
			
                out.write("\n\n\t\t");
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
            //  c:when
            org.apache.taglibs.standard.tag.rt.core.WhenTag _jspx_th_c_when_2 = (org.apache.taglibs.standard.tag.rt.core.WhenTag) _jspx_tagPool_c_when_test.get(org.apache.taglibs.standard.tag.rt.core.WhenTag.class);
            _jspx_th_c_when_2.setPageContext(_jspx_page_context);
            _jspx_th_c_when_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_choose_0);
            _jspx_th_c_when_2.setTest( topLink.equals("mine") );
            int _jspx_eval_c_when_2 = _jspx_th_c_when_2.doStartTag();
            if (_jspx_eval_c_when_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              do {
                out.write("\n\n\t\t\t");

			long groupImagesUserId = 0;

			if (themeDisplay.isSignedIn()) {
				groupImagesUserId = user.getUserId();
			}

			SearchContainer igSearchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, null, null);

			int total = DLAppServiceUtil.getGroupFileEntriesCount(repositoryId, groupImagesUserId, rootFolderId, mediaGalleryMimeTypes, status);

			igSearchContainer.setTotal(total);

			List results = DLAppServiceUtil.getGroupFileEntries(repositoryId, groupImagesUserId, rootFolderId, mediaGalleryMimeTypes, status, igSearchContainer.getStart(), igSearchContainer.getEnd(), igSearchContainer.getOrderByComparator());

			igSearchContainer.setResults(results);

			request.setAttribute("view.jsp-igSearchContainer", igSearchContainer);
			request.setAttribute("view.jsp-mediaGalleryMimeTypes", mediaGalleryMimeTypes);
			
                out.write("\n\n\t\t\t");
                //  aui:row
                com.liferay.taglib.aui.RowTag _jspx_th_aui_row_0 = (com.liferay.taglib.aui.RowTag) _jspx_tagPool_aui_row.get(com.liferay.taglib.aui.RowTag.class);
                _jspx_th_aui_row_0.setPageContext(_jspx_page_context);
                _jspx_th_aui_row_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_2);
                int _jspx_eval_aui_row_0 = _jspx_th_aui_row_0.doStartTag();
                if (_jspx_eval_aui_row_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  out.write("\n\t\t\t\t");
                  //  liferay-ui:header
                  com.liferay.taglib.ui.HeaderTag _jspx_th_liferay$1ui_header_1 = (com.liferay.taglib.ui.HeaderTag) _jspx_tagPool_liferay$1ui_header_title_nobody.get(com.liferay.taglib.ui.HeaderTag.class);
                  _jspx_th_liferay$1ui_header_1.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1ui_header_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_row_0);
                  _jspx_th_liferay$1ui_header_1.setTitle( topLink );
                  int _jspx_eval_liferay$1ui_header_1 = _jspx_th_liferay$1ui_header_1.doStartTag();
                  if (_jspx_th_liferay$1ui_header_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1ui_header_title_nobody.reuse(_jspx_th_liferay$1ui_header_1);
                    return;
                  }
                  _jspx_tagPool_liferay$1ui_header_title_nobody.reuse(_jspx_th_liferay$1ui_header_1);
                  out.write("\n\n\t\t\t\t");
                  //  liferay-util:include
                  com.liferay.taglib.util.IncludeTag _jspx_th_liferay$1util_include_2 = (com.liferay.taglib.util.IncludeTag) _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.get(com.liferay.taglib.util.IncludeTag.class);
                  _jspx_th_liferay$1util_include_2.setPageContext(_jspx_page_context);
                  _jspx_th_liferay$1util_include_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_row_0);
                  _jspx_th_liferay$1util_include_2.setPage("/image_gallery_display/view_images.jsp");
                  _jspx_th_liferay$1util_include_2.setServletContext( application );
                  int _jspx_eval_liferay$1util_include_2 = _jspx_th_liferay$1util_include_2.doStartTag();
                  if (_jspx_th_liferay$1util_include_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                    _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_2);
                    return;
                  }
                  _jspx_tagPool_liferay$1util_include_servletContext_page_nobody.reuse(_jspx_th_liferay$1util_include_2);
                  out.write("\n\t\t\t");
                }
                if (_jspx_th_aui_row_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _jspx_tagPool_aui_row.reuse(_jspx_th_aui_row_0);
                  return;
                }
                _jspx_tagPool_aui_row.reuse(_jspx_th_aui_row_0);
                out.write("\n\n\t\t\t");

			PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, topLink), currentURL);

			PortalUtil.setPageSubtitle(LanguageUtil.get(request, topLink), request);
			
                out.write("\n\n\t\t");
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
        out.write('\n');
      }
      if (_jspx_th_liferay$1ddm_template$1renderer_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _jspx_tagPool_liferay$1ddm_template$1renderer_entries_displayStyleGroupId_displayStyle_contextObjects_className.reuse(_jspx_th_liferay$1ddm_template$1renderer_0);
        return;
      }
      _jspx_tagPool_liferay$1ddm_template$1renderer_entries_displayStyleGroupId_displayStyle_contextObjects_className.reuse(_jspx_th_liferay$1ddm_template$1renderer_0);
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

  private boolean _jspx_meth_portlet_namespace_0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_when_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  portlet:namespace
    com.liferay.taglib.portlet.NamespaceTag _jspx_th_portlet_namespace_0 = (com.liferay.taglib.portlet.NamespaceTag) _jspx_tagPool_portlet_namespace_nobody.get(com.liferay.taglib.portlet.NamespaceTag.class);
    _jspx_th_portlet_namespace_0.setPageContext(_jspx_page_context);
    _jspx_th_portlet_namespace_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_when_1);
    int _jspx_eval_portlet_namespace_0 = _jspx_th_portlet_namespace_0.doStartTag();
    if (_jspx_th_portlet_namespace_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
      return true;
    }
    _jspx_tagPool_portlet_namespace_nobody.reuse(_jspx_th_portlet_namespace_0);
    return false;
  }
}
