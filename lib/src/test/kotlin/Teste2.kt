import JSONObject.JSONKeyValuePair
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.List
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation


class Testes {

    val controller = EditorController()
    val jsonBuilder = JsonBuilder()

    data class Person(
        @JSONExclude val id: Int,
        @JSONIdentifier("full_name") val name: String,
        @Inject val age: Int,
        @InjectAdd val address: String
    )


    @Test
    open fun testNestedJSONObject() {
        val deeplyNestedMap = JSONObject(
            List.of(
                JSONKeyValuePair("deeplyNestedKey1", JSONString("deeplyNestedValue1")),
                JSONKeyValuePair("deeplyNestedKey2", JSONNumber(999)),
                JSONKeyValuePair("deeplyNestedKey3", JSONBoolean(true)),
                JSONKeyValuePair(
                    "deeplyNestedKey4",
                    JSONArray(List.of(JSONString("deeplyNestedItem1"), JSONString("deeplyNestedItem2")))
                ),
                JSONKeyValuePair("deeplyNestedKey5", JSONString("deeplyNestedTest"))
            )
        )
        val nestedMap = JSONObject(
            List.of(
                JSONKeyValuePair("nestedKey1", JSONString("nestedValue1")),
                JSONKeyValuePair("nestedKey2", JSONNumber(456)),
                JSONKeyValuePair("nestedKey3", JSONBoolean(false)),
                JSONKeyValuePair(
                    "nestedKey4",
                    JSONArray(List.of(JSONString("nestedItem1"), JSONString("nestedItem2")))
                ),
                JSONKeyValuePair("nestedKey5", deeplyNestedMap)
            )
        )
        val map = JSONObject(
            List.of(
                JSONKeyValuePair("key1", JSONString("value1")),
                JSONKeyValuePair("key2", JSONNumber(123)),
                JSONKeyValuePair("key3", JSONBoolean(true)),
                JSONKeyValuePair("key4", JSONArray(List.of(JSONString("item1"), JSONString("item2")))),
                JSONKeyValuePair("key5", JSONNumber(789)),
                JSONKeyValuePair("key6", nestedMap)
            )
        )
        val visitor = JSONVisitor()
        map.accept(visitor)
        val expectedOutput =
            "{ \"key1\": \"value1\", \"key2\": 123, \"key3\": true, \"key4\": [ \"item1\", \"item2\" ], \"key5\": 789, \"key6\": { \"nestedKey1\": \"nestedValue1\", \"nestedKey2\": 456, \"nestedKey3\": false, \"nestedKey4\": [ \"nestedItem1\", \"nestedItem2\" ], \"nestedKey5\": { \"deeplyNestedKey1\": \"deeplyNestedValue1\", \"deeplyNestedKey2\": 999, \"deeplyNestedKey3\": true, \"deeplyNestedKey4\": [ \"deeplyNestedItem1\", \"deeplyNestedItem2\" ], \"deeplyNestedKey5\": \"deeplyNestedTest\" } } }"
        assertEquals(expectedOutput, visitor.jsonString)
    }

    @Test
    fun testEmptyNestedJSONObject() {
        val deeplyNestedMap = JSONObject(mutableListOf<JSONKeyValuePair>())
        val nestedMap = JSONObject(List.of(JSONKeyValuePair("nestedKey1", deeplyNestedMap)))
        val map = JSONObject(List.of(JSONKeyValuePair("key1", nestedMap)))
        val visitor = JSONVisitor()
        map.accept(visitor)
        assertEquals(
            "{\n  \"key1\": {\n    \"nestedKey1\": {\n\n    }, \n" +
                    "\n  }, " +
                    "\n\n}, ",
            visitor.jsonString
        )
    }

    @Test
    fun `test JSONExclude annotation`() {
        val person = Person(1, "John Doe", 25, "123 Street")
        val properties = person::class.declaredMemberProperties
        val hasExcludedAnnotation = properties.any { it.hasAnnotation<JSONExclude>() }
        assertEquals(true, hasExcludedAnnotation)
    }

    @Test
    fun `test JSONIdentifier annotation`() {
        val person = Person(1, "John Doe", 25, "123 Street")
        val nameProperty = person::class.declaredMemberProperties.find { it.name == "name" }
        val identifier = nameProperty?.findAnnotation<JSONIdentifier>()?.identifier
        assertEquals("full_name", identifier)
    }

    @Test
    fun testParseInputString() {
        val jsonValue = controller.parseInput("Hello", "String")
        assertTrue(jsonValue is JSONString)
        assertEquals("Hello", (jsonValue as JSONString).value)
    }

    @Test
    fun testParseInputBoolean() {
        val jsonValue = controller.parseInput("true", "Boolean")
        assertTrue(jsonValue is JSONBoolean)
        assertEquals(true, (jsonValue as JSONBoolean).value)
    }

    @Test
    fun testParseInputNumber() {
        val jsonValue = controller.parseInput("123.4", "Number")
        assertTrue(jsonValue is JSONNumber)
        assertEquals(123.4, (jsonValue as JSONNumber).value)
    }

    @Test
    fun testParseInputArray() {
        val jsonValue = controller.parseInput("1,2,3", "Array")
        assertTrue(jsonValue is JSONArray)
        assertEquals(3, (jsonValue as JSONArray).values.size)
    }

    @Test
    fun testParseInputObject() {
        val jsonValue = controller.parseInput("key:value,key2:value2", "Object")
        assertTrue(jsonValue is JSONObject)
        assertEquals(2, (jsonValue as JSONObject).values.size)
    }

    @Test
    fun testParseToJsonArray() {
        val map: MutableMap<String, JSONValue> = mutableMapOf("array" to JSONArray(mutableListOf(JSONString("Hello"), JSONNumber(123), JSONBoolean(true))))
        val jsonString = controller.parseToJson(map)
        assertEquals("{\n  \"array\": [\"Hello\", 123, true]\n}", jsonString)
    }

    @Test
    fun testParseToJsonObject() {
        val map: MutableMap<String, JSONValue> = mutableMapOf("object" to JSONObject(mutableListOf(JSONObject.JSONKeyValuePair("key", JSONString("Hello")))))
        val jsonString = controller.parseToJson(map)
        assertEquals("{\n  \"object\": {\n    \"key\": \"Hello\"\n  }\n}", jsonString)
    }

    @Test
    fun testParseToJsonNestedObject() {
        val map: MutableMap<String, JSONValue> = mutableMapOf(
            "object" to JSONObject(mutableListOf(
                JSONObject.JSONKeyValuePair("key", JSONObject(mutableListOf(
                    JSONObject.JSONKeyValuePair("nestedKey", JSONString("Hello"))
                )))
            ))
        )
        val jsonString = controller.parseToJson(map)
        assertEquals("{\n  \"object\": {\n    \"key\": {\n      \"nestedKey\": \"Hello\"\n    }\n  }\n}", jsonString)
    }






    @Test
    fun testObserverAddRemove() {
        val observer = object : Observer {
            override fun update() {}
        }
        controller.observer.addObserver(observer)
        assertTrue(controller.observer.observers.contains(observer))
        controller.observer.removeObserver(observer)
        assertFalse(controller.observer.observers.contains(observer))
    }

    @Test
    fun `test add array to jsonObject`() {
        val jsonValue = controller.parseInput("1,2,3", "Array")
        controller.jsonObject["array"] = jsonValue
        assertTrue(controller.jsonObject.containsKey("array"))
        assertEquals(JSONArray::class, controller.jsonObject["array"]!!::class)
    }

    @Test
    fun `test remove array from jsonObject`() {
        val jsonValue = controller.parseInput("1,2,3", "Array")
        controller.jsonObject["array"] = jsonValue
        controller.jsonObject.remove("array")
        assertFalse( controller.jsonObject.containsKey("array") )
    }

    @Test
    fun `test modify array in jsonObject`() {
        val jsonValue = controller.parseInput("1,2,3", "Array")
        controller.jsonObject["array"] = jsonValue
        val jsonNewValue = controller.parseInput("1,2,3,4", "Array")
        controller.jsonObject["array"] = jsonNewValue
        assertEquals(jsonNewValue, controller.jsonObject["array"])
    }

    @Test
    fun `test parse nested object to Json`() {
        val nestedObject = mapOf("subKey1" to JSONString("subValue1"), "subKey2" to JSONString("subValue2"))
        controller.jsonObject["nestedObject"] = JSONObject(nestedObject.map { JSONObject.JSONKeyValuePair(it.key, it.value) }.toMutableList())
        assertEquals(nestedObject, (controller.jsonObject["nestedObject"] as JSONObject).values.associate { it.key to it.value })
    }

    @Test
    fun `test JsonBuilder build with nested object`() {
        val nestedObject = mapOf("subKey1" to "subValue1", "subKey2" to "subValue2")
        val jsonObject = jsonBuilder.build(nestedObject)
        assertTrue ( jsonObject is JSONObject )
        assertEquals(nestedObject.keys, (jsonObject as JSONObject).values.map { it.key }.toSet())
    }

    @Test
    fun `test JsonBuilder build with null value`() {
        val jsonObject = jsonBuilder.build(null)
        assertTrue ( jsonObject is JSONNull )
    }

    @Test
    fun `test JsonBuilder toJsonString with nested object`() {
        val nestedObject = mapOf("subKey1" to "subValue1", "subKey2" to "subValue2")
        val jsonObject = jsonBuilder.build(nestedObject)
        val jsonString = jsonBuilder.toJsonString(jsonObject)
        assertTrue ( jsonString.contains("subKey1") && jsonString.contains("subKey2") )
    }

    @Test
    fun `test JsonBuilder toJsonString with null value`() {
        val jsonObject = jsonBuilder.build(null)
        val jsonString = jsonBuilder.toJsonString(jsonObject)
        assertEquals("null", jsonString)
    }

    @Test
    fun `test JSONArray values`() {
        val jsonArray = JSONArray(mutableListOf(JSONNumber(1), JSONNumber(2), JSONNumber(3)))
        assertEquals(3, jsonArray.values.size)
        assertTrue ( jsonArray.values.all { it is JSONNumber })
    }

    @Test
    fun `test JSONObject values`() {
        val jsonObject = JSONObject(mutableListOf(JSONObject.JSONKeyValuePair("key1", JSONString("value1")), JSONObject.JSONKeyValuePair("key2", JSONString("value2"))))
        assertEquals(2, jsonObject.values.size)
        assertTrue ( jsonObject.values.all { it.value is JSONString } )
    }

    @Test
    fun `test parse invalid input string`() {
        try {
            controller.parseInput("Invalid", "InvalidType")
        } catch (e: IllegalArgumentException) {
            assertEquals("Invalid input type", e.message)
        }
    }

    @Test
    fun `test parse empty input string`() {
        val jsonValue = controller.parseInput("", "String")
        assertTrue(jsonValue is JSONString)
        assertEquals("", (jsonValue as JSONString).value)
    }
}
