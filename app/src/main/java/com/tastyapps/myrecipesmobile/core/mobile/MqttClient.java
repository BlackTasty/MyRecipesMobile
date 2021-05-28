package com.tastyapps.myrecipesmobile.core.mobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.tastyapps.myrecipesmobile.core.events.OnClientConnectedEventListener;
import com.tastyapps.myrecipesmobile.core.events.OnClientDestroyedEventListener;
import com.tastyapps.myrecipesmobile.core.events.OnClientDisconnectedEventListener;
import com.tastyapps.myrecipesmobile.core.events.OnTopicReceivedEventListener;
import com.tastyapps.myrecipesmobile.core.recipes.Category;
import com.tastyapps.myrecipesmobile.core.recipes.Ingredient;
import com.tastyapps.myrecipesmobile.core.recipes.Recipe;
import com.tastyapps.myrecipesmobile.core.recipes.RecipeImage;
import com.tastyapps.myrecipesmobile.core.recipes.RecipeIngredient;
import com.tastyapps.myrecipesmobile.core.util.EnumUtils;
import com.tastyapps.myrecipesmobile.core.util.HashUtils;
import com.tastyapps.myrecipesmobile.storage.CategoryStorage;
import com.tastyapps.myrecipesmobile.storage.IngredientStorage;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MqttClient implements MqttCallback {
    private static final MqttClient instance = new MqttClient();

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

    private List<String> subscribedTopics = new ArrayList<>();

    private MqttClient() {
        onClientConnectedEventListener = null;
        onClientDisconnectedEventListener = null;
        onClientDestroyedEventListener = null;
    }

    public static MqttClient getInstance() {
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
        passwordCurrent = HashUtils.hashSHA256(password, username);
    }

    public void connect(Context appContext, boolean isReconnect) {
        if (connected && !isReconnect) {
            return;
        }

        String clientId = org.eclipse.paho.client.mqttv3.MqttClient.generateClientId();
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
                        Log.d("MqttClient", "Connection established");
                        onClientConnectedEventListener.onConnected();
                        connected = true;
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (onClientConnectedEventListener != null) {
                        Log.d("MqttClient", "Connection failed");
                        onClientConnectedEventListener.onFail(exception);
                        if (exception != null) {
                            exception.printStackTrace();
                        }
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
                Log.d("MqttClient", "Publishing image, but waiting for connection...");
                OnClientConnectedEventListener originalHandler = MqttClient.getInstance().onClientConnectedEventListener;
                setOnClientConnectedEventListener(new OnClientConnectedEventListener() {
                    @Override
                    public void onConnected() {
                        Log.d("MqttClient", "Successfully published image to topic \"" + topic + "\"!");
                        publishImage(topic, imageBytes);
                        setOnClientConnectedEventListener(originalHandler);
                    }

                    @Override
                    public void onFail(Throwable ex) {
                        Log.d("MqttClient", "Connection to client failed while trying to publish image to topic \"" + topic + "\"!");
                        setOnClientConnectedEventListener(originalHandler);
                    }
                });
            } else {
                Log.d("MqttClient", "Publishing image...");
                publishImage(topic, imageBytes);
            }
        } catch (MqttException e) {
            Log.d("MqttClient", "An exception has been thrown while trying to publish image to topic \"" + topic + "\"!");
            e.printStackTrace();
        }
    }

    private void publishImage(String topic, byte[] imageBytes) {
        try {
            client.publish(topic, imageBytes, 0, false, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MqttClient", "Successfully published image to topic \"" + topic + "\"!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MqttClient", "Error publishing image to topic \"" + topic + "\"!");
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            });
        } catch (MqttException e) {
            Log.d("MqttClient", "An exception has been thrown while trying to publish image to topic \"" + topic + "\"!");
            e.printStackTrace();
        }
    }

    public void sendMessage(String topic, String payload) {
        MqttMessage message = new MqttMessage();
        if (payload != null) {
            message.setPayload(payload.getBytes());
            Log.d("MqttClient", "Payload: " + payload);
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
                    Log.d("MqttClient", "Successfully published message to topic \"" + topic + "\"!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MqttClient", "Error publishing message to topic \"" + topic + "\"!");
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            });
        } catch (MqttException e) {
            Log.d("MqttClient", "An exception has been thrown while trying to publish message to topic \"" + topic + "\"!");
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.d("MqttClient", "NPE has been thrown while trying to publish message to topic \"" + topic + "\". Possible cause: activity got stopped");
            e.printStackTrace();
        }
    }

    public boolean subscribeTopic(String topic) {
        if (client == null) {
            return false;
        }

        try {
            String clientTopic = getClientId() + "/" + topic;

            if (subscribedTopics.contains(clientTopic)) {
                Log.d("MqttClient", "Already subscribed to topic \"" + topic + "\"!");
                return false;
            }

            client.subscribe(clientTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MqttClient", "Subscribed to topic \"" + topic + "\"!");
                    subscribedTopics.add(clientTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MqttClient", "Error subscribing to topic \"" + topic + "\"!");
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            }, this::messageArrived);
        } catch (MqttException e) {
            Log.d("MqttClient", "An exception has been thrown while trying to subscribe to topic \"" + topic + "\"!");
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
            subscribedTopics.remove(getClientId() + "/" + topic);
            Log.d("MqttClient", "Successfully unsubscribed from topic \"" + topic + "\".");
        } catch (MqttException e) {
            Log.d("MqttClient", "An exception has been thrown while trying to unsubscribe from topic \"" + topic + "\"!");
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d("MqttClient", "Message received from topic \"" + topic + "\".");
        byte[] payloadBytes = message.getPayload();
        String payload = null;
        if (!topic.startsWith(getClientId() + "/recipes/img/")){
            if (payloadBytes != null && payloadBytes.length > 0) {
                payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                Log.d("MqttClient", "Message payload: " + payload);
            } else {
                Log.d("MqttClient", "No payload attached!");
            }
        } else {
            Log.d("MqttClient", "Payload has image data");
        }

        if (topic.equals(getClientId() + "/categories")) {
            Log.d("MqttClient", "Received available categories");
            Category[] categories = new Gson().fromJson(payload, Category[].class);
            CategoryStorage.getInstance().setCategories(Arrays.asList(categories.clone()));
        } else if (topic.equals(getClientId() + "/ingredients")) {
            Log.d("MqttClient", "Received available ingredients");
            Ingredient[] ingredients = new Gson().fromJson(payload, Ingredient[].class);
            IngredientStorage.getInstance().setIngredients(Arrays.asList(ingredients.clone()));
        } else if (topic.equals(getClientId() + "/recipes/clear")) {
            //Start of recipe list transfer
            Log.d("MqttClient", "Received command to clear available recipes");
            onTopicReceivedEventListener.onClearRecipes();
        } else if (topic.equals(getClientId() + "/recipes")) {
            Recipe recipe = Recipe.fromJson(payload);
            Log.d("MqttClient", "Received recipe \"" + recipe.Name + "\" (GUID: " + recipe.Guid + ")");
            for (RecipeIngredient recipeIngredient : recipe.Ingredients) {
                recipeIngredient.MeasurementTypeReal = EnumUtils.castIntToMeasurementType(recipeIngredient.MeasurementType);

                Ingredient ingredient = recipeIngredient.Ingredient;
                ingredient.IngredientCategoryReal = EnumUtils.castIntToIngredientCategory(ingredient.IngredientCategory);
                ingredient.MeasurementTypeReal = EnumUtils.castIntToMeasurementType(ingredient.MeasurementType);
            }

            RecipeStorage.getInstance().add(recipe);
            onTopicReceivedEventListener.onRecipeReceived(recipe);
            subscribeTopic("recipes/img/" + recipe.Guid);
            sendMessage("recipes/img", recipe.Guid);

        } else if (topic.startsWith(getClientId() + "/recipes/img/")) {
            String guid = topic.replace(getClientId() + "/recipes/img/", "");
            Log.d("MqttClient", "Received image for recipe GUID: " + guid);

            Recipe recipe = RecipeStorage.getInstance().stream()
                    .filter(x -> x.Guid.equals(guid))
                    .findFirst()
                    .orElse(null);
            if (recipe != null && payloadBytes != null) {
                Log.d("MqttClient", "Adding image to recipe: " + recipe.Name);
                recipe.RecipeImage = new RecipeImage(payloadBytes);
                onTopicReceivedEventListener.onRecipeImageReceived(payloadBytes, guid);
            }

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
