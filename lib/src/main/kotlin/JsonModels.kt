// Data classes representing various types of JSON leaf nodes
data class JSONBoolean(var value: Boolean) : JSONLeaf() {}
data class JSONNull(val value: Nothing? = null) : JSONLeaf() {}
data class JSONString(var value: String): JSONLeaf() {}
data class JSONNumber(var value: Number): JSONLeaf() {}

// Class representing a JSON array, which is a type of composite node
class JSONArray(val values: MutableList<JSONValue>): JSONComposite() {
    // Accept a visitor, passing it to each item in the array
    override fun accept(v: Visitor) {
        if(v.visit(this)) {
            values.forEach {
                it.accept(v)
                // Invoke endVisit unless this is the last item
                if(values.last() != it) {
                    v.endVisit(it)
                }
            }
        }
        // Invoke endVisit after visiting all items
        v.endVisit(this)
    }
}

// Class representing a JSON object, which is a type of composite node
data class JSONObject(val values: MutableList<JSONKeyValuePair>): JSONComposite() {
    // Nested class representing a key-value pair within the object
    data class JSONKeyValuePair(val key: String, var value: JSONValue): JSONValue(){
        // Accept a visitor, passing it to the value
        override fun accept(v: Visitor) {
            v.visit(this)
            value.accept(v)
        }
    }

    // Accept a visitor, passing it to each key-value pair in the object
    override fun accept(v: Visitor) {
        if(v.visit(this)) {
            values.forEach {
                it.accept(v)
                v.endVisit(it)
            }
        }
        // Invoke endVisit after visiting all pairs
        v.endVisit(this)
    }
}
