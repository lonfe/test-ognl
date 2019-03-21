package test.ognl;

import ognl.Ognl;
import ognl.OgnlException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    /**
     * 对Root对象的访问
     */
    @Test
    public void test0() {
        Address address = new Address("666666", "天涯路海角街");
        User user = new User("lucy", "123456", address);
        try {
            System.out.println(Ognl.getValue("username", user));
            System.out.println(Ognl.getValue("address", user));
            System.out.println(Ognl.getValue("address.location", user));
        } catch (OgnlException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对上下文对象的访问
     * 使用OGNL的时候如果不设置上下文对象，系统会自动创建一个上下文对象，如果传入的参数当中包含了上下文对象则会使用传入的上下文对象。
     * 当访问上下文环境当中的参数时候，需要在表达式前面加上‘#’，表示了与访问Root对象的区别。
     */
    @Test
    public void test1() {
        Address address = new Address("666666", "天涯路海角街");
        User user = new User("lucy", "123456", address);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("init", "hello");
        context.put("user", user);
        try {
            System.out.println(Ognl.getValue("#init", context, user));
            System.out.println(Ognl.getValue("#user.username", context, user));
            System.out.println(Ognl.getValue("username", context, user));
        } catch (OgnlException e) {
            e.printStackTrace();
        }

    }

    /**
     * 对静态变量的访问
     * 在OGNL表达式当中也可以访问静态变量或者调用静态方法，格式如@[class]@[field/method()]
     */
    @Test
    public void test2() {
        try {
            System.out.println(Ognl.getValue("@test.ognl.Constant@ONE", null));
            System.out.println(Ognl.getValue("@test.ognl.Constant@get()", null));
            System.out.println(Ognl.getValue("@test.ognl.Constant@getString()", null));
        } catch (OgnlException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法的调用
     * 如果需要调用Root对象或者上下文对象当中的方法也可以使用.+方法的方式来调用。甚至可以传入参数。
     * 从代码可以看出来，赋值的时候可以选择上下文当中的元素进行给Root对象的username属性赋值。
     */
    @Test
    public void test3() {
        Address address = new Address("666666", "天涯路海角街");
        User user = new User("lucy", "123456", address);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("username", "yyy");
        context.put("password", "654321");
        try {
            System.out.println(Ognl.getValue("getUsername()", context, user));
            Ognl.getValue("setUsername(#username)", context, user);
            System.out.println(Ognl.getValue("getUsername()", context, user));
        } catch (OgnlException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对数组和集合的访问
     */
    @Test
    public void test4() {
        User user = new User();
        Map<String, Object> context = new HashMap<String, Object>();
        String[] strings = {"aa", "bb"};
        ArrayList<String> list = new ArrayList<String>();
        list.add("aa");
        list.add("bb");
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        context.put("list", list);
        context.put("strings", strings);
        context.put("map", map);
        try {
            System.out.println(Ognl.getValue("#strings[0]", context, user));
            System.out.println(Ognl.getValue("#list[0]", context, user));
            System.out.println(Ognl.getValue("#list[0+1]", context, user));
            System.out.println(Ognl.getValue("#map['key1']", context, user));
            System.out.println(Ognl.getValue("#map['key' + '2']", context, user));
        } catch (OgnlException e) {
            e.printStackTrace();
        }
    }

    /**
     * 投影与选择
     * 　　OGNL支持类似数据库当中的选择与投影功能。
     * 　　投影：选出集合当中的相同属性组合成一个新的集合。语法为collection.{XXX}，XXX就是集合中每个元素的公共属性。
     * 　　选择：选择就是选择出集合当中符合条件的元素组合成新的集合。语法为collection.{Y XXX}，其中Y是一个选择操作符，XXX是选择用的逻辑表达式。
     * 　　　　选择操作符有3种：
     * 　　　　　　?：选择满足条件的所有元素
     * 　　　　　　^：选择满足条件的第一个元素
     * 　　　　　　$：选择满足条件的最后一个元素
     */
    @Test
    public void test5() {
        Map<String, Object> context = new HashMap<String, Object>();
        ArrayList<Person> list = new ArrayList<Person>();
        for (int i = 1; i < 5; i++) {
            Person p = new Person();
            p.setId(i);
            p.setName("name" + i);
            list.add(p);
        }
        context.put("list", list);
        try {
            System.out.println(Ognl.getValue("#list.{id}", context, list));
            System.out.println(Ognl.getValue("#list.{id + '-' + name}", context, list));
            System.out.println(Ognl.getValue("#list.{? #this.id > 2}", context, list));
            System.out.println(Ognl.getValue("#list.{^ #this.id > 2}", context, list));
            System.out.println(Ognl.getValue("#list.{$ #this.id > 2}", context, list));
        } catch (OgnlException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建对象
     * 　　OGNL支持直接使用表达式来创建对象。主要有三种情况：
     * 　　构造List对象：使用{},中间使用','进行分割如{"aa", "bb", "cc"}
     * 　　构造Map对象：使用#{}，中间使用',进行分割键值对，键值对使用':'区分，如#{"key1" : "value1", "key2" : "value2"}
     * 　　构造任意对象：直接使用已知的对象的构造方法进行构造。
     */
    @Test
    public void test6() {
        try {
            Map<String, String> map = (Map<String, String>) Ognl.getValue("#{'key1':'value1'}", null);
            System.out.println(map);
            List<String> list = (List<String>) Ognl.getValue("{'key1','value1'}", null);
            System.out.println(list);
            Object object = Ognl.getValue("new java.lang.Object()", null);
            System.out.println(object);
        } catch (OgnlException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test7() {
        System.out.println("...test7...");
    }
}
