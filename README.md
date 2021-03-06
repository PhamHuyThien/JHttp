# `JHttp` - code request very simple.
Một thư viện tiện lợi đơn giản để sử dụng `HttpURLConnection`.  
Tham khảo [HttpRequest-kevinsawicki](https://github.com/kevinsawicki/http-request), nhưng thư viện này đã lỗi thời.  
`JHttp` sẽ giải quyết những gì mà [HttpRequest-kevinsawicki](https://github.com/kevinsawicki/http-request) còn thiếu sót.  

## Cách cài đặt
- truy cập `releases`, tìm và tải phiên bản mới nhất.
- Giải nén file zip để lấy thư viện (`.jar`).
- Trong IDE của bạn thêm thư viện vào Jar Library.

## Ví dụ
### Thực hiện việc get Request và lấy code status trả về
```java
int code = JHttp.get("https://facebook.com").code();
```
### Thực hiện get Request và lấy body html trả về
```java
String body = JHttp.get("https://facebook.com").body();
```
### Adding query parameters
```java
JHttp rq = JHttp.get("http://google.com", true, 'q', "baseball gloves", "size", 100);
// GET http://google.com?q=baseball%20gloves&size=100
```
### Lấy giá trị header
```java
String abc = JHttp.get("http://google.com")
                                .header("abc");
System.out.println("abc: " + abc);
```
### Perform a POST request with some data and get the status of the response
```java
int response = JHttp.post("http://google.com").send("name=thiendepzaii").code();
// int response = JHttp.post("http://google.com").send(true, name, "Thiên đẹp trai").code();
```
### Debug Error (New)
```java
JHttp rq = JHttp.post("http://google.comThienDz./").send("name=thiendepzaii").execute();
int errorCode = rq.getErrorCode();
int msg = rq.getErrorMessage();
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
- `getErrorCode(): int` lấy mã lỗi để debug
- `getErrorMessage(): String` lý do gây ra lỗi để debug

## Lịch sử cập nhật

#### v1.0.1 
- trả về String exept thay vì tự đặt như trước
- return String nếu lỗi sẽ trả về errorMessage
- return int nếu lỗi sẽ trả về errorCode
- còn lại sẽ trả về null

#### v1.0.0
- Ra mắt phiên bản đầu tiên

## Về tác giả
- Tên: Phạm Huy Thiên (SystemError)
- Cảm ơn: [HttpRequest-kevinsawicki](https://github.com/kevinsawicki/http-request)