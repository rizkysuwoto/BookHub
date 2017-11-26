package twotom.bookhub;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class BookHubFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent();
        intent.setAction("twotom.bookhub.CHAT_RECEIVED_MESSAGE");
        Map<String, String> data = remoteMessage.getData();
        intent.putExtra("sender", data.get("sender"));
        intent.putExtra("text", data.get("text"));
        sendBroadcast(intent);
    }
}