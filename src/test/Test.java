package test;

import org.http.simple.JHttp;

public class Test {
    public static void main(String[] args) {
        System.out.println(JHttp.get("https://fb.com").body());
    }
}
