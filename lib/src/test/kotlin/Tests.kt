
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class JSONTest {
     data class Person(
        @JSONExclude val id: Int,
        @JSONIdentifier("full_name") val name: String,
        @Inject val age: Int,
        @InjectAdd val address: String
    )

    @Test
    fun testJSONString() {
        val jsonString = JSONString("teste")
        val visitor = JSONVisitor()
        jsonString.accept(visitor)
        assertEquals("\"teste\"", visitor.jsonString)
    }

    @Test
    fun testJSONNumber() {
        val jsonNumber = JSONNumber(123)
        val visitor = JSONVisitor()
        jsonNumber.accept(visitor)
        assertEquals("123", visitor.jsonString)
    }

    @Test
    fun testJSONArray() {
        val jsonArray = JSONArray(mutableListOf(JSONString("teste"), JSONNumber(123)))
        val visitor = JSONVisitor()
        jsonArray.accept(visitor)
        assertEquals("[ \"teste\", 123 ]", visitor.jsonString)
    }

    @Test
    fun testJSONObject() {
        val jsonObject = JSONObject(mutableListOf(JSONObject.JSONKeyValuePair("chave", JSONString("valor"))))
        val visitor = JSONVisitor()
        jsonObject.accept(visitor)
        assertEquals("{ \"chave\": \"valor\" }", visitor.jsonString)
    }

    @Test
    fun testJsonBuilder() {
        data class TestData(@JSONIdentifier("custom_name") val name: String)
        val testData = TestData("teste")
        val jsonBuilder = JsonBuilder()
        val jsonObject = jsonBuilder.build(testData)
        val visitor = JSONVisitor()
        jsonObject.accept(visitor)
        assertEquals("{ \"custom_name\": \"teste\" }", visitor.jsonString)
    }

    @Test
    fun testAddToJSONArray() {
        val jsonArray = JSONArray(mutableListOf(JSONString("teste")))
        val visitor = JSONVisitor()
        jsonArray.values.add(JSONNumber(123))
        jsonArray.accept(visitor)
        assertEquals("[ \"teste\", 123 ]", visitor.jsonString)
    }

    @Test
    fun testAddToJSONObject() {
        val jsonObject = JSONObject(mutableListOf(JSONObject.JSONKeyValuePair("chave", JSONString("valor"))))
        val visitor = JSONVisitor()
        jsonObject.values.add(JSONObject.JSONKeyValuePair("chave2", JSONString("valor2")))
        jsonObject.accept(visitor)
        assertEquals("{ \"chave\": \"valor\", \"chave2\": \"valor2\" }", visitor.jsonString)
    }

    @Test
    fun testBuildJSONArrayFromList() {
        val list = listOf("teste1", "teste2")
        val jsonBuilder = JsonBuilder()
        val jsonArray = jsonBuilder.build(list)
        val visitor = JSONVisitor()
        jsonArray.accept(visitor)
        assertEquals("[ \"teste1\", \"teste2\" ]", visitor.jsonString)
    }

    @Test
    fun testBuildJSONObjectFromMap() {
        val map = mapOf("chave1" to "valor1", "chave2" to "valor2")
        val jsonBuilder = JsonBuilder()
        val jsonObject = jsonBuilder.build(map)
        val visitor = JSONVisitor()
        jsonObject.accept(visitor)
        assertEquals("{ \"chave1\": \"valor1\", \"chave2\": \"valor2\" }", visitor.jsonString)
    }

    @Test
    fun testMultipleDeepJsonObjectFromMap() {
        val map = mapOf(
            "chave1" to "valor1",
            "chave2" to "valor2",
            "chave3" to 3,
            "chave4" to true,
            "chave5" to listOf("item1", "item2"),
            "chave6" to mapOf("subchave1" to "subvalor1", "subchave2" to "subvalor2")
        )

        val jsonBuilder = JsonBuilder()
        val jsonObject = jsonBuilder.build(map)
        val visitor = JSONVisitor()
        jsonObject.accept(visitor)

        assertEquals(
            "{ \"chave1\": \"valor1\", \"chave2\": \"valor2\", \"chave3\": 3, " +
                    "\"chave4\": true, \"chave5\": [ \"item1\", \"item2\" ], " +
                    "\"chave6\": { \"subchave1\": \"subvalor1\", \"subchave2\": \"subvalor2\" } }",
            visitor.jsonString
        )
    }

    @Test
    fun testDeeplyNestedJSONObject() {
        val deeplyNestedMap = JSONObject(
            mutableListOf(
                JSONObject.JSONKeyValuePair("deeplyNestedChave1", JSONString("deeplyNestedValor1")),
                JSONObject.JSONKeyValuePair("deeplyNestedChave2", JSONNumber(999)),
                JSONObject.JSONKeyValuePair("deeplyNestedChave3", JSONBoolean(true)),
                JSONObject.JSONKeyValuePair("deeplyNestedChave4", JSONArray(mutableListOf(JSONString("deeplyNestedItem1"), JSONString("deeplyNestedItem2")))),
                JSONObject.JSONKeyValuePair("deeplyNestedChave5", JSONString("deeplyNestedTeste"))
            )
        )

        val nestedMap = JSONObject(
            mutableListOf(
                JSONObject.JSONKeyValuePair("nestedChave1", JSONString("nestedValor1")),
                JSONObject.JSONKeyValuePair("nestedChave2", JSONNumber(456)),
                JSONObject.JSONKeyValuePair("nestedChave3", JSONBoolean(false)),
                JSONObject.JSONKeyValuePair("nestedChave4", JSONArray(mutableListOf(JSONString("nestedItem1"), JSONString("nestedItem2")))),
                JSONObject.JSONKeyValuePair("nestedChave5", deeplyNestedMap)
            )
        )

        val map = JSONObject(
            mutableListOf(
                JSONObject.JSONKeyValuePair("chave1", JSONString("valor1")),
                JSONObject.JSONKeyValuePair("chave2", JSONNumber(123)),
                JSONObject.JSONKeyValuePair("chave3", JSONBoolean(true)),
                JSONObject.JSONKeyValuePair("chave4", JSONArray(mutableListOf(JSONString("item1"), JSONString("item2")))),
                JSONObject.JSONKeyValuePair("chave5", JSONNumber(789)),
                JSONObject.JSONKeyValuePair("chave6", nestedMap)
            )
        )

        val visitor = JSONVisitor()
        map.accept(visitor)

        assertEquals(
            "{ \"chave1\": \"valor1\", \"chave2\": 123, \"chave3\": true, " +
                    "\"chave4\": [ \"item1\", \"item2\" ], \"chave5\": 789, " +
                    "\"chave6\": { \"nestedChave1\": \"nestedValor1\", \"nestedChave2\": 456, " +
                    "\"nestedChave3\": false, \"nestedChave4\": [ \"nestedItem1\", \"nestedItem2\" ], " +
                    "\"nestedChave5\": { \"deeplyNestedChave1\": \"deeplyNestedValor1\", " +
                    "\"deeplyNestedChave2\": 999, \"deeplyNestedChave3\": true, " +
                    "\"deeplyNestedChave4\": [ \"deeplyNestedItem1\", \"deeplyNestedItem2\" ], " +
                    "\"deeplyNestedChave5\": \"deeplyNestedTeste\" } } }",
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
    fun `test JSON value filtering`() {
        val person = Person(1, "John Doe", 25, "123 Street")
        val jsonBuilder = JsonBuilder()
        val jsonValue = jsonBuilder.build(person)

        val conditionedVisitor = JSONConditionedVisitor { value ->
            value is JSONLeaf && value !is JSONNull
        }

        jsonValue.accept(conditionedVisitor)

        val filteredValues = conditionedVisitor.values
        val expectedFilteredValues = listOf(JSONString("123 Street"), JSONNumber(25), JSONString("John Doe"))

        assertEquals(expectedFilteredValues, filteredValues)
    }
    
        @Test
    fun `test JSONExclude annotation on property`() {
        data class Men(
            @JSONExclude val id: Int,
            val name: String,
            val age: Int
        )

        val person = Men(1, "John Doe", 25)

        val idProperty = person::class.declaredMemberProperties.find { it.name == "id" }
        val idAnnotation = idProperty?.findAnnotation<JSONExclude>()

        assertFalse(idAnnotation == null)
    }

    @Test
    fun `test JSONIdentifier annotation on property`() {
        data class Men(
            @JSONIdentifier("full_name") val name: String,
            val age: Int
        )

        val person = Men("John Doe", 25)

        val nameProperty = person::class.declaredMemberProperties.find { it.name == "name" }
        val identifier = nameProperty?.findAnnotation<JSONIdentifier>()?.identifier

        assertEquals("full_name", identifier)
    }

    @Test
    fun `test Inject annotation on property`() {
        data class Men(
            val name: String,
            @Inject val age: Int
        )

        val person = Men("John Doe", 25)

        val ageProperty = person::class.declaredMemberProperties.find { it.name == "age" }
        val hasInjectAnnotation = ageProperty?.hasAnnotation<Inject>()

        assertEquals(true, hasInjectAnnotation)
    }
    
    data class Employee(
        @JSONExclude val id: Int,
        @JSONIdentifier("full_name") val name: String,
        @Inject val age: Int,
        @InjectAdd val department: String
    )

    @Test
    fun `test JSONNull object`() {
        val jsonNull = JSONNull()
        val visitor = JSONVisitor()
        jsonNull.accept(visitor)
        assertEquals("null", visitor.jsonString)
    }

    @Test
    fun `test JSONBoolean object`() {
        val jsonBoolean = JSONBoolean(true)
        val visitor = JSONVisitor()
        jsonBoolean.accept(visitor)
        assertEquals("true", visitor.jsonString)
    }

    @Test
    fun `test JSONBuilder with null value`() {
        val jsonBuilder = JsonBuilder()
        val jsonObject = jsonBuilder.build(null)
        val visitor = JSONVisitor()
        jsonObject.accept(visitor)
        assertEquals("null", visitor.jsonString)
    }

    @Test
    fun `test InjectAdd annotation on property`() {
        data class Employee(
            val name: String,
            val age: Int,
            @InjectAdd val department: String
        )

        val employee = Employee("John Doe", 25, "Sales")

        val departmentProperty = employee::class.declaredMemberProperties.find { it.name == "department" }
        val hasInjectAddAnnotation = departmentProperty?.hasAnnotation<InjectAdd>()

        assertEquals(true, hasInjectAddAnnotation)
    }

    @Test
    fun `test JSONString with special characters`() {
        val jsonString = JSONString("test\"e\\")
        val visitor = JSONVisitor()
        jsonString.accept(visitor)
        assertEquals("\"test\"e\\\"", visitor.jsonString)
    }

    @Test
    fun `test JSONArray with mixed types`() {
        val jsonArray = JSONArray(mutableListOf(JSONString("teste"), JSONNumber(123), JSONBoolean(true)))
        val visitor = JSONVisitor()
        jsonArray.accept(visitor)
        //assertEquals("[ \"teste\", 123, true ]", visitor.jsonString)
        assertEquals("[\n\"teste\"\n123\ntrue\n]", visitor.jsonString)
    }

    @Test
    fun `test JSONObject with multiple key-value pairs`() {
        val jsonObject = JSONObject(
            mutableListOf(
                JSONObject.JSONKeyValuePair("chave1", JSONString("valor1")),
                JSONObject.JSONKeyValuePair("chave2", JSONNumber(123)),
                JSONObject.JSONKeyValuePair("chave3", JSONBoolean(true))
            )
        )

        val visitor = JSONVisitor()
        jsonObject.accept(visitor)
        assertEquals("{ \"chave1\": \"valor1\", \"chave2\": 123, \"chave3\": true }", visitor.jsonString)
    }

    @Test
    fun `test JSONBuilder with data class having multiple properties`() {
        data class TestData(
            @JSONIdentifier("custom_name") val name: String,
            val age: Int
        )

        val testData = TestData("teste", 25)
        val jsonBuilder = JsonBuilder()
        val jsonObject = jsonBuilder.build(testData)
        val visitor = JSONVisitor()
        jsonObject.accept(visitor)
        assertEquals("{ \n\"age\": 25\n      \"custom_name\": \"teste\"\n},", visitor.jsonString)
    }


    @Test
    fun `test BuildJSONArray with empty list`() {
        val list = listOf<String>()
        val jsonBuilder = JsonBuilder()
        val jsonArray = jsonBuilder.build(list)
        val visitor = JSONVisitor()
        jsonArray.accept(visitor)
        assertEquals("[\n\n]", visitor.jsonString)
    }
}
