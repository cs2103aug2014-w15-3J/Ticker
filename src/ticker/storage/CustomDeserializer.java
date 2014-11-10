package ticker.storage;

import java.lang.reflect.Type;

import ticker.common.DeadlineTask;
import ticker.common.FloatingTask;
import ticker.common.RepeatingTask;
import ticker.common.Task;
import ticker.common.TimedTask;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

//@author A0116673A

/**
 * This class specifies the deserializer used by Storage when converting from
 * JSON to Java Object. This is to be used together with Google-GSON library. As
 * each type of task has their own unique ID, this deserializer is able tor read
 * that and convert them into appropriate Java Object.
 *
 */

public class CustomDeserializer implements JsonDeserializer<Task> {

	public Task deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		if (json == null) {
			return null;
		} else {
			int id = json.getAsJsonObject().get("id").getAsInt();
			switch (id) {
			case 1:
				return context.deserialize(json, FloatingTask.class);
			case 2:
				return context.deserialize(json, TimedTask.class);
			case 3:
				return context.deserialize(json, RepeatingTask.class);
			case 4:
				return context.deserialize(json, DeadlineTask.class);
			default:
				return null;
			}
		}
	}
}