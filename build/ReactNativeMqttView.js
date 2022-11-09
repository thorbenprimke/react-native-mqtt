import { requireNativeViewManager } from 'expo-modules-core';
import * as React from 'react';
const NativeView = requireNativeViewManager('ReactNativeMqtt');
export default function ReactNativeMqttView(props) {
    return React.createElement(NativeView, { name: props.name });
}
//# sourceMappingURL=ReactNativeMqttView.js.map