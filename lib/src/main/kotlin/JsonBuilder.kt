import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
//import kotlin.reflect

class JsonBuilder {

    fun toJsonString(jsonValue: JSONValue): String {
        val visitor = JSONVisitor()
        jsonValue.accept(visitor)
        return visitor.jsonString.removeSuffix(visitor.SEPARATOR)
    }
    fun build(from: Any?): JSONValue {
        return when(from) {
            null -> JSONNull()
            is Boolean -> JSONBoolean(from)
            is String -> JSONString(from)
            is Number -> JSONNumber(from)
            is Enum<*> -> JSONString(from.name)
            // List and Set become a JSONArray
            is List<*> -> JSONArray(from.map { it -> build(it) }.toMutableList())
            is Set<*> -> JSONArray(from.map { it -> build(it) }.toMutableList())
            // Map becomes a JSONObject
            is Map<*, *> -> JSONObject(from.entries.map { JSONObject.JSONKeyValuePair(it.key.toString(),
                build(it.value)
            ) }.toMutableList())
            // Else it defaults to treating the parameter as an object
            else -> {
                val clazz: KClass<*> = from::class

                // Construct the list of key-value pairs from the object's properties
                val keypairList: MutableList<JSONObject.JSONKeyValuePair> =
                    clazz.declaredMemberProperties
                        .filter { !it.hasAnnotation<JSONExclude>() }
                        .map {
                            JSONObject.JSONKeyValuePair(
                                // If the property has a JSONIdentifier annotation, use it; otherwise, use the property's name
                                if (it.hasAnnotation<JSONIdentifier>())
                                    it.findAnnotation<JSONIdentifier>()!!.identifier
                                else
                                    it.name,
                                // Recursively build the JSONValue from the property's value
                                build(it.getter.call(from))
                            )
                        }.toMutableList()

                return JSONObject(keypairList)
            }
        }
    }
}
