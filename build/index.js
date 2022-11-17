import { EventEmitter } from "expo-modules-core";
import ReactNativeMQTTModule from "./ReactNativeMqttModule";
const emitter = new EventEmitter(ReactNativeMQTTModule);
export function addThemeListener(listener) {
    return emitter.addListener("onMessageReceived", listener);
}
export function createClientForEndpoint(endPoint, userName) {
    return ReactNativeMQTTModule.createClientForEndpoint(endPoint, userName);
}
//# sourceMappingURL=index.js.map