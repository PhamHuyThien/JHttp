# `JHttp` - code request very simple.
Một thư viện tiện lợi đơn giản, nhẹ và dễ sử dụng, không sử dụng thư viện ngoài.  
Tham khảo [`HttpRequest-kevinsawicki`](https://github.com/kevinsawicki/http-request), nhưng thư viện này không hoạt động được khi request https sử dụng proxy có authenticate.    
`JHttp` sẽ giải quyết những gì mà [`HttpRequest-kevinsawicki`](https://github.com/kevinsawicki/http-request) còn thiếu sót.  

## Cách cài đặt
- truy cập [`releases`](https://github.com/PhamHuyThien/JHttp/releases), tìm và tải phiên bản mới nhất.
- Giải nén file zip để lấy các thư viện (`.jar`).
- Trong IDE của bạn thêm thư viện vào `Jar Library`.

## Ví dụ
### Thực hiện việc get request và lấy code status trả về
```java
int code = JHttp.get("https://11x7.xyz/").code();
System.out.println(code);
```
### Thực hiện get request và lấy body html trả về
```java
String body = JHttp.get("https://11x7.xyz/").body();
System.out.println(body);
```
### Thực hiện post request và get response dạng json
```java
JJson json = JJson.parse("{\"user\": \"PhamHuyThen\", \"birthday\": \"11/07\"}");
String body = JHttp.post("https://11x7.xyz/").send(json).body();
// JJson respJson = JHttp.post("https://11x7.xyz/").send(json).json(); //lấy dữ liệu trả về dạng json
System.out.println(body);
```
### Adding query parameters
```java
JHttp rq = JHttp.get("https://11x7.xyz/", true, "name", "Phạm Huy Thiên", "birthday", "11/7/2000");
System.out.println(rq.body());
// GET https://11x7.xyz/?name=Ph%E1%BA%A1m%20Huy%20Thi%C3%AAn&birthday=11%2F7%2F2000
```
### Using arrays as query parameters
```java
int[] ids = new int[] { 22, 23 };
HttpRequest request = HttpRequest.get("https://11x7.xyz/", true, "id", ids);
System.out.println(request.toString()); // GET http://google.com?id[]=22&id[]=23
```

### Sử dụng cookie và user-agent nhanh
```java
String body = JHttp.get("https://11x7.xyz/").userAgent().cookie("Thien=Depzaii").body();
// JHttp.get("https://11x7.xyz/").userAgent(USERAGENT_DEFAULT).cookie("Thien=Depzaii").body();
System.out.println(body);
```

### Sử dụng proxy có authenticate
```java
String body = JHttp.get("https://11x7.xyz/").proxy("proxy.11x7.xyz", 6789).auth("userName", "p4ssw0rd").body();
System.out.println(body);
```

### Lấy giá trị header
```java
String cookie = JHttp.get("https://11x7.xyz/").header("Set-Cookie");
System.out.println("header name 'set-cookie'=" + cookie);
```
### Perform a POST request with some data and get the status of the response
```java
int response = JHttp.post("https://11x7.xyz/").send("name=Ph%E1%BA%A1m%20Huy%20Thi%C3%AAn").code();
// int response = JHttp.post("https://11x7.xyz/").send(true, name, "Phạm Huy Thiên").code();
System.out.println(response);
```
### debug error
```java
JHttp rq = JHttp.post("https://11x7.xyz/").send("name=thiendepzaii").execute();
int errorCode = rq.errorCode();
int msg = rq.errorMessage();
System.out.println("errorCode: "+errorCode+", errorMessage: "+errorMessage);
```
|ErrorCode|Debug|
|-----|------|
|`>0`|Request success, return code.|
|`=0`|Request not execute()|
|`-1`|Constructor `JHttp()` return exception|
|`-2`|`send(...):JHttp` return exception|
|`-3`|`execute():JHttp` return exception|
|`-4`|`header(String key):String` return exception|
|`-5`|`headers():Map<String, String>` return exception|
|`-6`|`json():JJson` or `body():String` return exception|

## Cấu trúc
|Type|Function|
|-----|------|
|`static`|`get(String url): JHttp`|
|`static`|`get(String url, boolean encode, Object... args):JHttp`|
|`static`|`[post, put, delete, connect, option...](...):JHttp`|
|`constructor`|`JHttp(String url):JHttp`|
||`method(String method):JHttp`|
||`headers(String headers):JHttp`|
||`headers(Map<String, String> map):JHttp`|
||`header(Object key, Object value):JHttp`|
||`cookie(String cookie):JHttp`|
||`userAgent():JHttp`|
||`userAgent(String userAgent):JHttp`|
||`proxy(String host, int port):JHttp`|
||`auth(String user, String pass):JHttp`|
||`timeout(int milis):JHttp`|
||`send(boolean encode, Object... params):JHttp`|
||`send(JJson json):JHttp`|
||`send(String data):JHttp`|
||`send(String data, String charset):JHttp`|
||`execute():JHttp`|
||`header(String key):String`|
||`headers():Map<String, String>`
||`json():JJson`|
||`body():String`|
||`code():int`|
||`message():String`|
||`errorCode():int`|
||`errorMessage():String`|

## Lịch sử cập nhật
|Version|Changed|
|-----|------|
|**v1.0.5**|update JJson lên phiên bản 2.0.1
|**v1.0.4**|thay HttpURLConnection sang HttpSocketConnection (lib riêng)|
||bỏ `body(String charset): String`, mặc định `JHttp.CHARSET_UTF8`|
||thêm các option function static các method cho tiện|
||thêm `proxy(String host, int port): JHttp` và `auth(String user, String pass): JHttp`|
||thêm `timeout(int milis): JHttp`|
||thêm `json(): JJson` chuyển body sang json|
||thêm `send(JJson json): JHttp` gửi dữ liệu đi (định dạng json)|
|**v1.0.3**|Cập nhật lại toàn bộ source cho mượn mà hơn|
||Update lại hàm `send(boolean encode, Object... pairs): JHttp` và các hàm tương tự.|
|**v1.0.2**|sử dụng `HttpsURLConnection` cho những request có protocol https|
||Đổi `getErrorCode(): int` thành `errorCode(): int`|
||Đổi `getErrorMessage(): String` thành `errorMessage(): String`|
|**v1.0.1**|trả về String except thay vì tự đặt như trước
||return String nếu lỗi sẽ trả về errorMessage|
||return int nếu lỗi sẽ trả về errorCode|
||còn lại sẽ trả về null|
|**v1.0.0**|Ra mắt phiên bản đầu tiên|

## Về tác giả
- Tên: Phạm Huy Thiên (SystemError)
- Cảm ơn: [`HttpRequest-kevinsawicki`](https://github.com/kevinsawicki/http-request)
- Liên hệ: [`Facebook`](https://fb.com/thiendz.systemerror)
- Sử dụng: JJson v2.0.0 và HttpSocketConnection v1.3