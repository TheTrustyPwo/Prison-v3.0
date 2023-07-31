package net.evilkingdom.prison.modules.users.currency.serializers;

import com.google.gson.*;
import net.evilkingdom.prison.modules.users.currency.Currency;

import java.lang.reflect.Type;

public class CurrencyAdapter implements JsonSerializer<Currency>, JsonDeserializer<Currency> {
    @Override
    public JsonElement serialize(Currency src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public Currency deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Currency.valueOf(json.getAsJsonPrimitive().getAsString());
    }
}
