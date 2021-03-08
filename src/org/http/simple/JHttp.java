package org.http.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class JHttp {

    //========================================================================//
    public static JHttp post(String url, boolean encode, Object... pairs) {
        return JHttp.get(url, encode, pairs).method(JHttp.METHOD_POST);
    }

    public static JHttp post(String url) {
        return JHttp.get(url).method(JHttp.METHOD_POST);
    }

    public static JHttp get(String url) {
        return new JHttp(url);
    }

    public static JHttp get(String url, boolean encode, Object... pairs) {
        StringBuilder sbParams = new StringBuilder();
        for (int i = 0; i < pairs.length; i += 2) {
            String key = pairs[i].toString().trim();
            String value = i + 1 < pairs.length ? pairs[i + 1].toString().trim() : "";
            key = encode ? JHttp.encode(key) : key;
            value = encode ? JHttp.encode(value) : value;
            sbParams.append(key).append("=").append(value).append("&");
        }
        String param = sbParams.toString().replaceFirst("&$", "");
        url = url.trim();
        return new JHttp(url + "?" + param);
    }

    //========================================================================//
    public JHttp(String url) {
        try {
            this.setUrl(new URL(url));
            URLConnection urlConnection = this.getUrl().openConnection();
            this.setHttpURLConnection((HttpURLConnection) urlConnection);
            if (this.getUrl().getProtocol().equals("https")) {
                this.setHttpURLConnection((HttpsURLConnection) getHttpURLConnection());
            }
        } catch (MalformedURLException ex) {
            this.setErrorCode(-1);
            this.setErrorMessage(ex);
        } catch (IOException ex) {
            this.setErrorCode(-2);
            this.setErrorMessage(ex);
        }
    }

    public JHttp method(String method) {
        if (this.errorCode() == 0) {
            try {
                this.getHttpURLConnection().setRequestMethod(method);
            } catch (ProtocolException ex) {
                this.setErrorCode(-3);
                this.setErrorMessage(ex);
            }
        }
        return this;
    }

    public JHttp header(String key, String value) {
        if (this.errorCode() == 0) {
            key = key.trim().toLowerCase();
            value = value.trim();
            this.getHttpURLConnection().setRequestProperty(key, value);
        }
        return this;
    }

    public JHttp headers(String headers) {
        if (this.errorCode() == 0) {
            String[] strHeaders = headers.split("\\n");
            for (String strHeader : strHeaders) {
                String[] pairs = strHeader.split(":");
                String key = pairs[0].trim();
                String value = pairs.length > 0 ? pairs[1].trim() : "";
                this.getHttpURLConnection().setRequestProperty(key.toLowerCase(), value);
            }
        }
        return this;
    }

    public JHttp cookie(String cookie) {
        if (this.errorCode() == 0) {
            cookie = cookie.trim();
            this.getHttpURLConnection().setRequestProperty("cookie", cookie);
        }
        return this;
    }

    public JHttp userAgent() {
        if (this.errorCode() == 0) {
            this.userAgent(JHttp.USERAGENT_DEFAULT);
        }
        return this;
    }

    public JHttp userAgent(String userAgent) {
        if (this.errorCode() == 0) {
            this.getHttpURLConnection().setRequestProperty("user-agent", userAgent);
        }
        return this;
    }

    public JHttp execute() {
        if (this.errorCode() == 0) {
            try {
                this.getHttpURLConnection().connect();
                this.setErrorCode(this.getHttpURLConnection().getResponseCode());
                this.setErrorMessage("Request connected!");
            } catch (IOException ex) {
                setErrorCode(-4);
                setErrorMessage(ex);
            }
        }
        return this;
    }

    public JHttp send(boolean encode, Object... pairs) {
        if (this.errorCode() == 0 && this.getHttpURLConnection().getRequestMethod().equals(JHttp.METHOD_POST)) {
            StringBuilder sbParams = new StringBuilder();
            for (int i = 0; i < pairs.length; i += 2) {
                String key = pairs[i].toString().trim();
                String value = i + 1 < pairs.length ? pairs[i + 1].toString().trim() : "";
                key = encode ? JHttp.encode(key) : key;
                value = encode ? JHttp.encode(value) : value;
                sbParams.append(key).append("=").append(value).append("&");
            }
            this.send(sbParams.toString().replaceFirst("&$", ""));
        }
        return this;
    }

    public JHttp send(String param) {
        if (this.errorCode() == 0 && this.getHttpURLConnection().getRequestMethod().equals(JHttp.METHOD_POST)) {
            this.send(param, CHARSET_DEFAULT);
        }
        return this;
    }

    public JHttp send(String param, String charset) {
        if (this.errorCode() == 0 && this.getHttpURLConnection().getRequestMethod().equals(JHttp.METHOD_POST)) {
            this.header("content-length", param.getBytes().length + "");
            this.getHttpURLConnection().setDoOutput(true);
            try {
                OutputStream outputStream = this.getHttpURLConnection().getOutputStream();
                outputStream.write(param.getBytes(charset));
                this.setErrorCode(1);
                this.setErrorMessage("Request connected!");
            } catch (IOException ex) {
                this.setErrorCode(-5);
                this.setErrorMessage(ex);
            }
        }
        return this;
    }

    public String header(String key) {
        if (this.errorCode() == 0) {
            this.execute();
        }
        if (this.errorCode() > 0) {
            Map<String, String> mHeaders = this.headers();
            if (mHeaders.containsKey(key.toLowerCase())) {
                return mHeaders.get(key.toLowerCase());
            }
        }
        return this.errorMessage();
    }

    public Map<String, String> headers() {
        if (this.errorCode() == 0) {
            this.execute();
        }
        if (this.errorCode() > 0) {
            Map<String, List<String>> mHeaders = this.getHttpURLConnection().getHeaderFields();
            HashMap<String, String> hmResuls = new HashMap<>();
            mHeaders.entrySet().forEach((entry) -> {
                String key = entry.getKey();
                List<String> lValues = entry.getValue();
                StringBuilder sbValues = new StringBuilder();
                lValues.forEach((value) -> {
                    sbValues.append(value).append(lValues.size() > 1 ? "; " : "");
                });
                if (key == null) {
                    key = this.getHttpURLConnection().getRequestMethod();
                }
                hmResuls.put(key.toLowerCase(), sbValues.toString());
            });
            return hmResuls;
        }
        return null;
    }

    public String body() {
        if (this.errorCode() == 0) {
            this.execute();
        }
        if (this.errorCode() > 0) {
            return this.body(JHttp.CHARSET_DEFAULT);
        }
        return this.errorMessage();
    }

    public String body(String charset) {
        if (this.errorCode() == 0) {
            this.execute();
        }
        if (this.errorCode() > 0) {
            BufferedReader bufferedReader = null;
            InputStreamReader inputStreamReader = null;
            try {
                StringBuilder sbBody = new StringBuilder();
                inputStreamReader = new InputStreamReader(this.getHttpURLConnection().getInputStream(), charset);
                bufferedReader = new BufferedReader(inputStreamReader);
                String c;
                while ((c = bufferedReader.readLine()) != null) {
                    sbBody.append(c).append("\n");
                }
                return sbBody.toString().replaceFirst("\\n$", "");
            } catch (UnsupportedEncodingException ex) {
                setErrorCode(-6);
                setErrorMessage(ex);
            } catch (IOException ex) {
                setErrorCode(-7);
                setErrorMessage(ex);
            } finally {
                try {
                    bufferedReader.close();
                    inputStreamReader.close();
                } catch (IOException | NullPointerException ex) {
                }
            }
        }
        return this.errorMessage();
    }

    public int code() {
        if (this.errorCode() == 0) {
            this.execute();
        }
        if (this.errorCode() > 0) {
            try {
                setErrorCode(this.getHttpURLConnection().getResponseCode());
            } catch (IOException ex) {
                this.setErrorCode(-8);
                this.setErrorMessage(ex);
            }

        }
        return this.errorCode();
    }

    public String message() {
        if (this.errorCode() == 0) {
            this.execute();
        }
        if (this.errorCode() > 0) {
            try {
                String message = this.getHttpURLConnection().getResponseMessage();
                setErrorMessage(message);
            } catch (IOException ex) {
                this.setErrorCode(-9);
                this.setErrorMessage(ex);
            }

        }
        return this.errorMessage();
    }

    public String errorMessage() {
        return errorMessage;
    }

    public int errorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return this.getHttpURLConnection().getURL().toString();
    }

    //========================================================================//
    private URL getUrl() {
        return url;
    }

    private void setUrl(URL url) {
        this.url = url;
    }

    private HttpURLConnection getHttpURLConnection() {
        return httpURLConnection;
    }

    private void setHttpURLConnection(HttpURLConnection httpURLConnection) {
        this.httpURLConnection = httpURLConnection;
    }

    private void setErrorMessage(Exception ex) {
        String strEx = ex.toString();
        String text = strEx.substring(strEx.indexOf(":") + 1);
        this.errorMessage = text.trim();
    }

    private void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    //========================================================================//
    private static String encode(String text) {
        return encode(text, JHttp.CHARSET_DEFAULT);
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
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_TRADE = "TRADE";
    public static final String METHOD_CONNECT = "CONNECT";
    //
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CHARSET_US_ASCII = "US-ASCII";
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
    private HttpURLConnection httpURLConnection;
    //
    private String errorMessage;
    private int errorCode;
}
