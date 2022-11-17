import ExpoModulesCore
import CocoaMQTT


public class ReactNativeMqttModule: Module {
  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  // See https://docs.expo.dev/modules/module-api for more details about available components.
  public func definition() -> ModuleDefinition {
    // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
    // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
    // The module will be accessible from `requireNativeModule('ReactNativeMqtt')` in JavaScript.
    Name("ReactNativeMqtt")

    Events("onChangeConnectionState")
    Events("onReceiveMessage")

      Function("createAndConnectClient") { (
        endPoint: String,
        userName: String,
        accessToken: String
      ) -> Bool in
        let websocket = CocoaMQTTWebSocket(uri: endPoint)
        websocket.enableSSL = true
        websocket.shouldConnectWithURIOnly = true
        websocket.headers = [
              "Authorization": "Bearer \(accessToken)",
        ]

        mqtt = CocoaMQTT(clientID: userName, host: "", socket: websocket)
        mqtt?.willMessage = CocoaMQTTMessage(topic: "/will", string: "dieout")
        mqtt?.keepAlive = 60
        mqtt?.enableSSL = true
        mqtt?.delegate = self
        return mqtt?.connect() == true
    }

    Function("subscribeToTopic") { (topicId: String) -> Void in
        print("[MQTT] subscribing")
        mqtt?.subscribe([(topicId, CocoaMQTTQoS.qos2)])
    }
      
    Function("cleanup") { () -> Void in
        mqtt?.disconnect()
        mqtt = nil
    }
  }

  private var mqtt: CocoaMQTT?

  enum ConnectionState: String, Enumerable {
    case disconnected
    case connected
    case error
  }
}

extension ReactNativeMqttModule: CocoaMQTTDelegate {
    public func mqtt(_ mqtt: CocoaMQTT, didReceive trust: SecTrust, completionHandler: @escaping (Bool) -> Void) {
        print(trust)
        completionHandler(true)
    }

    public func mqtt(_ mqtt: CocoaMQTT, didConnectAck ack: CocoaMQTTConnAck) {
        self.sendEvent("onChangeConnectionState", [
            "connectionState": ConnectionState.connected.rawValue
        ])
    }
    
    public func mqtt(_ mqtt: CocoaMQTT, didPublishMessage message: CocoaMQTTMessage, id: UInt16) {
    }
    
    public func mqtt(_ mqtt: CocoaMQTT, didPublishAck id: UInt16) {
    }
    
    public func mqtt(_ mqtt: CocoaMQTT, didReceiveMessage message: CocoaMQTTMessage, id: UInt16 ) {
        print("[MQTT] didReceiveMessage \(message)")
        self.sendEvent("onReceiveMessage", [
            "message": String(bytes: message.payload, encoding: .utf8)
        ])
    }
    
    public func mqtt(_ mqtt: CocoaMQTT, didSubscribeTopics success: NSDictionary, failed: [String]) {
        print("[MQTT] didSubscribeTopics \(success), \(failed)")
    }
    
    public func mqtt(_ mqtt: CocoaMQTT, didUnsubscribeTopics topics: [String]) {
    }
    
    public func mqttDidPing(_ mqtt: CocoaMQTT) {
    }
    
    public func mqttDidReceivePong(_ mqtt: CocoaMQTT) {
    }
    
    public func mqttDidDisconnect(_ mqtt: CocoaMQTT, withError err: Error?) {
        self.mqtt = nil
        self.sendEvent("onChangeConnectionState", [
            "connectionState": ConnectionState.disconnected.rawValue
        ])
    }
}
