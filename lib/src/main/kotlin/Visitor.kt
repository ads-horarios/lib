// Visitor interface defining the expected methods
interface Visitor {
    fun visit(l: JSONLeaf)
    fun visit(c: JSONComposite): Boolean
    fun visit(jsonKeyValuePair: JSONObject.JSONKeyValuePair) { }
    fun endVisit(c: JSONValue) { }
    fun endVisit(l: JSONComposite) { }
}

// Abstract class representing a JSONValue, which requires implementing the accept() method
abstract class JSONValue {
    abstract fun accept(v: Visitor)
}

// Abstract class representing a JSONComposite, which extends JSONValue
abstract class JSONComposite : JSONValue()

// Abstract class representing a JSONLeaf, which extends JSONValue
// and implements the accept() method by default
abstract class JSONLeaf : JSONValue() {
    override fun accept(v: Visitor) {
        v.visit(this)
    }
}

// Class representing a Visitor implementation that filters JSONValues based on a provided condition
class JSONConditionedVisitor(val filter: (JSONValue) -> Boolean): Visitor {
    var values: MutableList<JSONValue> = mutableListOf()

    override fun visit(l: JSONLeaf) {
        if (filter(l)) values.add(l)
    }

    override fun visit(c: JSONComposite): Boolean {
        if (filter(c)) values.add(c)
        return true
    }

    override fun visit(jsonKeyValuePair: JSONObject.JSONKeyValuePair) {
        if (filter(jsonKeyValuePair)) values.add(jsonKeyValuePair)
    }
}

// Class representing a Visitor implementation that builds a JSON string
class JSONVisitor: Visitor {
    val SEPARATOR: String = ", "
    private var indentLevel: Int = 0

    var jsonString: String = ""
        private set

    override fun visit(l: JSONLeaf) {
        jsonString += when(l) {
            is JSONString -> "\"${l.value}\""
            is JSONNumber -> l.value.toString()
            is JSONBoolean -> l.value.toString()
            is JSONNull -> "null"
            else -> ""
        }
    }

    override fun visit(c: JSONComposite): Boolean {
        jsonString += when(c) {
            is JSONArray -> "[\n"
            is JSONObject -> "{\n"
            else -> ""
        }
        indentLevel++
        return true
    }

    override fun visit(jsonKeyValuePair: JSONObject.JSONKeyValuePair) {
        jsonString += " ".repeat(indentLevel * 2)
        jsonString += "\"${jsonKeyValuePair.key}\": "
    }

    override fun endVisit(v: JSONValue) {
        jsonString += "\n"
        if(v is JSONObject.JSONKeyValuePair) {
            jsonString += SEPARATOR
        }
    }

    override fun endVisit(c: JSONComposite) {
        indentLevel--
        jsonString = jsonString.removeSuffix(SEPARATOR)
        jsonString += "\n" + " ".repeat(indentLevel * 2)

        jsonString += when(c) {
            is JSONArray -> "]"
            is JSONObject -> "}"
            else -> ""
        }
        if(c is JSONObject) {
            jsonString += SEPARATOR
        }
    }
}
