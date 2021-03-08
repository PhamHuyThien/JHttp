# `JHttp` - code request very simple.
Một thư viện tiện lợi đơn giản để sử dụng `HttpURLConnection` và `HttpsURLConnection`.  
Tham khảo [`HttpRequest-kevinsawicki`](https://github.com/kevinsawicki/http-request), nhưng thư viện này đã lỗi thời, Không được update trong nhiều năm.  
`JHttp` sẽ giải quyết những gì mà [`HttpRequest-kevinsawicki`](https://github.com/kevinsawicki/http-request) còn thiếu sót.  

## Cách cài đặt
- truy cập `releases`, tìm và tải phiên bản mới nhất.
- Giải nén file zip để lấy thư viện (`.jar`).
- Trong IDE của bạn thêm thư viện vào Jar Library.

## Ví dụ
### Thực hiện việc get Request và lấy code status trả về
```java
int code = JHttp.get("https://11x7.xyz/").code();
System.out.println(code);
```
### Thực hiện get Request và lấy body html trả về
```java
String body = JHttp.get("https://11x7.xyz/").body();
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

### Lấy giá trị header
```java
String abc = JHttp.get("https://11x7.xyz/").header("set-cookie");
System.out.println("header name 'set-cookie'=" + abc);
```
### Perform a POST request with some data and get the status of the response
```java
int response = JHttp.post("https://11x7.xyz/").send("name=Ph%E1%BA%A1m%20Huy%20Thi%C3%AAn").code();
// int response = JHttp.post("https://11x7.xyz/").send(true, name, "Phạm Huy Thiên").code();
```
### Debug Error (New)
```java
JHttp rq = JHttp.post("https://11x7.xyz/").send("name=thiendepzaii").execute();
int errorCode = rq.errorCode();
int msg = rq.errorMessage();
System.out.println("errorCode: "+errorCode+", errorMessage: "+errorMessage);
```

## Cấu trúc

### callback
- `get(String url): JHttp` thực hiện tạo JHttp có method là get
- `get(String url, boolean encode, Object... pairs): JHttp` thực hiện tạo JHttp có method là get, có param là pairs(key, value) encode sẽ mã hóa đầu vào
- `post(String url): JHttp` thực hiện tạo JHttp có method là post
- `post(String url, boolean encode, Object... pairs): JHttp` thực hiện tạo JHttp có method là post, có param là pairs(key, value) encode sẽ mã hóa đầu vào
- `method(String method): JHttp` set method cho request (mặc định `JHttp.METHOD_GET`)
- `header(String key, String value): JHttp` set header cho request
- `headers(String headers): JHttp` set list string header cho request (key:value\nkey1:value1\n...)
- `cookie(String cookie): JHttp` set cookie cho request
- `userAgent(): JHttp` set user-agent mặc định cho request (mặc định `JHttp.USERAGENT_DEFAULT`)
- `userAgent(String userAgent): JHttp` set user agent cho request
- `execute(): JHttp` chạy request (không cần sử dụng cũng được).
- `send(Object... pairs): JHttp` gửi dữ liệu đi (định dạng `(key, value, key1, value1, ...)`) (mặc định không encode).
- `send(boolean encode, Object... pairs): JHttp` gửi dữ liệu đi (định dạng `(key, value, key1, value1, ...)`).
- `send(String param): JHttp` gửi dữ liệu đi (định dạng `key=value&key1=value1&...`) (mặc định `JHttp.CHARSET_UTF8`)
- `send(String param, String charset): JHttp` gửi dữ liệu đi (định dạng `key=value&key1=value1&...`)
### get
- `header(String key): String` get header có name là String key
- `headers(): Map<String, String>` get all header có trong request
- `body(): String` lấy raw html (mặc định `JHttp.CHARSET_UTF8`)
- `body(String charset): String` lấy raw html
- `code(): int` mã code response
- `message(): String` string code response
- `errorCode(): int` lấy mã lỗi để debug
- `errorMessage(): String` lý do gây ra lỗi để debug

## Lịch sử cập nhật
#### v1.0.3
- Cập nhật lại toàn bộ source cho mượn mà hơn
- Update lại hàm `send(boolean encode, Object... pairs): JHttp` và các hàm tương tự.

#### v1.0.2
- sử dụng `HttpsURLConnection` cho những request có protocol https
- Đổi `getErrorCode(): int` thành `errorCode(): int`
- Đổi `getErrorMessage(): String` thành `errorMessage(): String`

#### v1.0.1 
- trả về String exept thay vì tự đặt như trước
- return String nếu lỗi sẽ trả về errorMessage
- return int nếu lỗi sẽ trả về errorCode
- còn lại sẽ trả về null

#### v1.0.0
- Ra mắt phiên bản đầu tiên

## Về tác giả
- Tên: Phạm Huy Thiên (SystemError)
- Cảm ơn: [`HttpRequest-kevinsawicki`](https://github.com/kevinsawicki/http-request)
- Liên hệ: [`Facebook`](https://fb.com/thiendz.systemerror)