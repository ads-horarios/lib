@Target(AnnotationTarget.PROPERTY)
annotation class JSONExclude

@Target(AnnotationTarget.PROPERTY)
annotation class JSONIdentifier(val identifier: String)

@Target(AnnotationTarget.PROPERTY)
annotation class Inject

@Target(AnnotationTarget.PROPERTY)
annotation class InjectAdd