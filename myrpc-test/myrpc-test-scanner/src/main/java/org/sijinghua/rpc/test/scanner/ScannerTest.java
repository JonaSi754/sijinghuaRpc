package org.sijinghua.rpc.test.scanner;

import org.junit.Test;
import org.sijinghua.rpc.common.scanner.ClassScanner;
import org.sijinghua.rpc.common.scanner.reference.RpcReferenceScanner;
import org.sijinghua.rpc.common.scanner.server.RpcServiceScanner;

import java.util.List;

public class ScannerTest {
    /**
     * 扫描org.sijinghua.rpc.test包下所有的类
     */
    @Test
    public void testScannerClassNameList() throws Exception {
        List<String> classNameList = ClassScanner.getClassNameList("org.sijinghua.rpc.test");
        classNameList.forEach(System.out::println);
    }

    /**
     * 扫描org.sijinghua.rpc.test包下所有标注了@RpcService的类
     */
    @Test
    public void testScannerClassNameListByRpcService() throws Exception {
        RpcServiceScanner.doScannerWithRpcServiceAnnotationAndRegistryService("org.sijinghua.rpc.test");
    }

    /**
     * 扫描org.sijinghua.rpc.test.scanner包下所有标注了@RpcReference的类
     */
    @Test
    public void testScannerClassNameListByRpcReference() throws Exception {
        RpcReferenceScanner.doScannerWithRpcReferenceAnnotationFilter("org.sijinghua.rpc.test.scanner");
    }
}
