package org.thiendz.net.simple;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.thiendz.json.JJson;
import org.thiendz.net.HttpSocketConnection;

public class JHttp {

    //========================================================================//
    public static JHttp connect(String url, boolean encode, Object... pairs) {
        return get(url, encode, pairs).method(METHOD_CONNECT);
    }

    public static JHttp connect(String url) {
        return get(url).method(METHOD_CONNECT);
    }

    public static JHttp options(String url, boolean encode, Object... pairs) {
        return get(url, encode, pairs).method(METHOD_OPTIONS);
    }

    public static JHttp options(String url) {
        return get(url).method(METHOD_OPTIONS);
    }

    public static JHttp delete(String url, boolean encode, Object... pairs) {
        return get(url, encode, pairs).method(METHOD_DELETE);
    }

    public static JHttp delete(String url) {
        return get(url).method(METHOD_DELETE);
    }

    public static JHttp put(String url, boolean encode, Object... pairs) {
        return get(url, encode, pairs).method(METHOD_PUT);
    }

    public static JHttp put(String url) {
        return get(url).method(METHOD_PUT);
    }

    public static JHttp post(String url, boolean encode, Object... pairs) {
        return get(url, encode, pairs).method(METHOD_POST);
    }

    public static JHttp post(String url) {
        return get(url).method(METHOD_POST);
    }

    public static JHttp get(String url) {
        return new JHttp(url);
    }

    public static JHttp get(String url, boolean encode, Object... pairs) {
        return new JHttp(url.trim() + "?" + encode(encode, pairs));
    }

    //========================================================================//
    public JHttp(String url) {
        try {
            setUrl(new URL(url));
            setHttpSocketConnection(new HttpSocketConnection(getUrl()));
        } catch (MalformedURLException ex) {
            setErrorCode(-1);
            setErrorMessage(ex);
        }
    }

    public JHttp method(String method) {
        if (errorCode() == 0) {
            getHttpSocketConnection().setRequestMethod(method);
        }
        return this;
    }

    public JHttp headers(String headers) {
        if (headers != null) {
            String[] strHeaders = headers.split("\\n");
            for (String strHeader : strHeaders) {
                String[] pairs = strHeader.split(":");
                String key = pairs[0].trim();
                String value = pairs.length > 0 ? pairs[1].trim() : "";
                header(key, value);
            }
        }
        return this;
    }

    public JHttp headers(Map<String, String> map) {
        if (map != null) {
            map.forEach((key, value) -> {
                header(key, value);
            });
        }
        return this;
    }

    public JHttp header(Object key, Object value) {
        if (errorCode() == 0) {
            String k = key.toString().toLowerCase();
            String v = value.toString();
            getHttpSocketConnection().setRequestProperty(k, v);
        }
        return this;
    }

    public JHttp cookie(String cookie) {
        if (errorCode() == 0) {
            getHttpSocketConnection().setRequestProperty(HEADER_KEY_COOKIE, cookie);
        }
        return this;
    }

    public JHttp userAgent() {
        return userAgent(USERAGENT_DEFAULT);
    }

    public JHttp userAgent(String userAgent) {
        if (errorCode() == 0) {
            getHttpSocketConnection().setRequestProperty(HEADER_KEY_USER_AGENT, userAgent);
        }
        return this;
    }

    public JHttp proxy(String host, int port) {
        if (errorCode() == 0) {
            getHttpSocketConnection().setProxy(host, port);
        }
        return this;
    }

    public JHttp auth(String user, String pass) {
        if (errorCode() == 0) {
            getHttpSocketConnection().setProxyAuth(user, pass);
        }
        return this;
    }

    public JHttp timeout(int milis) {
        if (errorCode() == 0) {
            getHttpSocketConnection().setConnectTimeout(milis);
        }
        return this;
    }

    public JHttp send(boolean encode, Object... params) {
        return send(encode(encode, params));
    }

    public JHttp send(JJson json) {
        header(HEADER_KEY_CONTENT_TYPE, HEADER_VALUE_CONTENT_TYPE_JSON);
        return send(json.toStr());
    }

    public JHttp send(String data) {
        return send(data, CHARSET_DEFAULT);
    }

    public JHttp send(String data, String charset) {
        if (errorCode() == 0) {
            final int HEADER_VALUE_CONTENT_LENGTH = data.length();
            if (!getHttpSocketConnection().getRequestProperties().containsKey(HEADER_KEY_CONTENT_TYPE)) {
                getHttpSocketConnection().setRequestProperty(HEADER_KEY_CONTENT_TYPE, HEADER_VALUE_CONTENT_TYPE_DEFAULT);
            }
            header(HEADER_KEY_CONTENT_LENGTH, HEADER_VALUE_CONTENT_LENGTH);
            try {
                OutputStream outputStream = getHttpSocketConnection().getOutputStream();
                outputStream.write(data.getBytes(charset));
            } catch (IOException ex) {
                setErrorCode(-2);
                setErrorMessage(ex);
            }
        }
        return this;
    }

    public JHttp execute() {
        if (errorCode() == 0) {
            try {
                getHttpSocketConnection().connect();
                setErrorCode(getHttpSocketConnection().getResponseCode());
                setErrorMessage(getHttpSocketConnection().getResponseMessage());
            } catch (IOException ex) {
                setErrorCode(-3);
                setErrorMessage(ex);
            }
        }
        return this;
    }

    public String header(String key) {
        execute();
        if (errorCode() > 0) {
            try {
                List<String> lValue = getHttpSocketConnection().getHeaderField(key);
                return lValue != null ? String.join("; ", lValue) : "";
            } catch (IOException ex) {
                setErrorCode(-4);
                setErrorMessage(ex);
            }
        }
        return null;
    }

    public Map<String, String> headers() {
        execute();
        if (errorCode() > 0) {
            try {
                Map<String, List<String>> mHeaders = getHttpSocketConnection().getHeaderFields();
                HashMap<String, String> hmResuls = new HashMap<>();
                mHeaders.forEach((key, listValue) -> {
                    hmResuls.put(key, String.join("; ", listValue));
                });
                return hmResuls;
            } catch (IOException ex) {
                setErrorCode(-5);
                setErrorMessage(ex);
            }
        }
        return null;
    }

    public JJson json() {
        return JJson.parse(body());
    }

    public String body() {
        execute();
        if (errorCode() > 0) {
            try {
                return getHttpSocketConnection().getResponseBody();
            } catch (IOException ex) {
                setErrorCode(-6);
                setErrorMessage(ex);
            }
        }
        return null;
    }

    public int code() {
        execute();
        return errorCode();
    }

    public String message() {
        execute();
        return errorMessage();
    }

    public int errorCode() {
        return errorCode;
    }

    public String errorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return getUrl().toString();
    }

    //========================================================================//
    private URL getUrl() {
        return url;
    }

    private void setUrl(URL url) {
        this.url = url;
    }

    private HttpSocketConnection getHttpSocketConnection() {
        return httpSocketConnection;
    }

    private void setHttpSocketConnection(HttpSocketConnection httpSocketConnection) {
        this.httpSocketConnection = httpSocketConnection;
    }

    private void setErrorMessage(Exception ex) {
        String strEx = ex.toString();
        char[] chars = strEx.toCharArray();
        int i;
        for (i = chars.length - 1; i > -1; i--) {
            if (chars[i] == '.') {
                break;
            }
        }
        String text = strEx.substring(i + 1);
        errorMessage = text.trim();
    }

    private void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    //========================================================================//
    private static String encode(boolean encode, Object... pairs) {
        StringBuilder sbParams = new StringBuilder();
        for (int i = 0; i < pairs.length; i += 2) {
            Object objKey = encode ? encode(pairs[i].toString()) : pairs[i];
            Object objvalue = i + 1 < pairs.length ? pairs[i + 1] : "";
            Object[] objValues = cast(objvalue);
            if (objValues != null) {
                objKey += encode ? encode("[]") : "[]";
                for (Object objVal : objValues) {
                    String valueEncode = encode ? encode(objVal.toString()) : objVal.toString();
                    sbParams.append(objKey).append("=").append(valueEncode).append("&");
                }
            } else {
                objvalue = encode ? encode(objvalue.toString()) : objvalue;
                sbParams.append(objKey).append("=").append(objvalue).append("&");
            }
        }
        return sbParams.toString().replaceFirst("&$", "");
    }

    private static Object[] cast(Object o) {
        Object[] objects = null;
        Class classInfo = o.getClass();
        if (classInfo.isArray()) {
            switch (classInfo.getComponentType().getName()) {
                case "int":
                    int[] intTmps = (int[]) o;
                    objects = new Object[intTmps.length];
                    for (int i = 0; i < intTmps.length; i++) {
                        objects[i] = (Object) intTmps[i];
                    }
                    break;
                case "long":
                    long[] longTmps = (long[]) o;
                    objects = new Object[longTmps.length];
                    for (int i = 0; i < longTmps.length; i++) {
                        objects[i] = (Object) longTmps[i];
                    }
                    break;
                case "double":
                    double[] doubleTmps = (double[]) o;
                    objects = new Object[doubleTmps.length];
                    for (int i = 0; i < doubleTmps.length; i++) {
                        objects[i] = (Object) doubleTmps[i];
                    }
                    break;
                case "float":
                    float[] floatTmps = (float[]) o;
                    objects = new Object[floatTmps.length];
                    for (int i = 0; i < floatTmps.length; i++) {
                        objects[i] = (Object) floatTmps[i];
                    }
                    break;
                case "char":
                    char[] charTmps = (char[]) o;
                    objects = new Object[charTmps.length];
                    for (int i = 0; i < charTmps.length; i++) {
                        objects[i] = (Object) charTmps[i];
                    }
                    break;
                default:
                    objects = (Object[]) o;
            }
        }
        return objects;
    }

    private static String encode(String text) {
        return encode(text, CHARSET_DEFAULT);
    }

    private static String encode(String text, String charset) {
        String encoder = null;
        try {
            encoder = URLEncoder.encode(text, charset);
        } catch (UnsupportedEncodingException e) {
        }
        return encoder;
    }

    //========================================================================//
    public static final String HEADER_KEY_COOKIE = "cookie";
    public static final String HEADER_KEY_USER_AGENT = "user-agent";
    public static final String HEADER_KEY_CONTENT_TYPE = "content-type";
    public static final String HEADER_KEY_CONTENT_LENGTH = "content-length";
    public static final String HEADER_KEY_ACCEPT = "accept";
    //
    public static final String HEADER_VALUE_CONTENT_TYPE_DEFAULT = "application/x-www-form-urlencoded";
    public static final String HEADER_VALUE_ACCEPT_DEFAULT = "*/*";
    public static final String HEADER_VALUE_CONTENT_TYPE_JSON = "application/json";
    //
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_TRADE = "TRADE";
    public static final String METHOD_CONNECT = "CONNECT";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_PATCH = "PATCH";
    public static final String METHOD_HEAD = "HEAD";
    //
    public static final String CHARSET_ASCII = "ASCII";
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CHARSET_UTF16 = "UTF-16";
    public static final String CHARSET_DEFAULT = CHARSET_UTF8;
    //
    public static final String USERAGENT_DESKTOP = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36";
    public static final String USERAGENT_MOBILE = "Mozilla/5.0 (Windows Phone 10.0; Android 6.0.1; Microsoft; Lumia 950) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Mobile Safari/537.36 Edge/15.14977";
    public static final String USERAGENT_TABLET = "Mozilla/5.0 (iPad; CPU OS 12_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.1.2 Mobile/15E148 Safari/604.1";
    public static final String USERAGENT_SAFARI = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1";
    public static final String USERAGENT_ANDROID = "Mozilla/5.0 (Linux; U; Android 4.4.2; en-us; SCH-I535 Build/KOT49H) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
    public static final String USERAGENT_JAVAMOBILE = "Opera/9.80 (J2ME/MIDP; Opera Mini/5.1.21214/28.2725; U; ru) Presto/2.8.119 Version/11.10";
    public static final String USERAGENT_DEFAULT = USERAGENT_DESKTOP;
    //
    public static final int CODE_OK = 200;
    public static final int CODE_CREATED = 201;
    public static final int CODE_ACCEPTED = 202;
    public static final int CODE_NOT_AUTHORITATIVE = 203;
    public static final int CODE_NO_CONTENT = 204;
    public static final int CODE_RESET = 205;
    public static final int CODE_PARTIAL = 206;
    public static final int CODE_MULT_CHOICE = 300;
    public static final int CODE_MOVED_PERM = 301;
    public static final int CODE_MOVED_TEMP = 302;
    public static final int CODE_SEE_OTHER = 303;
    public static final int CODE_NOT_MODIFIED = 304;
    public static final int CODE_USE_PROXY = 305;
    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_PAYMENT_REQUIRED = 402;
    public static final int CODE_FORBIDDEN = 403;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_BAD_METHOD = 405;
    public static final int CODE_NOT_ACCEPTABLE = 406;
    public static final int CODE_PROXY_AUTH = 407;
    public static final int CODE_CLIENT_TIMEOUT = 408;
    public static final int CODE_CONFLICT = 409;
    public static final int CODE_GONE = 410;
    public static final int CODE_LENGTH_REQUIRED = 411;
    public static final int CODE_PRECON_FAILED = 412;
    public static final int CODE_ENTITY_TOO_LARGE = 413;
    public static final int CODE_REQ_TOO_LONG = 414;
    public static final int CODE_UNSUPPORTED_TYPE = 415;
    public static final int CODE_SERVER_ERROR = 500;
    public static final int CODE_INTERNAL_ERROR = 500;
    public static final int CODE_NOT_IMPLEMENTED = 501;
    public static final int CODE_BAD_GATEWAY = 502;
    public static final int CODE_UNAVAILABLE = 503;
    public static final int CODE_GATEWAY_TIMEOUT = 504;
    public static final int CODE_VERSION = 505;
    //========================================================================//
    private URL url;
    private HttpSocketConnection httpSocketConnection;
    //
    private String errorMessage;
    private int errorCode;
}
