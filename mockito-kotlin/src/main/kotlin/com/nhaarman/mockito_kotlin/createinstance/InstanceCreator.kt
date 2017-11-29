package com.nhaarman.mockito_kotlin.createinstance

import com.nhaarman.mockito_kotlin.MockitoKotlin
import com.nhaarman.mockito_kotlin.MockitoKotlinException
import org.mockito.Mockito
import org.mockito.internal.creation.bytebuddy.MockAccess
import org.mockito.internal.util.MockUtil
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.*
import kotlin.reflect.jvm.*
import kotlin.reflect.full.starProjectedType
import java.lang.reflect.Array as JavaArray

internal class InstanceCreator() : NonNullProvider {

    override fun <T : Any> createInstance(kClass: KClass<T>): T {
        var cause: Throwable? = null

        @Suppress("UNCHECKED_CAST")
        return MockitoKotlin.instanceCreator(kClass)?.invoke() as T? ?:
                try {
                    when {
                        kClass.hasObjectInstance() -> kClass.objectInstance!!
                        kClass.isPrimitive() -> kClass.toDefaultPrimitiveValue()
                        kClass.isEnum() -> kClass.java.enumConstants.first()
                        kClass.isArray() -> kClass.toArrayInstance()
                        kClass.isClassObject() -> kClass.toClassObject()
                        kClass.isMockable() -> try {
                            kClass.java.uncheckedMock()
                        } catch(e: Throwable) {
                            cause = e
                            kClass.easiestConstructor().newInstance()
                        }
                        else -> kClass.easiestConstructor().newInstance()
                    }
                } catch(e: Exception) {
                    if (e is MockitoKotlinException) throw e

                    cause?.let {
                        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
                        (e as java.lang.Throwable).initCause(it)
                    }
                    throw MockitoKotlinException("Could not create an instance for $kClass.", e)
                }
    }

    /**
     * Tries to find the easiest constructor which it can instantiate.
     */
    private fun <T : Any> KClass<T>.easiestConstructor(): KFunction<T> {
        return constructors
                .sortedBy { it.parameters.withoutOptionalParameters().size }
                .withoutParametersOfType(this.starProjectedType)
                .withoutArrayParameters()
                .firstOrNull() ?: constructors.sortedBy { it.parameters.withoutOptionalParameters().size }
                .withoutParametersOfType(this.starProjectedType)
                .first()
    }

    private fun <T> List<KFunction<T>>.withoutArrayParameters() = filter {
        it.parameters.filter { parameter -> parameter.type.toString().toLowerCase().contains("array") }.isEmpty()
    }

    /**
     * Filters out functions with the given type.
     * This is especially useful to avoid infinite loops where constructors
     * accepting a parameter of their own type, e.g. 'copy constructors'.
     */
    private fun <T : Any> List<KFunction<T>>.withoutParametersOfType(type: KType) = filter {
        it.parameters.filter { it.type == type }.isEmpty()
    }

    private fun List<KParameter>.withoutOptionalParameters() = filterNot { it.isOptional }

    @Suppress("SENSELESS_COMPARISON")
    private fun KClass<*>.hasObjectInstance() = objectInstance != null

    private fun KClass<*>.isMockable(): Boolean {
        return !Modifier.isFinal(java.modifiers) || mockMakerInlineEnabled()
    }

    private fun KClass<*>.isEnum() = java.isEnum
    private fun KClass<*>.isArray() = java.isArray
    private fun KClass<*>.isClassObject() = jvmName.equals("java.lang.Class")
    private fun KClass<*>.isPrimitive() =
            java.isPrimitive || !starProjectedType.isMarkedNullable && simpleName in arrayOf(
                    "Boolean",
                    "Byte",
                    "Short",
                    "Int",
                    "Double",
                    "Float",
                    "Long",
                    "String"
            )

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    private fun <T : Any> KClass<T>.toDefaultPrimitiveValue(): T {
        return when (simpleName) {
            "Boolean" -> true
            "Byte" -> 0.toByte()
            "Short" -> 0.toShort()
            "Int" -> 0
            "Double" -> 0.0
            "Float" -> 0f
            "Long" -> 0
            "String" -> ""
            else -> throw UnsupportedOperationException("Cannot create default primitive for $simpleName.")
        } as T
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    private fun <T : Any> KClass<T>.toArrayInstance(): T {
        return when (simpleName) {
            "ByteArray" -> byteArrayOf()
            "ShortArray" -> shortArrayOf()
            "IntArray" -> intArrayOf()
            "LongArray" -> longArrayOf()
            "DoubleArray" -> doubleArrayOf()
            "FloatArray" -> floatArrayOf()
            else -> {
                val name = java.name.drop(2).dropLast(1)
                return JavaArray.newInstance(Class.forName(name), 0) as T
            }
        } as T
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> KClass<T>.toClassObject(): T {
        return Class.forName("java.lang.Object") as T
    }

    private fun <T : Any> KFunction<T>.newInstance(): T {
        try {
            isAccessible = true
            return callBy(parameters.withoutOptionalParameters().associate {
                it to it.type.createNullableInstance<T>()
            })
        } catch(e: InvocationTargetException) {
            throw MockitoKotlinException(
                    """

        Could not create an instance of class ${this.returnType}, because of an error with the following message:

            "${e.cause?.message}"

        Try registering an instance creator yourself, using MockitoKotlin.registerInstanceCreator<${this.returnType}> {...}.""",
                    e.cause
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> KType.createNullableInstance(): T? {
        if (isMarkedNullable) {
            return null
        }

        val javaType: Type = javaType
        return when (javaType) {
            is ParameterizedType -> (javaType.rawType as Class<T>).uncheckedMock()
            is Class<*> -> createInstance((javaType as Class<T>).kotlin)
            else -> null
        }
    }

    /**
     * Creates a mock instance of given class, without modifying or checking any internal Mockito state.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Class<T>.uncheckedMock(): T {
        val settings = Mockito.withSettings()
              .defaultAnswer(Mockito.RETURNS_DEFAULTS)
              .build(this)

        return MockUtil.createMock(settings).apply {
            (this as? MockAccess)?.mockitoInterceptor = null
        }
    }
}

