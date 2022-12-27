package com.github.bd.b.c.common;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BeanUtils.copyProperties
 * com.github.bd.b.c.common
 *
 * @author BlueDriver
 * @email cpwu@foxmail.com
 * @date 2022/09/05 14:00 Monday
 */
public class NotifyUtils {
    public static void error(@Nullable Project project, @NotNull String content) {
        notifyMsg(project, content, NotificationType.ERROR);
    }

    public static void warning(@Nullable Project project, @NotNull String content) {
        notifyMsg(project, content, NotificationType.WARNING);
    }

    public static void info(@Nullable Project project, @NotNull String content) {
        notifyMsg(project, content, NotificationType.INFORMATION);
    }

    /**
     * 右下角消息通知
     *
     * @param project 没有这个的话，仅仅是通知栏有文字，无Balloon
     */
    private static void notifyMsg(@Nullable Project project, @NotNull String content, @NotNull NotificationType type) {
        Notification notification = new Notification(
                Constant.PLUGIN_NAME,
                Constant.PLUGIN_NAME,
                content,
                type);
        Notifications.Bus.notify(notification, project);
    }
}
