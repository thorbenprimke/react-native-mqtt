import { StyleSheet, Text, View } from "react-native";

import * as ReactNativeMqtt from "react-native-mqtt";

export default function App() {
  return (
    <View style={styles.container}>
      <Text>{ReactNativeMqtt.hello()} mixed with TS</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    alignItems: "center",
    justifyContent: "center",
  },
});
