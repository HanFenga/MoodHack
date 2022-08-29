package dev.hanfeng.zhebushigudu.xufangggg.notification;

import java.util.ArrayList;

public class NotificationsManager {
    public ArrayList<Notification> notifications = new ArrayList<>();

    public void add(Notification noti) {
        noti.y = notifications.size() * 25;
        notifications.add(noti);

    }

    public void draw() {
        Notification remove = null;
        for (Notification notification : notifications) {
//            if (i == 0) {
            if (notification.x == 0) {
                notification.in = !notification.in;
            }
            if (Math.abs(notification.x - notification.width) < 0.1 && !notification.in) {
                remove = notification;
            }
            if (notification.in) {
                notification.x = notification.animationUtils.animate(0, notification.x, 0.1f);
            } else {
                notification.x = notification.animationUtils.animate(notification.width, notification.x, 0.1f);
            }
//            }
            notification.onRender();
        }
        if (remove != null) {
            notifications.remove(remove);
        }
    }
}
