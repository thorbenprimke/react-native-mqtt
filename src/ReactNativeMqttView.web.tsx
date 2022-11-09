import * as React from 'react';

import { ReactNativeMqttViewProps } from './ReactNativeMqtt.types';

function ReactNativeMqttWebView(props: ReactNativeMqttViewProps) {
  return (
    <div>
      <span>{props.name}</span>
    </div>
  );
}

export default ReactNativeMqttWebView;
