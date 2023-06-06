# JSONBuilder - Biblioteca de Criação de JSON

A `JSONBuilder` é uma biblioteca para criar e manipular estruturas de dados JSON em memória na utilizando a linguagem Kotlin. A biblioteca oferece uma série de recursos que incluem a criação de objetos JSON, a derivação de objetos JSON a partir de objetos de valor (data classes) através de reflexão, bem como suporte para a serialização de tais objetos em formato JSON textual.

## Estrutura da Biblioteca

A biblioteca possui várias classes, interfaces e anotações, que são explicadas em detalhes abaixo.

### Classes Principais

A biblioteca consiste em várias classes que representam os diferentes tipos de nós em uma estrutura de dados JSON. As classes `JSONBoolean`, `JSONNull`, `JSONString` e `JSONNumber` representam os tipos de folhas na estrutura de dados, enquanto as classes `JSONArray` e `JSONObject` representam tipos de nós compostos.

As classes `JSONArray` e `JSONObject` estendem a classe abstrata `JSONComposite`, que por sua vez estende a classe abstrata `JSONValue`. As classes folha também estendem `JSONValue`.

### Classe JSONBuilder

A classe `JsonBuilder` é responsável por criar objetos `JSONValue` a partir de objetos Kotlin. A classe usa a técnica de reflexão para determinar o tipo de objeto de entrada e criar o objeto `JSONValue` correspondente.

### Interfaces

A interface `Visitor` define os métodos que serão implementados pelas classes de visitantes. Os visitantes são usados para varrer a estrutura de dados JSON.

### Anotações

As anotações `JSONExclude`, `JSONIdentifier`, `Inject` e `InjectAdd` são usadas para personalizar o comportamento da classe `JsonBuilder` ao criar objetos `JSONValue`.

## Uso da Biblioteca

A seguir, são apresentados alguns exemplos de uso da biblioteca `JSONBuilder`.

### Criação de um objeto JSON

A criação de um objeto JSON é feita usando a classe `JsonBuilder`.

```kotlin
val jsonBuilder = JsonBuilder()
val jsonObject = jsonBuilder.build(myObject)
```

### Serialização de um objeto JSON

A serialização de um objeto JSON em formato de texto é feita usando a classe `JSONVisitor`.

```kotlin
val jsonVisitor = JSONVisitor()
jsonObject.accept(jsonVisitor)
println(jsonVisitor.jsonString)
```

### Pesquisa na estrutura de dados JSON

A pesquisa na estrutura de dados JSON é feita usando a classe `JSONConditionedVisitor`.

```kotlin
val jsonVisitor = JSONConditionedVisitor { it is JSONNumber && it.value > 0 }
jsonObject.accept(jsonVisitor)
println(jsonVisitor.values)
```

### Uso de anotações

As anotações podem ser usadas para personalizar o comportamento da classe `JsonBuilder` ao criar objetos `JSONValue`.

```kotlin
data class MyClass(
    @JSONIdentifier("custom_name") val name: String,
    @JSONExclude val excludeMe: String
)
```

Neste exemplo, a propriedade `name` será representada com o identificador "custom_name" no JSON resultante, e a propriedade `excludeMe` será excluída.

### GUI
Toda a interface gráfica foi criada por meio da biblioteca `swing`.
Dentro do arquivo `EditorView.kt` temos os componentes utilizados.


## Conclusão

A biblioteca `JSONBuilder` oferece uma maneira flexível e poderosa de criar e manipular estruturas de dados JSON em memória na linguagem Kotlin. A biblioteca também oferece recursos avançados, como suporte para derivação
