import { useEffect, useState } from "react";
import { SafeAreaView, StyleSheet, Text, View } from "react-native";

import * as ReactNativeMqtt from "react-native-mqtt";

const authToken = "";

type LiveChatItem = {
  id: string;
  type: string;
  text?: string;
};

const Item = ({ item }: { item: LiveChatItem }) => {
  if (item.type === "livestreamchatmessage") {
    return (
      <View style={{ flexDirection: "row", marginVertical: 10 }}>
        <Text>
          {item.sender.full_name}
          {" says "}
          {item.text}
        </Text>
      </View>
    );
  } else if (item.type === "livestreamuserjoin") {
    return (
      <View style={{ flexDirection: "row", marginVertical: 10 }}>
        <Text>
          {item.sender.full_name}
          {" joined the livestream "}
        </Text>
      </View>
    );
  }
  console.log({ item });
  return (
    <View>
      <Text>Unknown type {item.type}</Text>
    </View>
  );
};

export default function App() {
  const [latestMessage, setLatestMessage] = useState("");
  const [viewerCount, setViewerCount] = useState(0);
  const [latestUserJoined, setLatestUserJoined] = useState("");
  const chatItems: LiveChatItem[] = [];

  const pinsubTopic = {
    id: "5264678671687400261",
    ivs_channel_arn: null,
    endpoint: "wss://mqtt.pinterest.com/prod000/",
    topic_type: "creator-class-instance",
    type: "pinsubtopic",
  };

  const maybeSubscribeToTopic = () => {
    ReactNativeMqtt.subscribeToTopic(pinsubTopic.id);
  };

  useEffect(() => {
    const connectionStateSubscription =
      ReactNativeMqtt.addConnectionStateListener((value) => {
        console.log({ value });
        if (value.connectionState === "connected") {
          maybeSubscribeToTopic();
        }
      });
    const messageReceiveSubscription =
      ReactNativeMqtt.addMessageReceiveListener((value) => {
        const mqttPayload = JSON.parse(value.message);
        const type = mqttPayload.data.type;
        if (type === "livestreamchatmessage") {
          setLatestMessage(mqttPayload.data.text);
          console.log({ item: mqttPayload.data });
        } else if (type === "liveproductshowcasesviewercountupdate") {
        } else if (type === "livestreamviewerstats") {
          setViewerCount(mqttPayload.data.viewer_count);
        } else if (type === "livestreamuserjoin") {
          console.log({ item: mqttPayload.data });
        } else {
          // console.log({ mqttPayload });
        }
      });

    ReactNativeMqtt.createAndConnectClient(
      // "ws://broker.emqx.io:8083/mqtt",
      // "ws://broker.mqttdashboard.com:8000/mqtt",
      pinsubTopic.endpoint,
      "userId" + (Math.random() * 1000).toString(),
      authToken
    );

    return () => {
      messageReceiveSubscription.remove();
      connectionStateSubscription.remove();
      ReactNativeMqtt.cleanup();
    };
  }, []);

  return (
    <SafeAreaView style={{ flex: 1 }}>
      <View style={styles.container}>
        <Text>MQTT Topic: {pinsubTopic.id}</Text>
        <Text>Viewer Count: {viewerCount}</Text>
        <Text>Latest message: {latestMessage}</Text>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
  },
});
