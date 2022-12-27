package com.github.bd.b.c.common;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BeanUtils.copyProperties
 * com.github.bd.b.c.common
 *
 * @author BlueDriver
 * @email cpwu@foxmail.com
 * @date 2022/09/05 14:14 Monday
 */
public class ActionUtils {
    /**
     * 获取当前类
     * can both from editor and projectViewer
     */
    @Nullable
    public static PsiClass getClass(@NotNull AnActionEvent event) {
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        return psiFile == null ? null : PsiTreeUtil.findChildOfAnyType(psiFile.getOriginalElement(),
                PsiClass.class);
    }
}
