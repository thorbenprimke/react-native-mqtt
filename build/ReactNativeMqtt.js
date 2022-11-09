import { NativeModulesProxy, EventEmitter } from 'expo-modules-core';
// Import the native module. On web, it will be resolved to ReactNativeMqtt.web.ts
// and on native platforms to ReactNativeMqtt.ts
import ReactNativeMqtt from './ReactNativeMqttModule';
import ReactNativeMqttView from './ReactNativeMqttView';
// Get the native constant value.
export const PI = ReactNativeMqtt.PI;
export function hello() {
    return ReactNativeMqtt.hello();
}
export async function setValueAsync(value) {
    return await ReactNativeMqtt.setValueAsync(value);
}
// For now the events are not going through the JSI, so we have to use its bridge equivalent.
// This will be fixed in the stable release and built into the module object.
// Note: On web, NativeModulesProxy.ReactNativeMqtt is undefined, so we fall back to the directly imported implementation
const emitter = new EventEmitter(NativeModulesProxy.ReactNativeMqtt ?? ReactNativeMqtt);
export function addChangeListener(listener) {
    return emitter.addListener('onChange', listener);
}
export { ReactNativeMqttView };
//# sourceMappingURL=ReactNativeMqtt.js.map