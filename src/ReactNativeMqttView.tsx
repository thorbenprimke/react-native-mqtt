import { requireNativeViewManager } from 'expo-modules-core';
import * as React from 'react';

import { ReactNativeMqttViewProps } from './ReactNativeMqtt.types';

const NativeView: React.ComponentType<ReactNativeMqttViewProps> =
  requireNativeViewManager('ReactNativeMqtt');

export default function ReactNativeMqttView(props: ReactNativeMqttViewProps) {
  return <NativeView name={props.name} />;
}
