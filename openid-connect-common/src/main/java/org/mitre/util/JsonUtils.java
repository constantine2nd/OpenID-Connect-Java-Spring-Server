/*******************************************************************************
 * Copyright 2015 The MITRE Corporation
 *   and the MIT Kerberos and Internet Trust Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package org.mitre.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;

/**
 * A collection of null-safe converters from common classes and JSON elements, using GSON.
 * 
 * @author jricher
 *
 */
@SuppressWarnings(value = {"rawtypes", "unchecked"})
public class JsonUtils {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static Gson gson = new Gson();

	/**
	 * Translate a set of strings to a JSON array
	 * @param value
	 * @return
	 */
	public static JsonElement getAsArray(Set<String> value) {
		return gson.toJsonTree(value, new TypeToken<Set<String>>(){}.getType());
	}

	/**
	 * Gets the value of the given member (expressed as integer seconds since epoch) as a Date
	 */
	public static Date getAsDate(JsonObject o, String member) {
		if (o.has(member)) {
			JsonElement e = o.get(member);
			if (e != null && e.isJsonPrimitive()) {
				return new Date(e.getAsInt() * 1000L);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Gets the value of the given member as a JWE Algorithm, null if it doesn't exist
	 */
	public static JWEAlgorithm getAsJweAlgorithm(JsonObject o, String member) {
		String s = getAsString(o, member);
		if (s != null) {
			return JWEAlgorithm.parse(s);
		} else {
			return null;
		}
	}

	/**
	 * Gets the value of the given member as a JWE Encryption Method, null if it doesn't exist
	 */
	public static EncryptionMethod getAsJweEncryptionMethod(JsonObject o, String member) {
		String s = getAsString(o, member);
		if (s != null) {
			return EncryptionMethod.parse(s);
		} else {
			return null;
		}
	}

	/**
	 * Gets the value of the given member as a JWS Algorithm, null if it doesn't exist
	 */
	public static JWSAlgorithm getAsJwsAlgorithm(JsonObject o, String member) {
		String s = getAsString(o, member);
		if (s != null) {
			return JWSAlgorithm.parse(s);
		} else {
			return null;
		}
	}

	/**
	 * Gets the value of the given member as a string, null if it doesn't exist
	 */
	public static String getAsString(JsonObject o, String member) {
		if (o.has(member)) {
			JsonElement e = o.get(member);
			if (e != null && e.isJsonPrimitive()) {
				return e.getAsString();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Gets the value of the given member as a boolean, null if it doesn't exist
	 */
	public static Boolean getAsBoolean(JsonObject o, String member) {
		if (o.has(member)) {
			JsonElement e = o.get(member);
			if (e != null && e.isJsonPrimitive()) {
				return e.getAsBoolean();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the value of the given member as a Long, null if it doesn't exist
	 */
	public static Long getAsLong(JsonObject o, String member) {
		if (o.has(member)) {
			JsonElement e = o.get(member);
			if (e != null && e.isJsonPrimitive()) {
				return e.getAsLong();
			} else {
				return null;
			}			
		} else {
			return null;
		}
	}

	/**
	 * Gets the value of the given given member as a set of strings, null if it doesn't exist
	 */
	public static Set<String> getAsStringSet(JsonObject o, String member) throws JsonSyntaxException {
		if (o.has(member)) {
			if (o.get(member).isJsonArray()) {
				return gson.fromJson(o.get(member), new TypeToken<Set<String>>(){}.getType());
			} else {
				return Sets.newHashSet(o.get(member).getAsString());
			}
		} else {
			return null;
		}
	}

	/**
	 * Gets the value of the given given member as a set of strings, null if it doesn't exist
	 */
	public static List<String> getAsStringList(JsonObject o, String member) throws JsonSyntaxException {
		if (o.has(member)) {
			if (o.get(member).isJsonArray()) {
				return gson.fromJson(o.get(member), new TypeToken<List<String>>(){}.getType());
			} else {
				return Lists.newArrayList(o.get(member).getAsString());
			}
		} else {
			return null;
		}
	}

	/**
	 * Gets the value of the given member as a list of JWS Algorithms, null if it doesn't exist
	 */
	public static List<JWSAlgorithm> getAsJwsAlgorithmList(JsonObject o, String member) {
		List<String> strings = getAsStringList(o, member);
		if (strings != null) {
			List<JWSAlgorithm> algs = new ArrayList<JWSAlgorithm>();
			for (String alg : strings) {
				algs.add(JWSAlgorithm.parse(alg));
			}
			return algs;
		} else {
			return null;
		}
	}

	/**
	 * Gets the value of the given member as a list of JWS Algorithms, null if it doesn't exist
	 */
	public static List<JWEAlgorithm> getAsJweAlgorithmList(JsonObject o, String member) {
		List<String> strings = getAsStringList(o, member);
		if (strings != null) {
			List<JWEAlgorithm> algs = new ArrayList<JWEAlgorithm>();
			for (String alg : strings) {
				algs.add(JWEAlgorithm.parse(alg));
			}
			return algs;
		} else {
			return null;
		}
	}

	/**
	 * Gets the value of the given member as a list of JWS Algorithms, null if it doesn't exist
	 */
	public static List<EncryptionMethod> getAsEncryptionMethodList(JsonObject o, String member) {
		List<String> strings = getAsStringList(o, member);
		if (strings != null) {
			List<EncryptionMethod> algs = new ArrayList<EncryptionMethod>();
			for (String alg : strings) {
				algs.add(EncryptionMethod.parse(alg));
			}
			return algs;
		} else {
			return null;
		}
	}

	public static <T> T base64UrlDecodeObject(String encoded, Class<T> type) {
		if (encoded == null) {
			return null;
		} else {
			T deserialized = null;
			try {
				byte[] decoded = BaseEncoding.base64Url().decode(encoded);
				ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
				ObjectInputStream ois = new ObjectInputStream(bais);
				deserialized = type.cast(ois.readObject());
				ois.close();
				bais.close();
			} catch (Exception ex) {
				logger.error("Unable to decode object", ex);
			}
			return deserialized;
		}
	}

	public static Map readMap(JsonReader reader) throws IOException {
		Map map = new HashMap<String, Object>();
		reader.beginObject();
		while(reader.hasNext()) {
			String name = reader.nextName();
			Object value = null;
			switch(reader.peek()) {
			case STRING:
				value = reader.nextString();
				break;
			case BOOLEAN:
				value = reader.nextBoolean();
				break;
			case NUMBER:
				value = reader.nextLong();
				break;
			}
			map.put(name, value);
		}
		reader.endObject();
		return map;
	}

	public static Set readSet(JsonReader reader) throws IOException {
		Set arraySet = null;
		reader.beginArray();
		switch (reader.peek()) {
		case STRING:
			arraySet = new HashSet<String>();
			while (reader.hasNext()) {
				arraySet.add(reader.nextString());
			}
			break;
		case NUMBER:
			arraySet = new HashSet<Long>();
			while (reader.hasNext()) {
				arraySet.add(reader.nextLong());
			}
			break;
		default:
			arraySet = new HashSet();
			break;
		}
		reader.endArray();
		return arraySet;
	}

	public static void writeNullSafeArray(JsonWriter writer, Set<String> items) throws IOException {
		if (items != null) {
			writer.beginArray();
			for (String s : items) {
				writer.value(s);
			}
			writer.endArray();
		} else {
			writer.nullValue();
		}
	}

}
