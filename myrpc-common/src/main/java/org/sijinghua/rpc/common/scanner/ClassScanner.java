package org.sijinghua.rpc.common.scanner;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @description  通用扫描器
 */
public class ClassScanner {
    /**
     * 文件
     */
    private static final String PROTOCOL_FILE = "file";

    /**
     * jar包
     */
    private static final String PROTOCOL_JAR = "jar";

    /**
     * class文件的后缀
     */
    private static final String CLASS_FILE_SUFFIX = ".class";

    /**
     * 扫描指定包下的所有类信息
     * @param packageName 指定的包名
     * @return 指定包下所有完整类名的List集合
     * @throws Exception
     */
    public static List<String> getClassNameList(String packageName) throws Exception {
        // 第一个class类的集合
        List<String> classNameList = new ArrayList<>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字，并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合，用来处理这个目录下的things
        Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        // 循环迭代
        while (dirs.hasMoreElements()) {
            // 获取下一个元素
            URL url = dirs.nextElement();
            // 得到协议名称
            String protocol = url.getProtocol();
            // 如果是文件形式保存在服务器上
            if (PROTOCOL_FILE.equals(protocol)) {
                // 获取包的物理路径
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                // 以文件的方式扫描整个包下的文件，并添加到集合中
                findAndAddClassesInPackageByFile(packageName, filePath, recursive, classNameList);
            } else if (PROTOCOL_JAR.equals(protocol)) {
                packageName = findAndAddClassesInPackageByJar(packageName, packageDirName, recursive, classNameList, url);
            }
        }
        return classNameList;
    }

    /**
     * 扫描当前工程中，指定包下的所有类信息
     * @param packageName 扫描的包名
     * @param packagePath 扫描的包的完整路径
     * @param recursive 是否递归调用
     * @param classNameList 类名称的合集
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
                                                         final boolean recursive, List<String> classNameList) {
        // 获取此包的目录，建立一个File
        File dir = new File(packagePath);

        // 如果不存在或者该路径不是目录，就返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        // 如果存在，就获取包下所有文件，包括目录
        File[] dirFiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则：可以循环（包含子目录），或.class结尾的文件
            @Override
            public boolean accept(File pathname) {
                return (recursive && pathname.isDirectory()) || pathname.getName().endsWith(".class");
            }
        });

        // 如果包下没有文件，则返回
        if (dirFiles == null) {
            return;
        }

        // 循环所有文件
        for (File file: dirFiles) {
            // 如果是目录，则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(), recursive, classNameList);
            } else {
                // 如果是java类文件，去掉".class"，只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                // 添加到集合中
                classNameList.add(packageName + "." + className);
            }
        }
    }

    /**
     * 扫描Jar文件中指定包下所有类信息
     * @param packageName 扫描的包名
     * @param packageDirName 当前包名前面的部分的名称
     * @param recursive 是否递归调用
     * @param classNameList 完成类名存放否List集合
     * @param url 包的url地址
     * @return 处理后的包名，方便下次调用使用
     * @throws IOException io
     */
    private static String findAndAddClassesInPackageByJar(String packageName, String packageDirName, boolean recursive,
                                                          List<String> classNameList, URL url) throws IOException {
        // 如果是jar包文件，定义一个JarFile
        JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
        // 用此jar包得到一个枚举类
        Enumeration<JarEntry> entries = jar.entries();
        //进行同样的循环迭代
        while (entries.hasMoreElements()) {
            // 获取jar包里的一个实体，可以是目录，也可以是一些其他文件，如META-INF等
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            // 如果以"/"开头，获取后面的字符串
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            // 如果前半部分和定义的包名相同
            if (name.startsWith(packageDirName)) {
                String nowPackageName = packageName;
                int idx = name.lastIndexOf('/');
                // 如果以'/'结尾，则是一个包
                if (idx != -1) {
                    // 获取包名，把"/"替换成"."
                    nowPackageName = name.substring(0, idx).replace('/', '.');
                }
                // 如果可以迭代下去，且是一个包
                if (recursive && (idx != -1)) {
                    // 如果是一个class文件，且不是目录
                    if (name.endsWith(CLASS_FILE_SUFFIX) && !entry.isDirectory()) {
                        // 去掉后面的".class"，获取真正的类名
                        String className = name.substring(nowPackageName.length() + 1, name.length() - CLASS_FILE_SUFFIX.length());
                        classNameList.add(packageName + "." + className);
                    }
                }
            }
        }
        return packageName;
    }
}
