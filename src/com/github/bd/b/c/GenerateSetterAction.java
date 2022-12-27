package com.github.bd.b.c;

import com.github.bd.b.c.common.ActionUtils;
import com.github.bd.b.c.common.Constant;
import com.github.bd.b.c.common.NotifyUtils;
import com.github.bd.b.c.common.PsiUtils;
import com.github.bd.b.c.common.VarNameUtils;
import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.tree.java.PsiIdentifierImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * BeanUtils.copyProperties
 * com.github.bd.b.c
 *
 * @author BlueDriver
 * @email cpwu@foxmail.com
 * @since 2022/09/05 13:55 Monday
 */
public class GenerateSetterAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        assert editor != null;

        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        assert psiFile != null;

        int offset = editor.getCaretModel().getOffset();
        PsiElement ele = psiFile.findElementAt(offset);

        // 必须是标识符
        if (!(ele instanceof PsiIdentifier)) {
            return;
        }

        final Project project = ele.getProject();

        // the parent element of cursor
        PsiElement eleParent = ele.getParent();


        PsiType type;
        if (eleParent instanceof PsiLocalVariable) {
            type = ((PsiLocalVariable) eleParent).getType();
        } else if (eleParent instanceof PsiReferenceExpression) {
            type = ((PsiReferenceExpression) eleParent).getType();
        } else if (eleParent instanceof PsiParameter) {
            type = ((PsiParameter) eleParent).getType();
        } else {
            NotifyUtils.warning(project, "Unexpected element type: " + eleParent.getClass());
            return;
        }
        // the Class of
        PsiClass clazz = PsiUtils.getPsiClass(type);
        if (clazz == null) {
            NotifyUtils.warning(project, "Cannot get class type");
            return;
        }


        PsiFieldMember[] fieldMembers = this.getFieldsMember(clazz);
        if (fieldMembers.length < 1) {
            NotifyUtils.warning(project, "No Field in " + clazz.getName());
            return;
        }

        // the previous code statement of setter code that to insert
        PsiElement statement = this.getStatement(ele);
        if (statement == null) {
            NotifyUtils.warning(project, "Cannot locate where to insert code");
            return;
        }

        MemberChooser<PsiFieldMember> chooser = new MemberChooser<>(fieldMembers, false,
                true, project);

        chooser.setTitle("Choose field(s) for copy value");

        // show Copy Javadoc checkbox
        chooser.setCopyJavadocVisible(true);

        TransactionGuard.getInstance().submitTransactionAndWait(chooser::show);
        if (chooser.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
            // exit or cancel by user
            return;
        }

        List<PsiFieldMember> selectedElements = chooser.getSelectedElements();
        if (selectedElements == null || selectedElements.isEmpty()) {
            // choose none
            return;
        }

        String sourceObjectName = Messages.showInputDialog(
                "Enter the variable name of source object.\n(If nothing is entered, null will be used)",
                Constant.PLUGIN_NAME, Messages.getQuestionIcon());


        String targetEleName = ele.getText().trim();


        // region insert code string
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        String space8 = "        ";
        for (PsiFieldMember selectedElement : selectedElements) {
            PsiField field = selectedElement.getElement();

            // append javadoc
            if (chooser.isCopyJavadoc() && field.getDocComment() != null) {
                sb.append(space8).append("//").append(" ").append(PsiUtils.getSingleLineDoc(field.getDocComment())).append("\n");
            }

            // setter code example : target.setXxx(source.getXxxx());
            sb.append(space8).append(targetEleName)
                    .append(".").append("set").append(VarNameUtils.upperFirstChar(field.getName())).append("(");
            if (sourceObjectName == null || sourceObjectName.trim().length() <= 0) {
                sb.append("null");
            } else {
                sb.append(sourceObjectName)
                        .append(".").append("get").append(VarNameUtils.upperFirstChar(field.getName()))
                        .append("(").append(")");
            }
            sb.append(")").append(";").append("\n");
        }

        Document document = FileDocumentManager.getInstance().getDocument(psiFile.getVirtualFile());
        assert document != null;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.insertString(statement.getTextOffset() + 1,
                    sb.toString());
        });

        // endregion


    }


    @NotNull
    private PsiFieldMember[] getFieldsMember(@NotNull PsiClass clazz) {
        List<PsiFieldMember> list = new ArrayList<>(clazz.getAllFields().length);
        for (PsiField field : clazz.getAllFields()) {
            list.add(new PsiFieldMember(field));
        }
        return list.toArray(new PsiFieldMember[0]);
    }


    @Nullable
    private PsiElement getStatement(@Nullable PsiElement element) {
        if (element == null) {
            return null;
        }
        PsiElement eleParent = element.getParent();

        if (eleParent instanceof PsiLocalVariable) {
            return eleParent.getLastChild();
        } else if (eleParent instanceof PsiParameter) {
            if (eleParent.getParent() instanceof PsiParameterList
                    && eleParent.getParent().getParent() instanceof PsiMethod) {
                PsiMethod method = (PsiMethod) eleParent.getParent().getParent();
                return method.getBody();
            }
            return null;
        } else if (eleParent instanceof PsiReferenceExpression) {
            if (eleParent.getParent().getParent().getParent() instanceof PsiExpressionStatement) {
                return eleParent.getParent().getParent().getParent().getLastChild();
            }
            return null;
        } else if (element instanceof PsiStatement) {
            return element;
        }

        return this.getStatement(element.getParent());
    }


    @Override
    public void update(AnActionEvent e) {
        PsiClass currentClass = ActionUtils.getClass(e);
        if (currentClass == null) {
            this.disableAndHide(e);
            return;
        }

        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        if (psiFile == null) {
            this.disableAndHide(e);
            return;
        }

        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) {
            this.disableAndHide(e);
            return;
        }

        int offset = editor.getCaretModel().getOffset();

        PsiElement ele = psiFile.findElementAt(offset);

        if (!(ele instanceof PsiIdentifierImpl)) {
            this.disableAndHide(e);
        }
    }

    /**
     * 禁用和隐藏
     *
     * @param e AnActionEvent
     */
    private void disableAndHide(AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(false);
    }
}
