package org.tree.commons.support.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tree.commons.annotation.Comment;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author er_dong_chen
 * @date 2018/11/2
 *
 * <p>  由于要获取入参名, 所以必须使用 java 8 外加 -parameters 编译选项, 否则无法获取正确的入参名
 */
@RestController
public class DebugController {

    @Autowired
    private DebugConfig config;

    @RequestMapping("/debug.do")
    public String debug(ModelMap map) throws Exception {
        /* 运行时不需要模块名 */
        List<Class> classes = scanController();
        List<ClassInfo> values = getClassInfo(classes);
        File file = new ClassPathResource("org/tree/commons/support/html/debug.html").getFile();
        RandomAccessFile access = new RandomAccessFile(file, "r");
        String temp, jsonValues;
        StringBuilder sb = new StringBuilder();
        while (access.getFilePointer() < access.length()) {
            temp = access.readLine();
            if (temp.contains("${jsonValues}")) {
                /* 这句代码必须的, 否则乱码 */
                jsonValues = new String(JSON.toJSONString(values).getBytes("utf-8"), "iso-8859-1");
                temp = temp.replace("${jsonValues}", jsonValues);
            }
            sb.append(temp + "\n");
        }
        return new String(sb);
    }

    @RequestMapping("/debugValue.do")
    public List<ClassInfo> debugValue(ModelMap map) throws Exception {
        /* 运行时不需要模块名 */
        List<Class> classes = scanController();
        List<ClassInfo> values = getClassInfo(classes);
        return values;
    }

    /* 获取 controller 下的类 */
    public List<Class> scanController() throws Exception {
        String packageToScan = config.getPackageToScan();
        if (packageToScan == null)
            throw new Exception("请正确配置 DebugConfig !");
        File dir = new ClassPathResource(packageToScan.replace(".", "/")).getFile();
        String[] files = dir.list();
        List<String> list = new ArrayList<>(files.length);
        for (String str : files)
            list.add(packageToScan + "." + str.split("\\.")[0]);
        List<Class> result = new ArrayList<>(files.length);
        for (String str : list)
            result.add(Class.forName(str));
        return result;
    }

    /* 通过反射获取 url 和接口属性 */
    public static List<ClassInfo> getClassInfo(List<Class> classes) throws Exception {
        RequestMapping annotation;
        List<ClassInfo> classInfos = new ArrayList<>(classes.size());
        for (Class clazz : classes) {
            ClassInfo classInfo = new ClassInfo();
            String prefixUrl = _getUrl(clazz);
            if (prefixUrl.length() != 0) {
                classInfo.setRoot(prefixUrl);
                classInfo.setComment(_getComment(clazz));
            } else continue;
            Method[] methods = clazz.getMethods();
            List<MethodInfo> methodInfos = new ArrayList<>();
            for (Method method : methods) {
                MethodInfo methodInfo = new MethodInfo();
                List<ArgInfo> args = methodInfo.getArgs();
                String suffixUrl = _getUrl(method);
                if (suffixUrl.length() != 0) {
                    methodInfo.setMethod(suffixUrl);
                    methodInfo.setComment(_getComment(method));
                    Parameter[] parameters = method.getParameters();
                    for (Parameter parameter : parameters) {
                        String comment = null;
                        if (_testBaseType(parameter.getType())) {   // 是否基本类型
                            args.add(new ArgInfo(parameter.getName(), _getComment(parameter)));
                            continue;
                        }
                        Field[] MethodInfo = parameter.getType().getDeclaredFields();
                        for (Field field : MethodInfo) {
                            if (!field.getName().equals("id"))  //  忽略 id 字段
                                args.add(new ArgInfo(field.getName(), _getComment(field)));
                        }
                    }
                    methodInfos.add(methodInfo);
                }
            }
            classInfo.setMethods(methodInfos);
            classInfos.add(classInfo);
        }

        return classInfos;
    }

    private static String _getComment(AnnotatedElement clazz) {
        String comment = "";
        if (clazz.isAnnotationPresent(Comment.class))
            comment = clazz.getAnnotation(Comment.class).value();
        return comment;
    }

    private static String _getUrl(AnnotatedElement clazz) {
        String url = "";
        if (clazz.isAnnotationPresent(RequestMapping.class))
            url = clazz.getAnnotation(RequestMapping.class).value()[0];
        return url;
    }

    private static boolean _testBaseType(Class param) {
        return param == String.class || param == Integer.class || param == Long.class || param == Byte.class || param == Character.class || param == Boolean.class;
    }

    public static class ClassInfo {
        String root;
        String comment;
        List<MethodInfo> methods;

        public String getRoot() {
            return root;
        }

        public void setRoot(String root) {
            this.root = root;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public List<MethodInfo> getMethods() {
            return methods;
        }

        public void setMethods(List<MethodInfo> methods) {
            this.methods = methods;
        }

        @Override
        public String toString() {
            return "ClassInfo{" +
                    "root='" + root + '\'' +
                    ", comment='" + comment + '\'' +
                    ", methods=" + methods +
                    '}';
        }
    }

    private static class MethodInfo {
        String method;
        String comment;
        List<ArgInfo> args = new ArrayList<>();

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public List<ArgInfo> getArgs() {
            return args;
        }

        public void setArgs(List<ArgInfo> args) {
            this.args = args;
        }

        @Override
        public String toString() {
            return "MethodInfo{" +
                    "method='" + method + '\'' +
                    ", comment='" + comment + '\'' +
                    ", args=" + args +
                    '}';
        }
    }

    private static class ArgInfo {
        private String arg;
        private String comment = "";

        public ArgInfo(String arg, String comment) {
            this.arg = arg;
            this.comment = comment;
        }

        public String getArg() {
            return arg;
        }

        public void setArg(String arg) {
            this.arg = arg;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        @Override
        public String toString() {
            return "ArgInfo{" +
                    "arg='" + arg + '\'' +
                    ", comment='" + comment + '\'' +
                    '}';
        }
    }
}
