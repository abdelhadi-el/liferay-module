var link = document.createElement("link");
link.setAttribute("rel", "stylesheet");
link.setAttribute("type", "text/css");
link.setAttribute("href", Liferay.ThemeDisplay.getPathContext() + "/o/document-library-web/document_library/css/main.css");

function defineModule() {
  Liferay.Loader.define("document-library-web@4.0.86/document_library/css/main.scss", ['module', 'exports', 'require'], function (module, exports, require) {
    var define = undefined;

    module.exports = link;
  });
}

link.onload = defineModule;

link.onerror = function () {
  console.warn('Unable to load /o/document-library-web/document_library/css/main.css. However, its .js module will still be defined to avoid breaking execution flow (expect some visual degradation).');
  defineModule();
};

document.querySelector("head").appendChild(link);
//# sourceMappingURL=main.scss.js.map