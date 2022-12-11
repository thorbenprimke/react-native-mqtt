package com.thorbenprimke.reactnative.mqtt

import androidx.core.os.bundleOf
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.types.Enumerable
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

        Events("onChangeConnectionState", "onReceiveMessage")

        Function("createAndConnectClient") { endPoint: String, userName: String, accessToken: String ->
            return@Function createClientForEndpoint(endPoint, userName, accessToken);
        }

        Function("subscribeToTopic") { topicId: String ->
            return@Function subscribeToTopic(topicId)
        }

        Function("cleanup") {
            this@ReactNativeMqttModule.cleanup()
        }
    }

    private val context
        get() = requireNotNull(appContext.reactContext)

    private var client: MqttAndroidClient? = null
    private var subscribedTopicId: String? = null

    private fun createClientForEndpoint(endPoint: String, userName: String, accessToken: String): Boolean {
        if (client != null) {
            client?.disconnect()
            client = null
        }
        client = MqttAndroidClient(
            context = context,
            serverURI = endPoint,
            clientId = userName,
        )
        client?.setCallback(object: MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                this@ReactNativeMqttModule.sendEvent(
                    "onChangeConnectionState",
                    bundleOf(
                        "connectionState" to ConnectionState.DISCONNECTED.value
                    )
                )
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                this@ReactNativeMqttModule.sendEvent(
                    "onReceiveMessage",
                    bundleOf(
                        "message" to String(message?.payload ?: "None".toByteArray())
                    )
                )
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
            }
        })

        mqttConnectOptions.customWebSocketHeaders = Properties().apply {
            setProperty(
                AUTHORIZATION_HEADER_NAME,
                AUTHORIZATION_HEADER_VALUE_FORMAT_STR.format(accessToken)
            )
        }

        client?.connect(
            mqttConnectOptions,
            null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    this@ReactNativeMqttModule.sendEvent(
                        "onChangeConnectionState",
                        bundleOf(
                            "connectionState" to ConnectionState.CONNECTED.value
                        )
                    )
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    this@ReactNativeMqttModule.sendEvent(
                        "onChangeConnectionState",
                        bundleOf(
                            "connectionState" to ConnectionState.ERROR.value
                        )
                    )
                }
            }
        )
        return true
    }

    private fun subscribeToTopic(topicId: String): Boolean {
        subscribedTopicId = topicId
        client?.subscribe(
            topicId,
            0,
            null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    // TODO: Add an event for this
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken?,
                    exception: Throwable?
                ) {
                    // TODO: Add an event for this
                }
            }
        )
        return true;
    }

    private fun cleanup() {
        client?.let {
            subscribedTopicId?.let { topicId ->
                it.unsubscribe(topicId)
            }
            it.disconnect()
        }
        client = null
    }

    private val mqttConnectOptions = MqttConnectOptions().apply {
        isAutomaticReconnect = true
        isCleanSession = false
        mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
    }
}

enum class ConnectionState(val value: String) : Enumerable {
    DISCONNECTED("disconnected"),
    CONNECTED("connected"),
    ERROR("error")
}

const val AUTHORIZATION_HEADER_NAME = "Authorization"
const val AUTHORIZATION_HEADER_VALUE_FORMAT_STR = "Bearer %s"
