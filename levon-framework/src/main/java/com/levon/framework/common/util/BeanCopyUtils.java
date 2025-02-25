package com.levon.framework.common.util;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    // 私有化构造函数，防止实例化
    private BeanCopyUtils() {
    }

    /**
     * 拷贝单个Bean
     * @param source 源对象
     * @param clazz 目标对象类型
     * @param <V> 目标对象类型
     * @return 拷贝后的目标对象
     */
    public static <V> V copyBean(Object source, Class<V> clazz) {
        // 创建目标对象
        V result = null;
        try {
            // 使用反射创建对象
            result = clazz.getDeclaredConstructor().newInstance();
            // 使用 Spring BeanUtils 拷贝属性
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            System.err.println("Bean copy error: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 拷贝 Bean 列表
     * @param list 源对象列表
     * @param clazz 目标对象类型
     * @param <O> 源对象类型
     * @param <V> 目标对象类型
     * @return 拷贝后的目标对象列表
     */
    public static <O, V> List<V> copyBeanList(List<O> list, Class<V> clazz) {
        // 使用并行流提高大数据量时的性能
        return list.parallelStream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }
}