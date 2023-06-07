interface Observer {
    fun update()
}

class ObjectObserver {
    public val observers = mutableListOf<Observer>()

    fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    fun modifyObject() {
        notifyObservers()
    }

    private fun notifyObservers() {
        observers.forEach { it.update() }
    }
}

class EditorController {
    val jsonObject: MutableMap<String, JSONValue> = mutableMapOf()
    val observer = ObjectObserver()

    fun parseInput(value: String, dataType: String): JSONValue {
        val jsonBuilder = JsonBuilder()
        return when (dataType) {
            "Array" -> {
                val listValues = value.split(",").map { it.trim() }.map { parseIndividualValue(it) }
                JSONArray(listValues.toMutableList())
            }

            "Object" -> {
                val mapValues = value.split(",").map {
                    val keyValue = it.split(":")
                    Pair(keyValue[0].trim(), parseIndividualValue(keyValue[1].trim()))
                }.toMap()
                jsonBuilder.build(mapValues)
            }

            "String" -> JSONString(value)
            "Boolean" -> JSONBoolean(value.toBoolean())
            "Number" -> try {
                JSONNumber(value.toDouble())
            } catch (e: Exception) {
                JSONNumber(0)
        }
            else -> JSONNull()
        }
    }

    fun parseIndividualValue(value: String): JSONValue {
        return when {
            value.equals("true", ignoreCase = true) -> JSONBoolean(true)
            value.equals("false", ignoreCase = true) -> JSONBoolean(false)
            value.toDoubleOrNull() != null -> JSONNumber(value.toDouble())
            else -> JSONString(value)
        }
    }

    fun parseToJson(map: MutableMap<String, JSONValue>, indentLevel: Int = 0): String {
        val indent = " ".repeat(indentLevel * 2)
        val jsonBuilder = StringBuilder("{\n")
        map.forEach { (key, value) ->
            jsonBuilder.append("$indent  \"$key\":")
            when (value) {
                is JSONString -> jsonBuilder.append(" \"${value.value}\",\n")
                is JSONBoolean -> jsonBuilder.append(" ${value.value},\n")
                is JSONNumber -> jsonBuilder.append(" ${value.value},\n")
                is JSONArray -> {
                    val arrayStrings = value.values.map {
                        when (it) {
                            is JSONString -> "\"${it.value}\""
                            is JSONBoolean -> "${it.value}"
                            is JSONNumber -> "${it.value}"
                            is JSONObject -> parseToJson(it.values.associate { it.key to it.value }.toMutableMap(), indentLevel + 1).removeSuffix("\n${" ".repeat((indentLevel + 1) * 2)}}")
                            is JSONArray -> "[${it.values.joinToString(", ") { parseToJson(mapOf("" to it).toMutableMap(), indentLevel + 1).removePrefix("{\n  \"\":").removeSuffix("\n${" ".repeat((indentLevel + 1) * 2)}}") }}]"
                            is JSONNull -> "null"
                            else -> ""
                        }
                    }
                    jsonBuilder.append(" [${arrayStrings.joinToString(", ")}],\n")
                }
                is JSONObject -> {
                    val nestedObjectMap = value.values.associate { it.key to it.value }
                    jsonBuilder.append(" ${parseToJson(nestedObjectMap.toMutableMap(), indentLevel + 1)},\n")
                }
                is JSONNull -> jsonBuilder.append(" null,\n")
            }
        }
        if (jsonBuilder.length > 2) {
            jsonBuilder.delete(jsonBuilder.length - 2, jsonBuilder.length)
        }
        jsonBuilder.append("\n$indent}")
        return jsonBuilder.toString()
    }
}
