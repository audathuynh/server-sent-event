package org.maxsure.rabbitmq.rest.amqp;

import java.lang.reflect.Type;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.Delivery;

@Component
public class JSONMessageMarshaller implements MessageMarshaller {

    private final Gson gson;

    public JSONMessageMarshaller() {
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        Type byteArrayType = new TypeToken<byte[]>() {}.getType();
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(
                        mapType,
                        new JsonSerializer<Map<String, Object>>() {
                            @Override
                            public JsonElement serialize(
                                    Map<String, Object> src,
                                    Type typeOfSrc,
                                    JsonSerializationContext context) {
                                JsonObject object = new JsonObject();

                                src.forEach((key, value) -> object.add(key,
                                        new JsonPrimitive(value.toString())));
                                return object;
                            }
                        })
                .registerTypeAdapter(
                        byteArrayType,
                        new JsonSerializer<byte[]>() {
                            @Override
                            public JsonElement serialize(
                                    byte[] src,
                                    Type typeOfSrc,
                                    JsonSerializationContext context) {
                                return new JsonPrimitive(new String(src));
                            }
                        });
        this.gson = gsonBuilder.create();
    }

    @Override
    public String marshall(Delivery delivery) {
        return gson.toJson(delivery);
    }

}
