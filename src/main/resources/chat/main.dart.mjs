// Returns whether the `js-string` built-in is supported.
function detectJsStringBuiltins() {
  let bytes = [
    0,   97,  115, 109, 1,   0,   0,  0,   1,   4,   1,   96,  0,
    0,   2,   23,  1,   14,  119, 97, 115, 109, 58,  106, 115, 45,
    115, 116, 114, 105, 110, 103, 4,  99,  97,  115, 116, 0,   0
  ];
  return WebAssembly.validate(
    new Uint8Array(bytes), {builtins: ['js-string']});
}

// Compiles a dart2wasm-generated main module from `source` which can then
// instantiatable via the `instantiate` method.
//
// `source` needs to be a `Response` object (or promise thereof) e.g. created
// via the `fetch()` JS API.
export async function compileStreaming(source) {
  const builtins = detectJsStringBuiltins()
      ? {builtins: ['js-string']} : {};
  return new CompiledApp(
      await WebAssembly.compileStreaming(source, builtins), builtins);
}

// Compiles a dart2wasm-generated wasm modules from `bytes` which is then
// instantiatable via the `instantiate` method.
export async function compile(bytes) {
  const builtins = detectJsStringBuiltins()
      ? {builtins: ['js-string']} : {};
  return new CompiledApp(await WebAssembly.compile(bytes, builtins), builtins);
}

// DEPRECATED: Please use `compile` or `compileStreaming` to get a compiled app,
// use `instantiate` method to get an instantiated app and then call
// `invokeMain` to invoke the main function.
export async function instantiate(modulePromise, importObjectPromise) {
  var moduleOrCompiledApp = await modulePromise;
  if (!(moduleOrCompiledApp instanceof CompiledApp)) {
    moduleOrCompiledApp = new CompiledApp(moduleOrCompiledApp);
  }
  const instantiatedApp = await moduleOrCompiledApp.instantiate(await importObjectPromise);
  return instantiatedApp.instantiatedModule;
}

// DEPRECATED: Please use `compile` or `compileStreaming` to get a compiled app,
// use `instantiate` method to get an instantiated app and then call
// `invokeMain` to invoke the main function.
export const invoke = (moduleInstance, ...args) => {
  moduleInstance.exports.$invokeMain(args);
}

class CompiledApp {
  constructor(module, builtins) {
    this.module = module;
    this.builtins = builtins;
  }

  // The second argument is an options object containing:
  // `loadDeferredWasm` is a JS function that takes a module name matching a
  //   wasm file produced by the dart2wasm compiler and returns the bytes to
  //   load the module. These bytes can be in either a format supported by
  //   `WebAssembly.compile` or `WebAssembly.compileStreaming`.
  async instantiate(additionalImports, {loadDeferredWasm} = {}) {
    let dartInstance;

    // Prints to the console
    function printToConsole(value) {
      if (typeof dartPrint == "function") {
        dartPrint(value);
        return;
      }
      if (typeof console == "object" && typeof console.log != "undefined") {
        console.log(value);
        return;
      }
      if (typeof print == "function") {
        print(value);
        return;
      }

      throw "Unable to print message: " + js;
    }

    // Converts a Dart List to a JS array. Any Dart objects will be converted, but
    // this will be cheap for JSValues.
    function arrayFromDartList(constructor, list) {
      const exports = dartInstance.exports;
      const read = exports.$listRead;
      const length = exports.$listLength(list);
      const array = new constructor(length);
      for (let i = 0; i < length; i++) {
        array[i] = read(list, i);
      }
      return array;
    }

    // A special symbol attached to functions that wrap Dart functions.
    const jsWrappedDartFunctionSymbol = Symbol("JSWrappedDartFunction");

    function finalizeWrapper(dartFunction, wrapped) {
      wrapped.dartFunction = dartFunction;
      wrapped[jsWrappedDartFunctionSymbol] = true;
      return wrapped;
    }

    // Imports
    const dart2wasm = {

      _1: (x0,x1,x2) => x0.set(x1,x2),
      _2: (x0,x1,x2) => x0.set(x1,x2),
      _6: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._6(f,arguments.length,x0) }),
      _7: x0 => new window.FinalizationRegistry(x0),
      _8: (x0,x1,x2,x3) => x0.register(x1,x2,x3),
      _9: (x0,x1) => x0.unregister(x1),
      _10: (x0,x1,x2) => x0.slice(x1,x2),
      _11: (x0,x1) => x0.decode(x1),
      _12: (x0,x1) => x0.segment(x1),
      _13: () => new TextDecoder(),
      _14: x0 => x0.buffer,
      _15: x0 => x0.wasmMemory,
      _16: () => globalThis.window._flutter_skwasmInstance,
      _17: x0 => x0.rasterStartMilliseconds,
      _18: x0 => x0.rasterEndMilliseconds,
      _19: x0 => x0.imageBitmaps,
      _192: x0 => x0.select(),
      _193: (x0,x1) => x0.append(x1),
      _194: x0 => x0.remove(),
      _197: x0 => x0.unlock(),
      _202: x0 => x0.getReader(),
      _211: x0 => new MutationObserver(x0),
      _222: (x0,x1,x2) => x0.addEventListener(x1,x2),
      _223: (x0,x1,x2) => x0.removeEventListener(x1,x2),
      _226: x0 => new ResizeObserver(x0),
      _229: (x0,x1) => new Intl.Segmenter(x0,x1),
      _230: x0 => x0.next(),
      _231: (x0,x1) => new Intl.v8BreakIterator(x0,x1),
      _308: x0 => x0.close(),
      _309: (x0,x1,x2,x3,x4) => ({type: x0,data: x1,premultiplyAlpha: x2,colorSpaceConversion: x3,preferAnimation: x4}),
      _310: x0 => new window.ImageDecoder(x0),
      _311: x0 => x0.close(),
      _312: x0 => ({frameIndex: x0}),
      _313: (x0,x1) => x0.decode(x1),
      _316: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._316(f,arguments.length,x0) }),
      _317: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._317(f,arguments.length,x0) }),
      _318: (x0,x1) => ({addView: x0,removeView: x1}),
      _319: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._319(f,arguments.length,x0) }),
      _320: f => finalizeWrapper(f, function() { return dartInstance.exports._320(f,arguments.length) }),
      _321: (x0,x1) => ({initializeEngine: x0,autoStart: x1}),
      _322: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._322(f,arguments.length,x0) }),
      _323: x0 => ({runApp: x0}),
      _324: x0 => new Uint8Array(x0),
      _326: x0 => x0.preventDefault(),
      _327: x0 => x0.stopPropagation(),
      _328: (x0,x1) => x0.addListener(x1),
      _329: (x0,x1) => x0.removeListener(x1),
      _330: (x0,x1) => x0.prepend(x1),
      _331: x0 => x0.remove(),
      _332: x0 => x0.disconnect(),
      _333: (x0,x1) => x0.addListener(x1),
      _334: (x0,x1) => x0.removeListener(x1),
      _336: (x0,x1) => x0.append(x1),
      _337: x0 => x0.remove(),
      _338: x0 => x0.stopPropagation(),
      _342: x0 => x0.preventDefault(),
      _343: (x0,x1) => x0.append(x1),
      _344: x0 => x0.remove(),
      _345: x0 => x0.preventDefault(),
      _350: (x0,x1) => x0.removeChild(x1),
      _351: (x0,x1) => x0.appendChild(x1),
      _352: (x0,x1,x2) => x0.insertBefore(x1,x2),
      _353: (x0,x1) => x0.appendChild(x1),
      _354: (x0,x1) => x0.transferFromImageBitmap(x1),
      _356: (x0,x1) => x0.append(x1),
      _357: (x0,x1) => x0.append(x1),
      _358: (x0,x1) => x0.append(x1),
      _359: x0 => x0.remove(),
      _360: x0 => x0.remove(),
      _361: x0 => x0.remove(),
      _362: (x0,x1) => x0.appendChild(x1),
      _363: (x0,x1) => x0.appendChild(x1),
      _364: x0 => x0.remove(),
      _365: (x0,x1) => x0.append(x1),
      _366: (x0,x1) => x0.append(x1),
      _367: x0 => x0.remove(),
      _368: (x0,x1) => x0.append(x1),
      _369: (x0,x1) => x0.append(x1),
      _370: (x0,x1,x2) => x0.insertBefore(x1,x2),
      _371: (x0,x1) => x0.append(x1),
      _372: (x0,x1,x2) => x0.insertBefore(x1,x2),
      _373: x0 => x0.remove(),
      _374: x0 => x0.remove(),
      _375: (x0,x1) => x0.append(x1),
      _376: x0 => x0.remove(),
      _377: (x0,x1) => x0.append(x1),
      _378: x0 => x0.remove(),
      _379: x0 => x0.remove(),
      _380: x0 => x0.getBoundingClientRect(),
      _381: x0 => x0.remove(),
      _394: (x0,x1) => x0.append(x1),
      _395: x0 => x0.remove(),
      _396: (x0,x1) => x0.append(x1),
      _397: (x0,x1,x2) => x0.insertBefore(x1,x2),
      _398: x0 => x0.preventDefault(),
      _399: x0 => x0.preventDefault(),
      _400: x0 => x0.preventDefault(),
      _401: x0 => x0.preventDefault(),
      _402: x0 => x0.remove(),
      _403: (x0,x1) => x0.observe(x1),
      _404: x0 => x0.disconnect(),
      _405: (x0,x1) => x0.appendChild(x1),
      _406: (x0,x1) => x0.appendChild(x1),
      _407: (x0,x1) => x0.appendChild(x1),
      _408: (x0,x1) => x0.append(x1),
      _409: x0 => x0.remove(),
      _410: (x0,x1) => x0.append(x1),
      _412: (x0,x1) => x0.appendChild(x1),
      _413: (x0,x1) => x0.append(x1),
      _414: x0 => x0.remove(),
      _415: (x0,x1) => x0.append(x1),
      _419: (x0,x1) => x0.appendChild(x1),
      _420: x0 => x0.remove(),
      _979: () => globalThis.window.flutterConfiguration,
      _980: x0 => x0.assetBase,
      _985: x0 => x0.debugShowSemanticsNodes,
      _986: x0 => x0.hostElement,
      _987: x0 => x0.multiViewEnabled,
      _988: x0 => x0.nonce,
      _990: x0 => x0.fontFallbackBaseUrl,
      _991: x0 => x0.useColorEmoji,
      _996: x0 => x0.console,
      _997: x0 => x0.devicePixelRatio,
      _998: x0 => x0.document,
      _999: x0 => x0.history,
      _1000: x0 => x0.innerHeight,
      _1001: x0 => x0.innerWidth,
      _1002: x0 => x0.location,
      _1003: x0 => x0.navigator,
      _1004: x0 => x0.visualViewport,
      _1005: x0 => x0.performance,
      _1008: (x0,x1) => x0.dispatchEvent(x1),
      _1009: (x0,x1) => x0.matchMedia(x1),
      _1011: (x0,x1) => x0.getComputedStyle(x1),
      _1012: x0 => x0.screen,
      _1013: (x0,x1) => x0.requestAnimationFrame(x1),
      _1014: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1014(f,arguments.length,x0) }),
      _1019: (x0,x1) => x0.warn(x1),
      _1022: () => globalThis.window,
      _1023: () => globalThis.Intl,
      _1024: () => globalThis.Symbol,
      _1027: x0 => x0.clipboard,
      _1028: x0 => x0.maxTouchPoints,
      _1029: x0 => x0.vendor,
      _1030: x0 => x0.language,
      _1031: x0 => x0.platform,
      _1032: x0 => x0.userAgent,
      _1033: x0 => x0.languages,
      _1034: x0 => x0.documentElement,
      _1035: (x0,x1) => x0.querySelector(x1),
      _1038: (x0,x1) => x0.createElement(x1),
      _1039: (x0,x1) => x0.execCommand(x1),
      _1043: (x0,x1) => x0.createTextNode(x1),
      _1044: (x0,x1) => x0.createEvent(x1),
      _1048: x0 => x0.head,
      _1049: x0 => x0.body,
      _1050: (x0,x1) => x0.title = x1,
      _1053: x0 => x0.activeElement,
      _1055: x0 => x0.visibilityState,
      _1057: x0 => x0.hasFocus(),
      _1058: () => globalThis.document,
      _1059: (x0,x1,x2,x3) => x0.addEventListener(x1,x2,x3),
      _1060: (x0,x1,x2,x3) => x0.addEventListener(x1,x2,x3),
      _1063: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1063(f,arguments.length,x0) }),
      _1064: x0 => x0.target,
      _1066: x0 => x0.timeStamp,
      _1067: x0 => x0.type,
      _1069: x0 => x0.preventDefault(),
      _1071: (x0,x1,x2,x3) => x0.initEvent(x1,x2,x3),
      _1078: x0 => x0.firstChild,
      _1083: x0 => x0.parentElement,
      _1085: x0 => x0.parentNode,
      _1088: (x0,x1) => x0.removeChild(x1),
      _1089: (x0,x1) => x0.removeChild(x1),
      _1090: x0 => x0.isConnected,
      _1091: (x0,x1) => x0.textContent = x1,
      _1093: (x0,x1) => x0.contains(x1),
      _1099: x0 => x0.firstElementChild,
      _1101: x0 => x0.nextElementSibling,
      _1102: x0 => x0.clientHeight,
      _1103: x0 => x0.clientWidth,
      _1104: x0 => x0.offsetHeight,
      _1105: x0 => x0.offsetWidth,
      _1106: x0 => x0.id,
      _1107: (x0,x1) => x0.id = x1,
      _1110: (x0,x1) => x0.spellcheck = x1,
      _1111: x0 => x0.tagName,
      _1112: x0 => x0.style,
      _1114: (x0,x1) => x0.append(x1),
      _1115: (x0,x1) => x0.getAttribute(x1),
      _1116: x0 => x0.getBoundingClientRect(),
      _1119: (x0,x1) => x0.closest(x1),
      _1122: (x0,x1) => x0.querySelectorAll(x1),
      _1124: x0 => x0.remove(),
      _1125: (x0,x1,x2) => x0.setAttribute(x1,x2),
      _1126: (x0,x1) => x0.removeAttribute(x1),
      _1127: (x0,x1) => x0.tabIndex = x1,
      _1129: (x0,x1) => x0.focus(x1),
      _1130: x0 => x0.scrollTop,
      _1131: (x0,x1) => x0.scrollTop = x1,
      _1132: x0 => x0.scrollLeft,
      _1133: (x0,x1) => x0.scrollLeft = x1,
      _1134: x0 => x0.classList,
      _1135: (x0,x1) => x0.className = x1,
      _1140: (x0,x1) => x0.getElementsByClassName(x1),
      _1142: x0 => x0.click(),
      _1144: (x0,x1) => x0.hasAttribute(x1),
      _1147: (x0,x1) => x0.attachShadow(x1),
      _1152: (x0,x1) => x0.getPropertyValue(x1),
      _1154: (x0,x1,x2,x3) => x0.setProperty(x1,x2,x3),
      _1156: (x0,x1) => x0.removeProperty(x1),
      _1158: x0 => x0.offsetLeft,
      _1159: x0 => x0.offsetTop,
      _1160: x0 => x0.offsetParent,
      _1162: (x0,x1) => x0.name = x1,
      _1163: x0 => x0.content,
      _1164: (x0,x1) => x0.content = x1,
      _1182: (x0,x1) => x0.nonce = x1,
      _1187: x0 => x0.now(),
      _1189: (x0,x1) => x0.width = x1,
      _1191: (x0,x1) => x0.height = x1,
      _1196: (x0,x1) => x0.getContext(x1),
      _1273: (x0,x1) => x0.fetch(x1),
      _1274: x0 => x0.status,
      _1275: x0 => x0.headers,
      _1276: x0 => x0.body,
      _1278: x0 => x0.arrayBuffer(),
      _1281: (x0,x1) => x0.get(x1),
      _1283: x0 => x0.read(),
      _1284: x0 => x0.value,
      _1285: x0 => x0.done,
      _1287: x0 => x0.name,
      _1288: x0 => x0.x,
      _1289: x0 => x0.y,
      _1292: x0 => x0.top,
      _1293: x0 => x0.right,
      _1294: x0 => x0.bottom,
      _1295: x0 => x0.left,
      _1304: x0 => x0.height,
      _1305: x0 => x0.width,
      _1306: (x0,x1) => x0.value = x1,
      _1308: (x0,x1) => x0.placeholder = x1,
      _1309: (x0,x1) => x0.name = x1,
      _1310: x0 => x0.selectionDirection,
      _1311: x0 => x0.selectionStart,
      _1312: x0 => x0.selectionEnd,
      _1315: x0 => x0.value,
      _1317: (x0,x1,x2) => x0.setSelectionRange(x1,x2),
      _1321: x0 => x0.readText(),
      _1322: (x0,x1) => x0.writeText(x1),
      _1323: x0 => x0.altKey,
      _1324: x0 => x0.code,
      _1325: x0 => x0.ctrlKey,
      _1326: x0 => x0.key,
      _1327: x0 => x0.keyCode,
      _1328: x0 => x0.location,
      _1329: x0 => x0.metaKey,
      _1330: x0 => x0.repeat,
      _1331: x0 => x0.shiftKey,
      _1332: x0 => x0.isComposing,
      _1333: (x0,x1) => x0.getModifierState(x1),
      _1335: x0 => x0.state,
      _1336: (x0,x1) => x0.go(x1),
      _1338: (x0,x1,x2,x3) => x0.pushState(x1,x2,x3),
      _1339: (x0,x1,x2,x3) => x0.replaceState(x1,x2,x3),
      _1340: x0 => x0.pathname,
      _1341: x0 => x0.search,
      _1342: x0 => x0.hash,
      _1346: x0 => x0.state,
      _1352: f => finalizeWrapper(f, function(x0,x1) { return dartInstance.exports._1352(f,arguments.length,x0,x1) }),
      _1354: (x0,x1,x2) => x0.observe(x1,x2),
      _1357: x0 => x0.attributeName,
      _1358: x0 => x0.type,
      _1359: x0 => x0.matches,
      _1362: x0 => x0.matches,
      _1364: x0 => x0.relatedTarget,
      _1365: x0 => x0.clientX,
      _1366: x0 => x0.clientY,
      _1367: x0 => x0.offsetX,
      _1368: x0 => x0.offsetY,
      _1371: x0 => x0.button,
      _1372: x0 => x0.buttons,
      _1373: x0 => x0.ctrlKey,
      _1374: (x0,x1) => x0.getModifierState(x1),
      _1377: x0 => x0.pointerId,
      _1378: x0 => x0.pointerType,
      _1379: x0 => x0.pressure,
      _1380: x0 => x0.tiltX,
      _1381: x0 => x0.tiltY,
      _1382: x0 => x0.getCoalescedEvents(),
      _1384: x0 => x0.deltaX,
      _1385: x0 => x0.deltaY,
      _1386: x0 => x0.wheelDeltaX,
      _1387: x0 => x0.wheelDeltaY,
      _1388: x0 => x0.deltaMode,
      _1394: x0 => x0.changedTouches,
      _1396: x0 => x0.clientX,
      _1397: x0 => x0.clientY,
      _1399: x0 => x0.data,
      _1402: (x0,x1) => x0.disabled = x1,
      _1403: (x0,x1) => x0.type = x1,
      _1404: (x0,x1) => x0.max = x1,
      _1405: (x0,x1) => x0.min = x1,
      _1406: (x0,x1) => x0.value = x1,
      _1407: x0 => x0.value,
      _1408: x0 => x0.disabled,
      _1409: (x0,x1) => x0.disabled = x1,
      _1410: (x0,x1) => x0.placeholder = x1,
      _1411: (x0,x1) => x0.name = x1,
      _1412: (x0,x1) => x0.autocomplete = x1,
      _1413: x0 => x0.selectionDirection,
      _1414: x0 => x0.selectionStart,
      _1415: x0 => x0.selectionEnd,
      _1419: (x0,x1,x2) => x0.setSelectionRange(x1,x2),
      _1424: (x0,x1) => x0.add(x1),
      _1427: (x0,x1) => x0.noValidate = x1,
      _1428: (x0,x1) => x0.method = x1,
      _1429: (x0,x1) => x0.action = x1,
      _1454: x0 => x0.orientation,
      _1455: x0 => x0.width,
      _1456: x0 => x0.height,
      _1457: (x0,x1) => x0.lock(x1),
      _1475: f => finalizeWrapper(f, function(x0,x1) { return dartInstance.exports._1475(f,arguments.length,x0,x1) }),
      _1486: x0 => x0.length,
      _1487: (x0,x1) => x0.item(x1),
      _1488: x0 => x0.length,
      _1489: (x0,x1) => x0.item(x1),
      _1490: x0 => x0.iterator,
      _1491: x0 => x0.Segmenter,
      _1492: x0 => x0.v8BreakIterator,
      _1495: x0 => x0.done,
      _1496: x0 => x0.value,
      _1497: x0 => x0.index,
      _1501: (x0,x1) => x0.adoptText(x1),
      _1502: x0 => x0.first(),
      _1503: x0 => x0.next(),
      _1504: x0 => x0.current(),
      _1516: x0 => x0.hostElement,
      _1517: x0 => x0.viewConstraints,
      _1519: x0 => x0.maxHeight,
      _1520: x0 => x0.maxWidth,
      _1521: x0 => x0.minHeight,
      _1522: x0 => x0.minWidth,
      _1523: x0 => x0.loader,
      _1524: () => globalThis._flutter,
      _1525: (x0,x1) => x0.didCreateEngineInitializer(x1),
      _1526: (x0,x1,x2) => x0.call(x1,x2),
      _1527: () => globalThis.Promise,
      _1528: f => finalizeWrapper(f, function(x0,x1) { return dartInstance.exports._1528(f,arguments.length,x0,x1) }),
      _1532: x0 => x0.length,
      _1535: x0 => x0.tracks,
      _1539: x0 => x0.image,
      _1544: x0 => x0.codedWidth,
      _1545: x0 => x0.codedHeight,
      _1548: x0 => x0.duration,
      _1552: x0 => x0.ready,
      _1553: x0 => x0.selectedTrack,
      _1554: x0 => x0.repetitionCount,
      _1555: x0 => x0.frameCount,
      _1599: (x0,x1,x2,x3) => x0.open(x1,x2,x3),
      _1600: (x0,x1,x2) => x0.setRequestHeader(x1,x2),
      _1601: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1601(f,arguments.length,x0) }),
      _1602: (x0,x1,x2) => x0.addEventListener(x1,x2),
      _1603: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1603(f,arguments.length,x0) }),
      _1604: x0 => x0.send(),
      _1605: () => new XMLHttpRequest(),
      _1606: () => globalThis.window.navigator.userAgent,
      _1618: (x0,x1,x2,x3) => x0.addEventListener(x1,x2,x3),
      _1619: (x0,x1,x2,x3) => x0.removeEventListener(x1,x2,x3),
      _1620: (x0,x1) => x0.createElement(x1),
      _1634: (x0,x1,x2,x3) => x0.open(x1,x2,x3),
      _1635: () => new AudioContext(),
      _1636: (x0,x1) => x0.createMediaElementSource(x1),
      _1637: x0 => x0.createStereoPanner(),
      _1638: (x0,x1) => x0.connect(x1),
      _1639: x0 => x0.load(),
      _1640: x0 => x0.remove(),
      _1641: x0 => x0.play(),
      _1642: x0 => x0.pause(),
      _1643: x0 => ({audio: x0}),
      _1644: (x0,x1) => x0.getUserMedia(x1),
      _1645: x0 => x0.getAudioTracks(),
      _1646: x0 => x0.stop(),
      _1647: (x0,x1) => x0.removeTrack(x1),
      _1648: x0 => x0.close(),
      _1652: x0 => ({sampleRate: x0}),
      _1653: x0 => new AudioContext(x0),
      _1654: (x0,x1) => x0.createMediaStreamSource(x1),
      _1655: (x0,x1) => x0.addModule(x1),
      _1656: x0 => ({parameterData: x0}),
      _1657: (x0,x1,x2) => new AudioWorkletNode(x0,x1,x2),
      _1658: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1658(f,arguments.length,x0) }),
      _1659: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1659(f,arguments.length,x0) }),
      _1660: x0 => x0.getAudioTracks(),
      _1661: x0 => x0.stop(),
      _1675: x0 => ({type: x0}),
      _1676: (x0,x1) => new Blob(x0,x1),
      _1683: (x0,x1) => x0.querySelector(x1),
      _1684: (x0,x1) => x0.appendChild(x1),
      _1685: (x0,x1) => x0.appendChild(x1),
      _1686: (x0,x1) => x0.item(x1),
      _1687: x0 => x0.remove(),
      _1688: x0 => x0.remove(),
      _1689: x0 => x0.remove(),
      _1690: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1690(f,arguments.length,x0) }),
      _1691: (x0,x1,x2) => x0.addEventListener(x1,x2),
      _1692: x0 => x0.click(),
      _1693: x0 => globalThis.URL.createObjectURL(x0),
      _1694: (x0,x1) => x0.getItem(x1),
      _1696: (x0,x1,x2) => x0.setItem(x1,x2),
      _1709: () => new Array(),
      _1710: x0 => new Array(x0),
      _1712: x0 => x0.length,
      _1714: (x0,x1) => x0[x1],
      _1715: (x0,x1,x2) => x0[x1] = x2,
      _1718: (x0,x1,x2) => new DataView(x0,x1,x2),
      _1720: x0 => new Int8Array(x0),
      _1721: (x0,x1,x2) => new Uint8Array(x0,x1,x2),
      _1722: x0 => new Uint8Array(x0),
      _1728: x0 => new Uint16Array(x0),
      _1730: x0 => new Int32Array(x0),
      _1732: x0 => new Uint32Array(x0),
      _1734: x0 => new Float32Array(x0),
      _1736: x0 => new Float64Array(x0),
      _1737: (o, t) => typeof o === t,
      _1738: (o, c) => o instanceof c,
      _1741: (o,s,v) => o[s] = v,
      _1742: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1742(f,arguments.length,x0) }),
      _1743: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1743(f,arguments.length,x0) }),
      _1768: () => Symbol("jsBoxedDartObjectProperty"),
      _1769: (decoder, codeUnits) => decoder.decode(codeUnits),
      _1770: () => new TextDecoder("utf-8", {fatal: true}),
      _1771: () => new TextDecoder("utf-8", {fatal: false}),
      _1772: x0 => new WeakRef(x0),
      _1773: x0 => x0.deref(),
      _1779: Date.now,
      _1781: s => new Date(s * 1000).getTimezoneOffset() * 60,
      _1782: s => {
        if (!/^\s*[+-]?(?:Infinity|NaN|(?:\.\d+|\d+(?:\.\d*)?)(?:[eE][+-]?\d+)?)\s*$/.test(s)) {
          return NaN;
        }
        return parseFloat(s);
      },
      _1783: () => {
        let stackString = new Error().stack.toString();
        let frames = stackString.split('\n');
        let drop = 2;
        if (frames[0] === 'Error') {
            drop += 1;
        }
        return frames.slice(drop).join('\n');
      },
      _1784: () => typeof dartUseDateNowForTicks !== "undefined",
      _1785: () => 1000 * performance.now(),
      _1786: () => Date.now(),
      _1787: () => {
        // On browsers return `globalThis.location.href`
        if (globalThis.location != null) {
          return globalThis.location.href;
        }
        return null;
      },
      _1788: () => {
        return typeof process != "undefined" &&
               Object.prototype.toString.call(process) == "[object process]" &&
               process.platform == "win32"
      },
      _1789: () => new WeakMap(),
      _1790: (map, o) => map.get(o),
      _1791: (map, o, v) => map.set(o, v),
      _1792: () => globalThis.WeakRef,
      _1802: s => JSON.stringify(s),
      _1803: s => printToConsole(s),
      _1804: a => a.join(''),
      _1805: (o, a, b) => o.replace(a, b),
      _1807: (s, t) => s.split(t),
      _1808: s => s.toLowerCase(),
      _1809: s => s.toUpperCase(),
      _1810: s => s.trim(),
      _1811: s => s.trimLeft(),
      _1812: s => s.trimRight(),
      _1814: (s, p, i) => s.indexOf(p, i),
      _1815: (s, p, i) => s.lastIndexOf(p, i),
      _1816: (s) => s.replace(/\$/g, "$$$$"),
      _1817: Object.is,
      _1818: s => s.toUpperCase(),
      _1819: s => s.toLowerCase(),
      _1820: (a, i) => a.push(i),
      _1821: (a, i) => a.splice(i, 1)[0],
      _1823: (a, l) => a.length = l,
      _1824: a => a.pop(),
      _1825: (a, i) => a.splice(i, 1),
      _1827: (a, s) => a.join(s),
      _1828: (a, s, e) => a.slice(s, e),
      _1829: (a, s, e) => a.splice(s, e),
      _1830: (a, b) => a == b ? 0 : (a > b ? 1 : -1),
      _1831: a => a.length,
      _1833: (a, i) => a[i],
      _1834: (a, i, v) => a[i] = v,
      _1836: (o, offsetInBytes, lengthInBytes) => {
        var dst = new ArrayBuffer(lengthInBytes);
        new Uint8Array(dst).set(new Uint8Array(o, offsetInBytes, lengthInBytes));
        return new DataView(dst);
      },
      _1837: (o, start, length) => new Uint8Array(o.buffer, o.byteOffset + start, length),
      _1838: (o, start, length) => new Int8Array(o.buffer, o.byteOffset + start, length),
      _1839: (o, start, length) => new Uint8ClampedArray(o.buffer, o.byteOffset + start, length),
      _1840: (o, start, length) => new Uint16Array(o.buffer, o.byteOffset + start, length),
      _1841: (o, start, length) => new Int16Array(o.buffer, o.byteOffset + start, length),
      _1842: (o, start, length) => new Uint32Array(o.buffer, o.byteOffset + start, length),
      _1843: (o, start, length) => new Int32Array(o.buffer, o.byteOffset + start, length),
      _1845: (o, start, length) => new BigInt64Array(o.buffer, o.byteOffset + start, length),
      _1846: (o, start, length) => new Float32Array(o.buffer, o.byteOffset + start, length),
      _1847: (o, start, length) => new Float64Array(o.buffer, o.byteOffset + start, length),
      _1848: (t, s) => t.set(s),
      _1849: l => new DataView(new ArrayBuffer(l)),
      _1850: (o) => new DataView(o.buffer, o.byteOffset, o.byteLength),
      _1852: o => o.buffer,
      _1853: o => o.byteOffset,
      _1854: Function.prototype.call.bind(Object.getOwnPropertyDescriptor(DataView.prototype, 'byteLength').get),
      _1855: (b, o) => new DataView(b, o),
      _1856: (b, o, l) => new DataView(b, o, l),
      _1857: Function.prototype.call.bind(DataView.prototype.getUint8),
      _1858: Function.prototype.call.bind(DataView.prototype.setUint8),
      _1859: Function.prototype.call.bind(DataView.prototype.getInt8),
      _1860: Function.prototype.call.bind(DataView.prototype.setInt8),
      _1861: Function.prototype.call.bind(DataView.prototype.getUint16),
      _1862: Function.prototype.call.bind(DataView.prototype.setUint16),
      _1863: Function.prototype.call.bind(DataView.prototype.getInt16),
      _1864: Function.prototype.call.bind(DataView.prototype.setInt16),
      _1865: Function.prototype.call.bind(DataView.prototype.getUint32),
      _1866: Function.prototype.call.bind(DataView.prototype.setUint32),
      _1867: Function.prototype.call.bind(DataView.prototype.getInt32),
      _1868: Function.prototype.call.bind(DataView.prototype.setInt32),
      _1871: Function.prototype.call.bind(DataView.prototype.getBigInt64),
      _1872: Function.prototype.call.bind(DataView.prototype.setBigInt64),
      _1873: Function.prototype.call.bind(DataView.prototype.getFloat32),
      _1874: Function.prototype.call.bind(DataView.prototype.setFloat32),
      _1875: Function.prototype.call.bind(DataView.prototype.getFloat64),
      _1876: Function.prototype.call.bind(DataView.prototype.setFloat64),
      _1889: (o, t) => o instanceof t,
      _1891: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1891(f,arguments.length,x0) }),
      _1892: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1892(f,arguments.length,x0) }),
      _1893: o => Object.keys(o),
      _1894: (ms, c) =>
      setTimeout(() => dartInstance.exports.$invokeCallback(c),ms),
      _1895: (handle) => clearTimeout(handle),
      _1896: (ms, c) =>
      setInterval(() => dartInstance.exports.$invokeCallback(c), ms),
      _1897: (handle) => clearInterval(handle),
      _1898: (c) =>
      queueMicrotask(() => dartInstance.exports.$invokeCallback(c)),
      _1899: () => Date.now(),
      _1900: (x0,x1) => new WebSocket(x0,x1),
      _1901: (x0,x1) => x0.send(x1),
      _1902: (x0,x1) => x0.send(x1),
      _1903: (x0,x1,x2) => x0.close(x1,x2),
      _1904: (x0,x1) => x0.close(x1),
      _1905: x0 => x0.close(),
      _1906: (x0,x1,x2,x3,x4,x5) => ({method: x0,headers: x1,body: x2,credentials: x3,redirect: x4,signal: x5}),
      _1907: (x0,x1,x2) => x0.fetch(x1,x2),
      _1908: (x0,x1) => x0.get(x1),
      _1909: f => finalizeWrapper(f, function(x0,x1,x2) { return dartInstance.exports._1909(f,arguments.length,x0,x1,x2) }),
      _1910: (x0,x1) => x0.forEach(x1),
      _1912: () => new AbortController(),
      _1913: x0 => x0.getReader(),
      _1914: x0 => x0.read(),
      _1915: x0 => x0.cancel(),
      _1917: x0 => globalThis.URL.createObjectURL(x0),
      _1919: () => new XMLHttpRequest(),
      _1920: (x0,x1,x2,x3) => x0.open(x1,x2,x3),
      _1921: x0 => x0.send(),
      _1923: () => new FileReader(),
      _1924: (x0,x1) => x0.readAsArrayBuffer(x1),
      _1925: (x0,x1) => x0.item(x1),
      _1926: (x0,x1) => x0.removeChild(x1),
      _1933: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1933(f,arguments.length,x0) }),
      _1934: f => finalizeWrapper(f, function(x0) { return dartInstance.exports._1934(f,arguments.length,x0) }),
      _1940: (x0,x1) => x0.appendChild(x1),
      _1941: x0 => x0.click(),
      _1942: (x0,x1) => x0.querySelector(x1),
      _1943: (x0,x1) => x0.appendChild(x1),
      _1948: (x0,x1) => x0.key(x1),
      _1964: (s, m) => {
        try {
          return new RegExp(s, m);
        } catch (e) {
          return String(e);
        }
      },
      _1965: (x0,x1) => x0.exec(x1),
      _1966: (x0,x1) => x0.test(x1),
      _1967: (x0,x1) => x0.exec(x1),
      _1968: (x0,x1) => x0.exec(x1),
      _1969: x0 => x0.pop(),
      _1971: o => o === undefined,
      _1990: o => typeof o === 'function' && o[jsWrappedDartFunctionSymbol] === true,
      _1992: o => {
        const proto = Object.getPrototypeOf(o);
        return proto === Object.prototype || proto === null;
      },
      _1993: o => o instanceof RegExp,
      _1994: (l, r) => l === r,
      _1995: o => o,
      _1996: o => o,
      _1997: o => o,
      _1998: b => !!b,
      _1999: o => o.length,
      _2002: (o, i) => o[i],
      _2003: f => f.dartFunction,
      _2004: l => arrayFromDartList(Int8Array, l),
      _2005: l => arrayFromDartList(Uint8Array, l),
      _2006: l => arrayFromDartList(Uint8ClampedArray, l),
      _2007: l => arrayFromDartList(Int16Array, l),
      _2008: l => arrayFromDartList(Uint16Array, l),
      _2009: l => arrayFromDartList(Int32Array, l),
      _2010: l => arrayFromDartList(Uint32Array, l),
      _2011: l => arrayFromDartList(Float32Array, l),
      _2012: l => arrayFromDartList(Float64Array, l),
      _2013: x0 => new ArrayBuffer(x0),
      _2014: (data, length) => {
        const getValue = dartInstance.exports.$byteDataGetUint8;
        const view = new DataView(new ArrayBuffer(length));
        for (let i = 0; i < length; i++) {
          view.setUint8(i, getValue(data, i));
        }
        return view;
      },
      _2015: l => arrayFromDartList(Array, l),
      _2016: (s, length) => {
        if (length == 0) return '';
      
        const read = dartInstance.exports.$stringRead1;
        let result = '';
        let index = 0;
        const chunkLength = Math.min(length - index, 500);
        let array = new Array(chunkLength);
        while (index < length) {
          const newChunkLength = Math.min(length - index, 500);
          for (let i = 0; i < newChunkLength; i++) {
            array[i] = read(s, index++);
          }
          if (newChunkLength < chunkLength) {
            array = array.slice(0, newChunkLength);
          }
          result += String.fromCharCode(...array);
        }
        return result;
      },
      _2017: (s, length) => {
        if (length == 0) return '';
      
        const read = dartInstance.exports.$stringRead2;
        let result = '';
        let index = 0;
        const chunkLength = Math.min(length - index, 500);
        let array = new Array(chunkLength);
        while (index < length) {
          const newChunkLength = Math.min(length - index, 500);
          for (let i = 0; i < newChunkLength; i++) {
            array[i] = read(s, index++);
          }
          if (newChunkLength < chunkLength) {
            array = array.slice(0, newChunkLength);
          }
          result += String.fromCharCode(...array);
        }
        return result;
      },
      _2018: (s) => {
        let length = s.length;
        let range = 0;
        for (let i = 0; i < length; i++) {
          range |= s.codePointAt(i);
        }
        const exports = dartInstance.exports;
        if (range < 256) {
          if (length <= 10) {
            if (length == 1) {
              return exports.$stringAllocate1_1(s.codePointAt(0));
            }
            if (length == 2) {
              return exports.$stringAllocate1_2(s.codePointAt(0), s.codePointAt(1));
            }
            if (length == 3) {
              return exports.$stringAllocate1_3(s.codePointAt(0), s.codePointAt(1), s.codePointAt(2));
            }
            if (length == 4) {
              return exports.$stringAllocate1_4(s.codePointAt(0), s.codePointAt(1), s.codePointAt(2), s.codePointAt(3));
            }
            if (length == 5) {
              return exports.$stringAllocate1_5(s.codePointAt(0), s.codePointAt(1), s.codePointAt(2), s.codePointAt(3), s.codePointAt(4));
            }
            if (length == 6) {
              return exports.$stringAllocate1_6(s.codePointAt(0), s.codePointAt(1), s.codePointAt(2), s.codePointAt(3), s.codePointAt(4), s.codePointAt(5));
            }
            if (length == 7) {
              return exports.$stringAllocate1_7(s.codePointAt(0), s.codePointAt(1), s.codePointAt(2), s.codePointAt(3), s.codePointAt(4), s.codePointAt(5), s.codePointAt(6));
            }
            if (length == 8) {
              return exports.$stringAllocate1_8(s.codePointAt(0), s.codePointAt(1), s.codePointAt(2), s.codePointAt(3), s.codePointAt(4), s.codePointAt(5), s.codePointAt(6), s.codePointAt(7));
            }
            if (length == 9) {
              return exports.$stringAllocate1_9(s.codePointAt(0), s.codePointAt(1), s.codePointAt(2), s.codePointAt(3), s.codePointAt(4), s.codePointAt(5), s.codePointAt(6), s.codePointAt(7), s.codePointAt(8));
            }
            if (length == 10) {
              return exports.$stringAllocate1_10(s.codePointAt(0), s.codePointAt(1), s.codePointAt(2), s.codePointAt(3), s.codePointAt(4), s.codePointAt(5), s.codePointAt(6), s.codePointAt(7), s.codePointAt(8), s.codePointAt(9));
            }
          }
          const dartString = exports.$stringAllocate1(length);
          const write = exports.$stringWrite1;
          for (let i = 0; i < length; i++) {
            write(dartString, i, s.codePointAt(i));
          }
          return dartString;
        } else {
          const dartString = exports.$stringAllocate2(length);
          const write = exports.$stringWrite2;
          for (let i = 0; i < length; i++) {
            write(dartString, i, s.charCodeAt(i));
          }
          return dartString;
        }
      },
      _2019: () => ({}),
      _2020: () => [],
      _2021: l => new Array(l),
      _2022: () => globalThis,
      _2023: (constructor, args) => {
        const factoryFunction = constructor.bind.apply(
            constructor, [null, ...args]);
        return new factoryFunction();
      },
      _2024: (o, p) => p in o,
      _2025: (o, p) => o[p],
      _2026: (o, p, v) => o[p] = v,
      _2027: (o, m, a) => o[m].apply(o, a),
      _2029: o => String(o),
      _2030: (p, s, f) => p.then(s, f),
      _2031: o => {
        if (o === undefined) return 1;
        var type = typeof o;
        if (type === 'boolean') return 2;
        if (type === 'number') return 3;
        if (type === 'string') return 4;
        if (o instanceof Array) return 5;
        if (ArrayBuffer.isView(o)) {
          if (o instanceof Int8Array) return 6;
          if (o instanceof Uint8Array) return 7;
          if (o instanceof Uint8ClampedArray) return 8;
          if (o instanceof Int16Array) return 9;
          if (o instanceof Uint16Array) return 10;
          if (o instanceof Int32Array) return 11;
          if (o instanceof Uint32Array) return 12;
          if (o instanceof Float32Array) return 13;
          if (o instanceof Float64Array) return 14;
          if (o instanceof DataView) return 15;
        }
        if (o instanceof ArrayBuffer) return 16;
        return 17;
      },
      _2032: (jsArray, jsArrayOffset, wasmArray, wasmArrayOffset, length) => {
        const getValue = dartInstance.exports.$wasmI8ArrayGet;
        for (let i = 0; i < length; i++) {
          jsArray[jsArrayOffset + i] = getValue(wasmArray, wasmArrayOffset + i);
        }
      },
      _2033: (jsArray, jsArrayOffset, wasmArray, wasmArrayOffset, length) => {
        const setValue = dartInstance.exports.$wasmI8ArraySet;
        for (let i = 0; i < length; i++) {
          setValue(wasmArray, wasmArrayOffset + i, jsArray[jsArrayOffset + i]);
        }
      },
      _2034: (jsArray, jsArrayOffset, wasmArray, wasmArrayOffset, length) => {
        const getValue = dartInstance.exports.$wasmI16ArrayGet;
        for (let i = 0; i < length; i++) {
          jsArray[jsArrayOffset + i] = getValue(wasmArray, wasmArrayOffset + i);
        }
      },
      _2035: (jsArray, jsArrayOffset, wasmArray, wasmArrayOffset, length) => {
        const setValue = dartInstance.exports.$wasmI16ArraySet;
        for (let i = 0; i < length; i++) {
          setValue(wasmArray, wasmArrayOffset + i, jsArray[jsArrayOffset + i]);
        }
      },
      _2036: (jsArray, jsArrayOffset, wasmArray, wasmArrayOffset, length) => {
        const getValue = dartInstance.exports.$wasmI32ArrayGet;
        for (let i = 0; i < length; i++) {
          jsArray[jsArrayOffset + i] = getValue(wasmArray, wasmArrayOffset + i);
        }
      },
      _2037: (jsArray, jsArrayOffset, wasmArray, wasmArrayOffset, length) => {
        const setValue = dartInstance.exports.$wasmI32ArraySet;
        for (let i = 0; i < length; i++) {
          setValue(wasmArray, wasmArrayOffset + i, jsArray[jsArrayOffset + i]);
        }
      },
      _2038: (jsArray, jsArrayOffset, wasmArray, wasmArrayOffset, length) => {
        const getValue = dartInstance.exports.$wasmF32ArrayGet;
        for (let i = 0; i < length; i++) {
          jsArray[jsArrayOffset + i] = getValue(wasmArray, wasmArrayOffset + i);
        }
      },
      _2039: (jsArray, jsArrayOffset, wasmArray, wasmArrayOffset, length) => {
        const setValue = dartInstance.exports.$wasmF32ArraySet;
        for (let i = 0; i < length; i++) {
          setValue(wasmArray, wasmArrayOffset + i, jsArray[jsArrayOffset + i]);
        }
      },
      _2040: (jsArray, jsArrayOffset, wasmArray, wasmArrayOffset, length) => {
        const getValue = dartInstance.exports.$wasmF64ArrayGet;
        for (let i = 0; i < length; i++) {
          jsArray[jsArrayOffset + i] = getValue(wasmArray, wasmArrayOffset + i);
        }
      },
      _2041: (jsArray, jsArrayOffset, wasmArray, wasmArrayOffset, length) => {
        const setValue = dartInstance.exports.$wasmF64ArraySet;
        for (let i = 0; i < length; i++) {
          setValue(wasmArray, wasmArrayOffset + i, jsArray[jsArrayOffset + i]);
        }
      },
      _2042: s => {
        if (/[[\]{}()*+?.\\^$|]/.test(s)) {
            s = s.replace(/[[\]{}()*+?.\\^$|]/g, '\\$&');
        }
        return s;
      },
      _2044: x0 => x0.input,
      _2045: x0 => x0.index,
      _2046: x0 => x0.groups,
      _2049: (x0,x1) => x0.exec(x1),
      _2051: x0 => x0.flags,
      _2052: x0 => x0.multiline,
      _2053: x0 => x0.ignoreCase,
      _2054: x0 => x0.unicode,
      _2055: x0 => x0.dotAll,
      _2056: (x0,x1) => x0.lastIndex = x1,
      _2058: (o, p) => o[p],
      _2061: v => v.toString(),
      _2062: (d, digits) => d.toFixed(digits),
      _2065: (d, precision) => d.toPrecision(precision),
      _2066: x0 => x0.random(),
      _2067: x0 => x0.random(),
      _2068: (x0,x1) => x0.getRandomValues(x1),
      _2069: () => globalThis.crypto,
      _2071: () => globalThis.Math,
      _2111: x0 => x0.status,
      _2112: (x0,x1) => x0.responseType = x1,
      _2114: x0 => x0.response,
      _2172: (x0,x1) => x0.responseType = x1,
      _2173: x0 => x0.response,
      _2612: (x0,x1) => x0.download = x1,
      _2637: (x0,x1) => x0.href = x1,
      _2860: x0 => x0.error,
      _2862: (x0,x1) => x0.src = x1,
      _2867: (x0,x1) => x0.crossOrigin = x1,
      _2870: (x0,x1) => x0.preload = x1,
      _2874: x0 => x0.currentTime,
      _2875: (x0,x1) => x0.currentTime = x1,
      _2876: x0 => x0.duration,
      _2881: (x0,x1) => x0.playbackRate = x1,
      _2890: (x0,x1) => x0.loop = x1,
      _2894: (x0,x1) => x0.volume = x1,
      _2911: x0 => x0.code,
      _2912: x0 => x0.message,
      _3185: (x0,x1) => x0.accept = x1,
      _3199: x0 => x0.files,
      _3225: (x0,x1) => x0.multiple = x1,
      _3243: (x0,x1) => x0.type = x1,
      _3985: () => globalThis.window,
      _4049: x0 => x0.navigator,
      _4313: x0 => x0.localStorage,
      _4373: x0 => x0.message,
      _4424: x0 => x0.mediaDevices,
      _4440: x0 => x0.userAgent,
      _4441: x0 => x0.vendor,
      _4491: x0 => x0.data,
      _4530: (x0,x1) => x0.onmessage = x1,
      _4660: x0 => x0.length,
      _4882: x0 => x0.readyState,
      _4891: x0 => x0.protocol,
      _4895: (x0,x1) => x0.binaryType = x1,
      _4898: x0 => x0.code,
      _4899: x0 => x0.reason,
      _6116: x0 => x0.destination,
      _6120: x0 => x0.state,
      _6121: x0 => x0.audioWorklet,
      _6494: x0 => x0.port,
      _6640: x0 => x0.type,
      _6683: x0 => x0.signal,
      _6696: x0 => x0.length,
      _6762: () => globalThis.document,
      _6855: x0 => x0.body,
      _7206: (x0,x1) => x0.id = x1,
      _7233: x0 => x0.children,
      _8587: x0 => x0.value,
      _8589: x0 => x0.done,
      _8775: x0 => x0.size,
      _8783: x0 => x0.name,
      _8784: x0 => x0.lastModified,
      _8790: x0 => x0.length,
      _8801: x0 => x0.result,
      _9308: x0 => x0.url,
      _9310: x0 => x0.status,
      _9312: x0 => x0.statusText,
      _9313: x0 => x0.headers,
      _9314: x0 => x0.body,
      _13736: () => globalThis.window.flutterCanvasKit,
      _13737: () => globalThis.window._flutter_skwasmInstance,

    };

    const baseImports = {
      dart2wasm: dart2wasm,


      Math: Math,
      Date: Date,
      Object: Object,
      Array: Array,
      Reflect: Reflect,
    };

    const jsStringPolyfill = {
      "charCodeAt": (s, i) => s.charCodeAt(i),
      "compare": (s1, s2) => {
        if (s1 < s2) return -1;
        if (s1 > s2) return 1;
        return 0;
      },
      "concat": (s1, s2) => s1 + s2,
      "equals": (s1, s2) => s1 === s2,
      "fromCharCode": (i) => String.fromCharCode(i),
      "length": (s) => s.length,
      "substring": (s, a, b) => s.substring(a, b),
    };

    const deferredLibraryHelper = {
      "loadModule": async (moduleName) => {
        if (!loadDeferredWasm) {
          throw "No implementation of loadDeferredWasm provided.";
        }
        const source = await Promise.resolve(loadDeferredWasm(moduleName));
        const module = await ((source instanceof Response)
            ? WebAssembly.compileStreaming(source, this.builtins)
            : WebAssembly.compile(source, this.builtins));
        return await WebAssembly.instantiate(module, {
          ...baseImports,
          ...additionalImports,
          "wasm:js-string": jsStringPolyfill,
          "module0": dartInstance.exports,
        });
      },
    };

    dartInstance = await WebAssembly.instantiate(this.module, {
      ...baseImports,
      ...additionalImports,
      "deferredLibraryHelper": deferredLibraryHelper,
      "wasm:js-string": jsStringPolyfill,
    });

    return new InstantiatedApp(this, dartInstance);
  }
}

class InstantiatedApp {
  constructor(compiledApp, instantiatedModule) {
    this.compiledApp = compiledApp;
    this.instantiatedModule = instantiatedModule;
  }

  // Call the main function with the given arguments.
  invokeMain(...args) {
    this.instantiatedModule.exports.$invokeMain(args);
  }
}

