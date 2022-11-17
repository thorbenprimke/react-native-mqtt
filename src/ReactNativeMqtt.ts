import { EventEmitter, Subscription } from "expo-modules-core";
import ReactNativeMQTTModule from "./ReactNativeMqttModule";

const emitter = new EventEmitter(ReactNativeMQTTModule);

export type CONNECTION_STATE = "disconnected" | "connected" | "error";

export type ConnectionStateChangeEvent = {
  connectionState: CONNECTION_STATE;
};

export function addConnectionStateListener(
  listener: (event: ConnectionStateChangeEvent) => void
): Subscription {
  return emitter.addListener<ConnectionStateChangeEvent>(
    "onChangeConnectionState",
    listener
  );
}

export type MessageReceiveEvent = {
  message: string;
};

export function addMessageReceiveListener(
  listener: (event: MessageReceiveEvent) => void
): Subscription {
  return emitter.addListener<MessageReceiveEvent>("onReceiveMessage", listener);
}

export function createAndConnectClient(
  endPoint: String,
  userName: String,
  accessToken: String
): boolean {
  return ReactNativeMQTTModule.createAndConnectClient(
    endPoint,
    userName,
    accessToken
  );
}

export function subscribeToTopic(topicId: String) {
  ReactNativeMQTTModule.subscribeToTopic(topicId);
}

export function cleanup() {
  ReactNativeMQTTModule.cleanup();
}
