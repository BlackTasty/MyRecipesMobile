package com.tastyapps.myrecipesmobile.core.mobile;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tastyapps.myrecipesmobile.core.events.OnClientConnectedEventListener;
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

public class Client implements MqttCallback {
    private static final Client instance = new Client();

    private String addressCurrent;
    private String usernameCurrent;
    private String passwordCurrent;

    private OnClientConnectedEventListener onClientConnectedEventListener;
    private OnClientDisconnectedEventListener onClientDisconnectedEventListener;
    private OnTopicReceivedEventListener onTopicReceivedEventListener;

    private MqttAndroidClient client;
    private boolean connected;

    private Client() {
        onClientConnectedEventListener = null;
        onClientDisconnectedEventListener = null;
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

    public boolean isConnected() {
        return connected;
    }

    public String getClientId() {
        return client.getClientId();
    }

    public void connect(Context appContext, String address, String username, String password) {
        if (connected) {
            return;
        }

        addressCurrent = address;
        usernameCurrent = username;
        passwordCurrent = password;

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(appContext, "tcp://" + address + ":1883", clientId);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        if (!TextUtils.isEmpty(username)) {
            mqttConnectOptions.setUserName(username);
        }

        if (!TextUtils.isEmpty(password)) {
            mqttConnectOptions.setPassword(password.toCharArray());
        }

        try{
            client.setCallback(this);

            client.connect(mqttConnectOptions, appContext, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    connected = false;
                    if (onClientConnectedEventListener != null) {
                        onClientConnectedEventListener.onConnected();
                        connected = true;
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (onClientConnectedEventListener != null) {
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
            addressCurrent = null;
            usernameCurrent = null;
            passwordCurrent = null;

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

    public void sendMessage(String topic, String payload) {
        try {
            if (!client.isConnected()) {
                client.connect();
            }

            MqttMessage message = new MqttMessage();
            if (payload != null) {
                message.setPayload(payload.getBytes());
            }

            client.publish(topic, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT - Send", "Successfully published message to topic \"" + topic + "\"!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MQTT - Send", "Error publishing message to topic \"" + topic + "\"!");
                }
            });
        } catch (MqttException e) {
            Log.d("MQTT - Send", "An exception has been thrown while trying to publish message to topic \"" + topic + "\"!");
            e.printStackTrace();
        }
    }

    public void subscribeTopic(String topic) {
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
    }

    public void unsubscribeTopic(String topic) {
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
}
