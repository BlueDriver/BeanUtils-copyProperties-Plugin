package com.github.bd.b.c.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BeanUtils.copyProperties
 * com.github.bd.b.c.common
 *
 * @author BlueDriver
 * @email cpwu@foxmail.com
 * @date 2022/09/05 15:15 Monday
 */
public class VarNameUtils {
    /**
     * 将字符串首字母转成大写
     *
     * @param name String
     * @return 首字母大写的字符串
     */
    @NotNull
    public static String upperFirstChar(@Nullable String name) {
        if (name == null || name.length() == 0) {
            return "";
        }
        // 2位及以上
        if (name.length() >= 2) {
            if (Character.isUpperCase(name.charAt(0))) {
                return name;
            }
            return (new StringBuilder()).append(Character.toUpperCase(name.charAt(0))).append(name.substring(1)).toString();
        }
        // 单个字符
        return name.toUpperCase();

    }
}
