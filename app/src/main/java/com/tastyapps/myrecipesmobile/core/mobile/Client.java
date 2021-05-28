package com.tastyapps.myrecipesmobile.core.mobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.tastyapps.myrecipesmobile.core.events.OnClientConnectedEventListener;
import com.tastyapps.myrecipesmobile.core.events.OnClientDestroyedEventListener;
import com.tastyapps.myrecipesmobile.core.events.OnClientDisconnectedEventListener;
import com.tastyapps.myrecipesmobile.core.events.OnTopicReceivedEventListener;
import com.tastyapps.myrecipesmobile.core.recipes.Ingredient;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.core.recipes.RecipeIngredient;
import com.tastyapps.myrecipesmobile.core.util.EnumUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EventListener;

public class Client implements MqttCallback {
    private static final Client instance = new Client();

    private String addressCurrent;
    private String usernameCurrent;
    private String passwordCurrent;

    private OnClientConnectedEventListener onClientConnectedEventListener;
    private OnClientDisconnectedEventListener onClientDisconnectedEventListener;
    private OnTopicReceivedEventListener onTopicReceivedEventListener;
    private OnClientDestroyedEventListener onClientDestroyedEventListener;

    private MqttAndroidClient client;
    private boolean connected;

    public boolean isUploadingImage;
    public String tempImageFilePath;
    public String recipeGuid;
    public Bitmap selectedImage;
    public byte[] imageBytes;

    private Client() {
        onClientConnectedEventListener = null;
        onClientDisconnectedEventListener = null;
        onClientDestroyedEventListener = null;
    }

    public static Client getInstance() {
        return instance;
    }

    public void setOnClientConnectedEventListener(OnClientConnectedEventListener listener) {
        this.onClientConnectedEventListener = listener;
    }

    public void setOnClientDisconnectedEventListener(OnClientDisconnectedEventListener listener) {
        this.onClientDisconnectedEventListener = listener;
    }

    public void setOnTopicReceivedEventListener(OnTopicReceivedEventListener onTopicReceivedEventListener) {
        this.onTopicReceivedEventListener = onTopicReceivedEventListener;
    }

    public void setOnClientDestroyedEventListener(OnClientDestroyedEventListener onClientDestroyedEventListener) {
        this.onClientDestroyedEventListener = onClientDestroyedEventListener;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getClientId() {
        return client != null ? client.getClientId() : "";
    }

    public void setConnectionData(String address, String username, String password) {

        addressCurrent = address;
        usernameCurrent = username;
        passwordCurrent = password;
    }

    public void connect(Context appContext, boolean isReconnect) {
        if (connected && !isReconnect) {
            return;
        }

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(appContext, "tcp://" + addressCurrent + ":1883", clientId);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        if (!TextUtils.isEmpty(addressCurrent)) {
            mqttConnectOptions.setUserName(addressCurrent);
        }

        if (!TextUtils.isEmpty(addressCurrent)) {
            mqttConnectOptions.setPassword(addressCurrent.toCharArray());
        }
        mqttConnectOptions.setConnectionTimeout(5);

        try{
            client.setCallback(this);

            client.connect(mqttConnectOptions, appContext, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    connected = false;
                    if (onClientConnectedEventListener != null) {
                        Log.d("Client", "Connection established");
                        onClientConnectedEventListener.onConnected();
                        connected = true;
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (onClientConnectedEventListener != null) {
                        Log.d("Client", "Connection failed");
                        onClientConnectedEventListener.onFail(exception);
                    }
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void reconnect() {}

    public void disconnect() {
        if (!connected) {
            return;
        }

        try {
            //addressCurrent = null;
            //usernameCurrent = null;
            //passwordCurrent = null;

            if (client == null) {
                fireOnClientDestroyed();
                return;
            }

            client.disconnect().setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    connected = false;
                    if (onClientDisconnectedEventListener != null) {
                        onClientDisconnectedEventListener.onDisconnected();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (onClientDisconnectedEventListener != null) {
                        onClientDisconnectedEventListener.onFail(exception);
                    }
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendImage(String topic, byte[] imageBytes) {
        if (client == null) {
            return;
        }

        try {
            boolean isConnecting = false;

            if (!client.isConnected()) {
                isConnecting = true;
                client.connect();
            }

            if (isConnecting) {
                //this.imageBytes = imageBytes;
                Log.d("MQTT - SendImage", "Publishing image, but waiting for connection...");
                OnClientConnectedEventListener originalHandler = Client.getInstance().onClientConnectedEventListener;
                setOnClientConnectedEventListener(new OnClientConnectedEventListener() {
                    @Override
                    public void onConnected() {
                        Log.d("MQTT - SendImage", "Successfully published image to topic \"" + topic + "\"!");
                        publishImage(topic, imageBytes);
                        setOnClientConnectedEventListener(originalHandler);
                    }

                    @Override
                    public void onFail(Throwable ex) {
                        Log.d("MQTT - SendImage", "Connection to client failed while trying to publish image to topic \"" + topic + "\"!");
                        setOnClientConnectedEventListener(originalHandler);
                    }
                });
            } else {
                Log.d("MQTT - SendImage", "Publishing image...");
                publishImage(topic, imageBytes);
            }
        } catch (MqttException e) {
            Log.d("MQTT - SendImage", "An exception has been thrown while trying to publish image to topic \"" + topic + "\"!");
            e.printStackTrace();
        }
    }

    private void publishImage(String topic, byte[] imageBytes) {
        try {
            client.publish(topic, imageBytes, 0, false, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT - SendImage", "Successfully published image to topic \"" + topic + "\"!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MQTT - SendImage", "Error publishing image to topic \"" + topic + "\"!");
                }
            });
        } catch (MqttException e) {
            Log.d("MQTT - SendImage", "An exception has been thrown while trying to publish image to topic \"" + topic + "\"!");
            e.printStackTrace();
        }
    }

    public void sendMessage(String topic, String payload) {
        MqttMessage message = new MqttMessage();
        if (payload != null) {
            message.setPayload(payload.getBytes());
            Log.d("MQTT - SendMessage", "Payload: " + payload);
        }

        sendMessage(topic, message);
    }

    private void sendMessage(String topic, MqttMessage message) {
        if (client == null) {
            return;
        }

        try {
            if (!client.isConnected()) {
                client.connect();
            }

            client.publish(topic, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT - SendMessage", "Successfully published message to topic \"" + topic + "\"!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MQTT - SendMessage", "Error publishing message to topic \"" + topic + "\"!");
                }
            });
        } catch (MqttException e) {
            Log.d("MQTT - SendMessage", "An exception has been thrown while trying to publish message to topic \"" + topic + "\"!");
            e.printStackTrace();
        }
    }

    public boolean subscribeTopic(String topic) {
        if (client == null) {
            return false;
        }

        try {
            client.subscribe(getClientId() + "/" + topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT - Subscribe", "Received message from topic \"" + topic + "\"!");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MQTT - Subscribe", "Error subscribing to topic \"" + topic + "\"!");
                }
            }, this::messageArrived);
        } catch (MqttException e) {
            Log.d("MQTT - Subscribe", "An exception has been thrown while trying to subscribe to topic \"" + topic + "\"!");
            e.printStackTrace();
        }

        return true;
    }

    public void unsubscribeTopic(String topic) {
        if (client == null) {
            return;
        }

        try {
            client.unsubscribe(topic);
            Log.d("MQTT - Unsubscribe", "Successfully unsubscribed from topic \"" + topic + "\".");
        } catch (MqttException e) {
            Log.d("MQTT - Unsubscribe", "An exception has been thrown while trying to unsubscribe from topic \"" + topic + "\"!");
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d("MQTT - Received", "Message received from topic \"" + topic + "\".");
        byte[] payloadBytes = message.getPayload();
        String payload = null;
        if (!topic.startsWith(getClientId() + "/recipes/img/")){
            if (payloadBytes != null && payloadBytes.length > 0) {
                payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                Log.d("MQTT - Received", "Message payload: " + payload);
            } else {
                Log.d("MQTT - Received", "No payload attached!");
            }
        } else {
            Log.d("MQTT - Received", "Payload has image data");
        }

        if (topic.equals(getClientId() + "/categories")) {

        } else if (topic.equals(getClientId() + "/ingredients")) {

        } else if (topic.equals(getClientId() + "/recipes/clear")) {
            //Start of recipe list transfer
            onTopicReceivedEventListener.onClearRecipes();
        } else if (topic.equals(getClientId() + "/recipes")) {
            Recipe recipe = Recipe.fromJson(payload);
            for (RecipeIngredient recipeIngredient : recipe.Ingredients) {
                recipeIngredient.MeasurementTypeReal = EnumUtils.castIntToMeasurementType(recipeIngredient.MeasurementType);

                Ingredient ingredient = recipeIngredient.Ingredient;
                ingredient.IngredientCategoryReal = EnumUtils.castIntToIngredientCategory(ingredient.IngredientCategory);
                ingredient.MeasurementTypeReal = EnumUtils.castIntToMeasurementType(ingredient.MeasurementType);
            }
            onTopicReceivedEventListener.onRecipeReceived(recipe);
        } else if (topic.startsWith(getClientId() + "/recipes/img/")) {
            String guid = topic.replace(getClientId() + "/recipes/img/", "");
            onTopicReceivedEventListener.onRecipeImageReceived(payloadBytes, guid);
        } else if (topic.equals(getClientId() + "/season")) {

        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    protected void fireOnClientDestroyed() {
        if (onClientDestroyedEventListener != null) {
            onClientDestroyedEventListener.onDestroyed();
        }
    }
}
