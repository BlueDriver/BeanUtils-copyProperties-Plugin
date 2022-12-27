package com.github.bd.b.c.common;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import com.intellij.psi.javadoc.PsiDocComment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BeanUtils.copyProperties
 * com.github.bd.b.c.common
 *
 * @author BlueDriver
 * @email cpwu@foxmail.com
 * @date 2022/09/05 14:04 Monday
 */
public class PsiUtils {

    /**
     * 文档注释转单行文本
     * 多行之间以空格分开
     */
    public static String getSingleLineDoc(@NotNull PsiDocComment docComment) {
        return getSingleLineDoc(docComment.getText());
    }

    private static String getSingleLineDoc(@Nullable String docComment) {
        if (docComment == null) {
            return "";
        }
        return docComment
                .replaceAll("/", "")
                .replaceAll("\\*", "")
                // .replaceAll(" ", "")
                .replaceAll("\\n", " ").trim();
    }

    /**
     * 根据PsiType解析PsiClass
     *
     * @param type PsiType
     * @return PsiClass
     */
    @Nullable
    public static PsiClass getPsiClass(@Nullable PsiType type) {
        if (type instanceof PsiClassType) {
            return ((PsiClassType) type).resolve();
        }
        return null;
    }
}
