package org.example;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.util.concurrent.TimeUnit;


public class Main {


    public static void main(String[] args) {
         String  PATH_CLIENT_CERT = "/Users/khaledmohamedali/Desktop/Projects/AWS/demo/deviceCert.pem.crt"
                ,PATH_PRIVATE_KEY="/Users/khaledmohamedali/Desktop/Projects/AWS/demo/private.pem.key"
                ,PATH_ROOT_CERT="/Users/khaledmohamedali/Desktop/Projects/AWS/demo/AmazonRootCA1.pem"
                 ,clientId = "demo"
                 ,endPoint = "a1xdlnsp133c8v-ats.iot.us-east-1.amazonaws.com";

        AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(PATH_CLIENT_CERT,PATH_PRIVATE_KEY);

        builder.withCertificateAuthorityFromPath(null,PATH_ROOT_CERT);
        builder.withClientId(clientId);
        builder.withEndpoint(endPoint);
        builder.withCleanSession(true);


        MqttClientConnection connection = builder.build();

        try {
            boolean sessionPresent =    connection.connect().get();

            System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");

            String message = "Hello, AWS IoT Core!",topic = "sensor/100";

            connection.subscribe(topic, QualityOfService.AT_LEAST_ONCE, (message1) -> {
                System.out.println("Received message on topic " + topic+ ": " + new String(message1.getPayload()));
            }).get();



            MqttMessage mqttMessage = new MqttMessage(topic,message.getBytes(),QualityOfService.AT_LEAST_ONCE);
            connection.publish(mqttMessage).get();
            System.out.println("Published message: " + message);


            TimeUnit.MINUTES.sleep(5);



        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            connection.close();
        }


    }


}