package org.thiendz.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/*
    @Name       :   JJson
    @Author     :   ThienDepTraii
    @Version    :   2.0.0
    @Library    :   JsonSimple V1.1.1
 */
public class JJson implements Comparable<JJson> {

    private Object obj;

    public static JJson parse(String json) {
        return new JJson(json);
    }

    public static JJson parse(Object object) {
        return new JJson(object);
    }

    public static JJson cre() {
        return new JJson();
    }

    public JJson() {
    }

    public JJson(Object object) {
        this.obj = object;
    }

    public JJson(String json) {
        obj = JSONValue.parse(json);
    }

    public JJson k(String key) {
        JSONObject jsonObj = toJsonObject(this.obj);
        return new JJson(jsonObj != null ? jsonObj.get(key) : null);
    }

    public JJson i(int index) {
        JSONArray jsonArray = toJsonArray(this.obj);
        return new JJson(jsonArray != null ? jsonArray.get(index) : null);
    }

    public JJson q(String query) {
        Object objTmp = this.obj;
        //đầu vào hợp lệ
        if (objTmp == null) {
            return this;
        }
        //query hợp lệ
        if (stringRegex("^((\\.\\w+(\\[\\d+\\])*)|((\\[\\d+\\])*))+$", query) == null) {
            return new JJson("");
        }
        String[] querys = query.split("\\.");
        for (String q : querys) {
            if (q.equals("")) {
                continue;
            }
            if (q.contains("[")) { // of array
                if (!q.startsWith("[")) { //có name thì truy cập
                    String[] names = stringRegex("^(\\w+)\\[", q); // lấy tên
                    String name = names[0].replace("[", "");
                    objTmp = toJsonObject(objTmp).get(name); //đọc object xong mấy lấy index
                }
                String[] strIndexs = stringRegex("\\[(\\d+)\\]+", q);
                for (String strIndex : strIndexs) {
                    int index = Integer.parseInt(strIndex.replaceAll("\\[|\\]", ""));
                    JSONArray jsonArrayTmp = toJsonArray(objTmp);
                    objTmp = jsonArrayTmp != null ? jsonArrayTmp.get(index) : null;
                }
            } else { //of object
                JSONObject jsonObjectTmp = toJsonObject(objTmp);
                objTmp = jsonObjectTmp != null ? jsonObjectTmp.get(q) : null;
            }
        }
        return new JJson(objTmp);
    }

    public JJson arr(Object... objs) {
        JSONArray ja = this.obj == null ? new JSONArray() : (JSONArray) this.obj;
        ja.addAll(Arrays.asList(objs));
        return new JJson(ja);
    }

    public JJson obj(Object... pair) {
        if (pair.length % 2 == 0) {
            JSONObject jo = this.obj == null ? new JSONObject() : (JSONObject) this.obj;
            for (int i = 0; i < pair.length; i += 2) {
                jo.put(pair[i], pair[i + 1]);
            }
            return new JJson(jo);
        }
        return null;
    }

    public JJson put(Map<Object, Object> map) {
        if (map != null) {
            JSONObject jo = this.obj == null ? new JSONObject() : (JSONObject) this.obj;
            map.forEach((key, value) -> {
                jo.put(key, value);
            });
            return new JJson(jo);
        }
        return null;
    }

    public JJson put(List<Object> list) {
        if (list != null) {
            JSONArray ja = this.obj == null ? new JSONArray() : (JSONArray) this.obj;
            list.forEach((value) -> {
                ja.add(value);
            });
            return new JJson(ja);
        }
        return null;
    }

    public JJson min() {
        float[] fls = toFloats();
        if (fls == null) {
            return new JJson("");
        }
        Arrays.sort(fls);
        return new JJson((Object) fls[0]);
    }

    public JJson max() {
        float[] fls = toFloats();
        if (fls == null) {
            return new JJson("");
        }
        Arrays.sort(fls);
        return new JJson((Object) fls[fls.length - 1]);
    }

    public JJson sum() {
        float[] fls = toFloats();
        if (fls == null) {
            return new JJson("");
        }
        return new JJson((Object) sumFloat(fls));
    }

    public JJson avg() {
        float[] fls = toFloats();
        if (fls == null) {
            return new JJson("");
        }
        return new JJson((Object) (sumFloat(fls) / fls.length));
    }

    public JJson sort() {
        JJson[] json2Ts = toObjs();
        if (json2Ts == null) {
            return new JJson("");
        }
        Arrays.sort(json2Ts);
        return new JJson(JSONValue.parse(arraysToString(json2Ts)));
    }

    public JJson reverse() {
        JJson[] json2Ts = toObjs();
        for (int i = 0; i < json2Ts.length / 2; i++) {
            int idMax = json2Ts.length - 1 - i;
            JJson json2T = json2Ts[i];
            json2Ts[i] = json2Ts[idMax];
            json2Ts[idMax] = json2T;
        }
        return new JJson(JSONValue.parse(arraysToString(json2Ts)));
    }

    public JJson[][] toPairObjs() {
        JSONObject jsonObject = toJsonObject(this.obj);
        if (jsonObject == null) {
            return null;
        }
        JJson[][] jsonSimpleses = new JJson[jsonObject.size()][2];
        Iterator iterator = jsonObject.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            jsonSimpleses[i][0] = new JJson(pair.getKey());
            jsonSimpleses[i++][1] = new JJson(pair.getValue());
        }
        return jsonSimpleses;
    }

    public String[] toKeys() {
        JJson[][] json2Tses = toPairObjs();
        if (json2Tses == null) {
            return null;
        }
        String[] keys = new String[json2Tses.length];
        int i = 0;
        for (JJson[] json2T : json2Tses) {
            keys[i++] = json2T[0].toString();
        }
        return keys;
    }

    public JJson[] toValues() {
        JJson[][] json2Tses = toPairObjs();
        if (json2Tses == null) {
            return null;
        }
        JJson[] values = new JJson[json2Tses.length];
        int i = 0;
        for (JJson[] json2T : json2Tses) {
            values[i++] = json2T[1];
        }
        return values;
    }

    public JJson[] toObjs() {
        JSONArray jsonArray = toJsonArray(this.obj);
        if (jsonArray == null) {
            return null;
        }
        JJson[] jsonSimples = new JJson[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonSimples[i] = new JJson(jsonArray.get(i));
        }
        return jsonSimples;
    }

    public String[] toStrs() {
        JSONArray jsonArray = toJsonArray(this.obj);
        if (jsonArray == null) {
            return null;
        }
        String[] strings = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            strings[i] = jsonArray.get(i).toString();
        }
        return strings;
    }

    public char[] toChars() {
        JSONArray jsonArray = toJsonArray(this.obj);
        if (jsonArray == null) {
            return null;
        }
        char[] chars = new char[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            char charr = '0';
            try {
                charr = jsonArray.get(i).toString().toCharArray()[0];
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            chars[i] = charr;
        }
        return chars;
    }

    public int[] toInts() {
        JSONArray jsonArray = toJsonArray(this.obj);
        if (jsonArray == null) {
            return null;
        }
        int[] ints = new int[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            int intt = 0;
            try {
                intt = Integer.parseInt(strToInt(jsonArray.get(i).toString()));
            } catch (NumberFormatException e) {
            }
            ints[i] = intt;
        }
        return ints;
    }

    public long[] toLongs() {
        JSONArray jsonArray = toJsonArray(this.obj);
        if (jsonArray == null) {
            return null;
        }
        long[] longs = new long[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            long longg = 0;
            try {
                longg = Long.parseLong(strToInt(jsonArray.get(i).toString()));
            } catch (NumberFormatException e) {
            }
            longs[i] = longg;
        }
        return longs;
    }

    public double[] toDoubles() {
        JSONArray jsonArray = toJsonArray(this.obj);
        if (jsonArray == null) {
            return null;
        }
        double[] doubles = new double[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            double doublee = 0;
            try {
                doublee = Double.parseDouble(jsonArray.get(i).toString());
            } catch (NumberFormatException e) {
            }
            doubles[i] = doublee;
        }
        return doubles;
    }

    public float[] toFloats() {
        JSONArray jsonArray = toJsonArray(this.obj);
        if (jsonArray == null) {
            return null;
        }
        float[] floats = new float[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            float floatt = 0;
            try {
                floatt = Float.parseFloat(jsonArray.get(i).toString());
            } catch (NumberFormatException e) {
            }
            floats[i] = floatt;
        }
        return floats;
    }

    public boolean[] toBooleans() {
        JSONArray jsonArray = toJsonArray(this.obj);
        if (jsonArray == null) {
            return null;
        }
        boolean[] booleans = new boolean[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            boolean bool = false;
            try {
                bool = Boolean.parseBoolean(jsonArray.get(i).toString());
            } catch (Exception e) {
            }
            booleans[i] = bool;
        }
        return booleans;
    }

    public Object toObj() {
        return this.obj;
    }

    public String toStr() {
        return this.obj == null ? null : this.obj.toString();
    }

    @Override
    public String toString() {
        return toStr();
    }

    public char toChar() {
        try {
            return this.obj.toString().toCharArray()[0];
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return '0';
        }
    }

    public int toInt() {
        try {
            return Integer.parseInt(strToInt(this.obj.toString()));
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    public long toLong() {
        try {
            return Long.parseLong(strToInt(this.obj.toString()));
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    public double toDouble() {
        try {
            return Double.parseDouble(this.obj.toString());
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    public float toFloat() {
        try {
            return Float.parseFloat(this.obj.toString());
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    public boolean toBoolean() {
        try {
            return Boolean.parseBoolean(this.obj.toString());
        } catch (NumberFormatException | NullPointerException eS) {
            return false;
        }
    }

    public int length() {
        int lengthObject = isInstanceOfJsonObject(this.obj) ? toJsonObject(this.obj).size() : -1;
        int lengthArray = isInstanceOfJsonArray(this.obj) ? toJsonArray(this.obj).size() : -1;
        return lengthArray > lengthObject ? lengthArray : lengthObject;
    }

    @Override
    public int compareTo(JJson json2T) {
        if (this.obj == null) {
            return -1;
        }
        if (json2T == null) {
            return 1;
        }
        String oThis = this.toString();
        String oThat = json2T.toString();
        if (isNumber(oThis) && isNumber(oThat)) {
            return Float.parseFloat(oThis) > Float.parseFloat(oThat) ? 1 : -1;
        } else {
            int len1 = oThis.length();
            int len2 = oThat.length();
            int lim = Math.min(len1, len2);
            char v1[] = oThis.toCharArray();
            char v2[] = oThat.toCharArray();
            int k = 0;
            while (k < lim) {
                char c1 = v1[k];
                char c2 = v2[k];
                if (c1 != c2) {
                    return c1 - c2;
                }
                k++;
            }
            return len1 - len2;
        }

    }

    //
    private static JSONObject toJsonObject(Object object) {
        return isInstanceOfJsonObject(object) ? (JSONObject) JSONValue.parse(object.toString()) : null;
    }

    private static JSONArray toJsonArray(Object object) {
        return isInstanceOfJsonArray(object) ? (JSONArray) JSONValue.parse(object.toString()) : null;
    }

    //
    private static boolean isInstanceOfJsonObject(Object object) {
        return object == null ? false : object instanceof JSONObject || object instanceof JJson;
    }

    private static boolean isInstanceOfJsonArray(Object object) {
        return object == null ? false : object instanceof JSONArray || object instanceof JJson;
    }

    //
    private static String[] stringRegex(String regex, String input) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);

        ArrayList<String> alMatch = new ArrayList<>();
        while (m.find()) {
            alMatch.add(m.group());
        }
        String[] matchs = new String[alMatch.size()];
        for (int i = 0; i < matchs.length; i++) {
            matchs[i] = alMatch.get(i);
        }
        return matchs.length == 0 ? null : matchs;
    }

    private static String strToInt(String fl) {
        if (fl.contains(".")) {
            return fl.substring(0, fl.indexOf("."));
        }
        return fl;
    }

    private static float sumFloat(float[] fls) {
        float fl = 0;
        for (int i = 0; i < fls.length / 2; i++) {
            fl += fls[i] + fls[fls.length - 1 - i];
        }
        fl += fls.length % 2 == 0 ? 0 : fls[fls.length / 2];
        return fl;
    }

    private static String arraysToString(Object[] objs) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (Object obj : objs) {
            String str = obj == null ? "" : obj.toString().replaceAll("\"", "\\\\\"");
            stringBuilder.append("\"").append(str).append("\",");
        }

        return stringBuilder.toString().substring(0, stringBuilder.length() - 1) + "]";
    }

    private static boolean isNumber(String num) {
        try {
            float fl = Float.parseFloat(num);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }
}
