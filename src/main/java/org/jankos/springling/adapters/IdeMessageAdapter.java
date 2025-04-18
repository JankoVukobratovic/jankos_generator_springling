package org.jankos.springling.adapters;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class IdeMessageAdapter {
    private static final String groupId = "Springling Plugin";

    public static void showErrorNotification(String message, Project project) {
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup(groupId)
                .createNotification(message, NotificationType.ERROR);
        notification.notify(project);
    }

    public static void showWarningNotification(String message, Project project) {
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup(groupId)
                .createNotification(message, NotificationType.WARNING);
        notification.notify(project);
    }

    public static void showInfoNotification(String message, Project project) {
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup(groupId)
                .createNotification(message, NotificationType.INFORMATION);
        notification.notify(project);
    }
}
