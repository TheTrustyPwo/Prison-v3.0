package net.evilkingdom.prison.modules.users.currency.serializers;

import com.google.gson.*;
import net.evilkingdom.prison.modules.users.currency.Currency;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CurrencySerializer implements JsonSerializer<Map<Currency, Long>>, JsonDeserializer<Map<Currency, Long>> {
    @Override
    public JsonElement serialize(Map<Currency, Long> src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        for (final Currency currency : src.keySet())
            jsonObject.add(currency.toString(), context.serialize(src.get(currency)));
        return jsonObject;
    }

    @Override
    public Map<Currency, Long> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final Map<Currency, Long> currencies = new HashMap<>();
        for (final String string : jsonObject.keySet())
            currencies.put(Currency.valueOf(string.toUpperCase()), jsonObject.get(string).getAsLong());
        return currencies;
    }
}
