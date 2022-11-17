import { EventEmitter } from "expo-modules-core";
import ReactNativeMQTTModule from "./ReactNativeMqttModule";
const emitter = new EventEmitter(ReactNativeMQTTModule);
export function addConnectionStateListener(listener) {
    return emitter.addListener("onChangeConnectionState", listener);
}
export function addMessageReceiveListener(listener) {
    return emitter.addListener("onReceiveMessage", listener);
}
export function createAndConnectClient(endPoint, userName, accessToken) {
    return ReactNativeMQTTModule.createAndConnectClient(endPoint, userName, accessToken);
}
export function subscribeToTopic(topicId) {
    ReactNativeMQTTModule.subscribeToTopic(topicId);
}
export function cleanup() {
    ReactNativeMQTTModule.cleanup();
}
//# sourceMappingURL=ReactNativeMqtt.js.map