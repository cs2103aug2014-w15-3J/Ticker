package ticker.storage;

import java.lang.reflect.Type;

import tickerPackage.DeadlineTask;
import tickerPackage.FloatingTask;
import tickerPackage.RepeatingTask;
import tickerPackage.Task;
import tickerPackage.TimedTask;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

class CustomDeserializer implements JsonDeserializer<Task>{

	public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

		if (json == null) {
			return null;
		} else {
				// null management can be improved
			int id = json.getAsJsonObject().get("id").getAsInt();
			switch(id){
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