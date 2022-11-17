import { Subscription } from "expo-modules-core";
export declare type CONNECTION_STATE = "disconnected" | "connected" | "error";
export declare type ConnectionStateChangeEvent = {
    connectionState: CONNECTION_STATE;
};
export declare function addConnectionStateListener(listener: (event: ConnectionStateChangeEvent) => void): Subscription;
export declare type MessageReceiveEvent = {
    message: string;
};
export declare function addMessageReceiveListener(listener: (event: MessageReceiveEvent) => void): Subscription;
export declare function createAndConnectClient(endPoint: String, userName: String, accessToken: String): boolean;
export declare function subscribeToTopic(topicId: String): void;
export declare function cleanup(): void;
//# sourceMappingURL=ReactNativeMqtt.d.ts.map