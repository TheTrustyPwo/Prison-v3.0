package net.evilkingdom.prison.modules.privatemines.serializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.evilkingdom.prison.Prison;
import net.evilkingdom.prison.modules.privatemines.PrivateMine;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesHandler;
import net.evilkingdom.prison.modules.privatemines.objects.Theme;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

public class PrivateMineAdapter implements JsonSerializer<PrivateMine>, JsonDeserializer<PrivateMine>  {
    @Override
    public JsonElement serialize(PrivateMine privateMine, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("owner", privateMine.getOwner().toString());
        jsonObject.addProperty("theme", privateMine.getTheme().getName());
        jsonObject.addProperty("isPublic", privateMine.isPublic());
        jsonObject.addProperty("banned", Prison.getGson().toJson(privateMine.getBanned()));
        jsonObject.addProperty("whitelisted", Prison.getGson().toJson(privateMine.getWhitelisted()));
        jsonObject.addProperty("location", Prison.getGson().toJson(privateMine.getLocation()));
        jsonObject.addProperty("tax", privateMine.getTax());
        return jsonObject;
    }

    @Override
    public PrivateMine deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final UUID owner = UUID.fromString(jsonObject.get("owner").getAsString());
        final Theme theme = PrivateMinesHandler.getInstance().getTheme(jsonObject.get("theme").getAsString());
        final boolean isPublic = jsonObject.get("isPublic").getAsBoolean();
        final Set<UUID> banned = Prison.getGson().fromJson(jsonObject.get("banned").getAsString(), new TypeToken<Set<UUID>>(){}.getType());
        final Set<UUID> whitelisted = Prison.getGson().fromJson(jsonObject.get("whitelisted").getAsString(), new TypeToken<Set<UUID>>(){}.getType());
        final Location location = Prison.getGson().fromJson(jsonObject.get("location").getAsString(), Location.class);
        final double tax = jsonObject.get("tax").getAsDouble();

        final PrivateMine privateMine = new PrivateMine(owner);
        privateMine.setTheme(theme);
        privateMine.setPublic(isPublic);
        privateMine.setBanned(banned);
        privateMine.setWhitelisted(whitelisted);
        privateMine.setLocation(location);
        privateMine.setTax(tax);

        return privateMine;
    }
}
