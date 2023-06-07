data class JSONBoolean(var value: Boolean) : JSONLeaf() {}
data class JSONNull(val value: Nothing? = null) : JSONLeaf() {}
data class JSONString(var value: String): JSONLeaf() {}
data class JSONNumber(var value: Number): JSONLeaf() {}


class JSONArray(val values: MutableList<JSONValue>): JSONComposite() {
    override fun accept(v: Visitor) {
        if(v.visit(this)) {
            values.forEach {
                it.accept(v)
                if(values.last() != it) {
                    v.endVisit(it)
                }
            }
        }
        v.endVisit(this)
    }
}

data class JSONObject(val values: MutableList<JSONKeyValuePair>): JSONComposite() {
    data class JSONKeyValuePair(val key: String, var value: JSONValue): JSONValue(){
        override fun accept(v: Visitor) {
            v.visit(this)
            value.accept(v)
        }
    }

    override fun accept(v: Visitor) {
        if(v.visit(this)) {
            values.forEach {
                it.accept(v)
                v.endVisit(it)
            }
        }
        v.endVisit(this)
    }
}
