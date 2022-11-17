import { Subscription } from "expo-modules-core";
export declare type MessageReceivedEvent = {
    payload: string;
};
export declare function addThemeListener(listener: (event: MessageReceivedEvent) => void): Subscription;
export declare function createClientForEndpoint(endPoint: String, userName: String): boolean;
//# sourceMappingURL=index.d.ts.map