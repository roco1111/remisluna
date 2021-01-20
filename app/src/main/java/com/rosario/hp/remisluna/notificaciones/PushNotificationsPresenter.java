package com.rosario.hp.remisluna.notificaciones;


import com.google.firebase.messaging.FirebaseMessaging;


import java.util.ArrayList;

/**
 * Presentador de las notificaciones
 */
public class PushNotificationsPresenter implements PushNotificationContract.Presenter {
    private final PushNotificationContract.View mNotificationView;
    private final FirebaseMessaging mFCMInteractor;

    public PushNotificationsPresenter(PushNotificationContract.View notificationView,
                                      FirebaseMessaging FCMInteractor) {
        mNotificationView = notificationView;
        mFCMInteractor = FCMInteractor;

        notificationView.setPresenter(this);
    }


    @Override
    public void start() {
        registerAppClient();
        loadNotifications();
    }

    @Override
    public void registerAppClient() {
        mFCMInteractor.subscribeToTopic("promos");
    }

    @Override
    public void loadNotifications() {
        PushNotificationsRepository.getInstance().getPushNotifications(
                new PushNotificationsRepository.LoadCallback() {
                    @Override
                    public void onLoaded(ArrayList<PushNotification> notifications) {
                        if (notifications.size() > 0) {
                            mNotificationView.showEmptyState(false);
                            mNotificationView.showNotifications(notifications);
                        } else {
                            mNotificationView.showEmptyState(true);
                        }
                    }
                }
        );
    }

    @Override
    public void savePushMessage(String title, String description, String id_pedido, String fecha) {
        PushNotification pushMessage = new PushNotification();
        pushMessage.setTitle(title);
        pushMessage.setDescription(description);
        pushMessage.setId_pedido(id_pedido);
        pushMessage.setFecha(fecha);


        PushNotificationsRepository.getInstance().savePushNotification(pushMessage);

        mNotificationView.showEmptyState(false);
        mNotificationView.popPushNotification(pushMessage);
    }


}
