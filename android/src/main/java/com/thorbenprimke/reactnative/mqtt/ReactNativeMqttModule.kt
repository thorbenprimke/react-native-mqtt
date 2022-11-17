package com.thorbenprimke.reactnative.mqtt

import android.content.Context
import android.content.Context.*
import android.content.SharedPreferences
import android.util.Log
import androidx.core.os.bundleOf
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import info.mqtt.android.service.MqttAndroidClient
import java.util.Properties
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

class ReactNativeMqttModule : Module() {
    // Each module class must implement the definition function. The definition consists of components
    // that describes the module's functionality and behavior.
    // See https://docs.expo.dev/modules/module-api for more details about available components.
    override fun definition() = ModuleDefinition {
        // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
        // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
        // The module will be accessible from `requireNativeModule('ReactNativeMqtt')` in JavaScript.
        Name("ReactNativeMqtt")

        Events("onMessageReceived")

        Function("hello") {
            return@Function "World"
        }

        Function("cleanup") {
            this@ReactNativeMqttModule.cleanup()
        }


        Function("createClientForEndpoint") { endPoint: String, userName: String ->
            return@Function createClientForEndpoint(endPoint, userName)
        }

        Function("subscribeToTopic") { topicId: String ->
            Log.d("ReactNativeMqttModule", "connected: " + client?.isConnected)

            Log.d("ReactNativeMqttModule", "setting callback")

            client?.setCallback(object: MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.d("ReactNativeMqttModule", "connection lost")
                    Log.d("ReactNativeMqttModule", cause?.message ?: "no connection lost message")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d("ReactNativeMqttModule", "got message")
                    Log.d("ReactNativeMqttModule", String(message?.payload ?: "None".toByteArray()))
                    this@ReactNativeMqttModule.sendEvent(
                        "onMessageReceived",
                        bundleOf(
                            "topic" to (topic ?: "noTopic"),
                            "message" to String(message?.payload ?: "None".toByteArray())
                        )
                    )
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d("ReactNativeMqttModule", "delivery complete")
                }
            })
            Log.d("ReactNativeMqttModule", "connecting")

            client?.connect(
                mqttConnectOptions,
                null,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("ReactNativeMqttModule", "connected")
                        client?.subscribe(
                            "testtopic/1",
                            0,
                            null,
                            object : IMqttActionListener {
                                override fun onSuccess(asyncActionToken: IMqttToken?) {
                                    Log.d("ReactNativeMqttModule", "subscribed")
                                }

                                override fun onFailure(
                                    asyncActionToken: IMqttToken?,
                                    exception: Throwable?
                                ) {
                                    Log.d("ReactNativeMqttModule", "sub failure")

                                }

                            }
                        )
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d("ReactNativeMqttModule", "connect failure")
                        Log.d("ReactNativeMqttModule", exception?.message ?: "no message")
                    }
                }
            )
        }
    }

    private val context
        get() = requireNotNull(appContext.reactContext)

    private var client: MqttAndroidClient? = null

    private fun createClientForEndpoint(endPoint: String, userName: String): Boolean {
        Log.d("ReactNativeMqttModule", "client starting")
        if (client != null) {
            client?.disconnect()
            client = null
        }
        client = MqttAndroidClient(
            context = context,
            serverURI = "ws://broker.mqttdashboard.com:8000",
            clientId = "clientId-Ec4CtnQFd2" + System.currentTimeMillis().toString()
        )
        Log.d("ReactNativeMqttModule", "client created")
        return true
    }

    private fun cleanup() {
        Log.d("ReactNativeMqttModule", "cleanup starting")
        client?.let {
            it.unsubscribe("testtopic/1")
            it.disconnect()
        }
        client = null
        Log.d("ReactNativeMqttModule", "cleaned up")
    }

    private val mqttConnectOptions = MqttConnectOptions().apply {
        isAutomaticReconnect = true
        isCleanSession = false
//        customWebSocketHeaders = Properties().apply {
//            setProperty(
//                AUTHORIZATION_HEADER_NAME,
//                AUTHORIZATION_HEADER_VALUE_FORMAT_STR.formatOther(ApiAuthManager.getActiveAccessToken())
//            )
//            setProperty(USER_AGENT, ApiUtils.userAgentForHeaders)
//        }
        mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
    }

}
