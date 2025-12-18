package org.mockito.kotlin.internal

import com.nhaarman.expect.expect
import kotlin.reflect.KClass
import org.junit.Test
import test.ValueClass
import test.assertThrows

class ValueClassSupportTest {
    @Test
    fun `toKotlinType should pass through null value`() {
        /* Given */
        val value: String? = null

        /* When */
        val result: ValueClass? = value.toKotlinType(ValueClass::class)

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun `toKotlinType should pass through non-value-class types`() {
        /* Given */
        val value = "test"

        /* When */
        val result: String = value.toKotlinType(String::class)

        /* Then */
        expect(result).toBe(value)
    }

    @Test
    fun `toKotlinType should box value as value class`() {
        /* Given */
        val value = "test"

        /* When */
        val result: ValueClass? = value.toKotlinType(ValueClass::class)

        /* Then */
        expect(result).toNotBeNull()
        expect(result!!.content).toBe(value)
    }

    @Test
    fun `toKotlinType should not re-box value class value`() {
        /* Given */
        val value = ValueClass("test")

        /* When */
        val result: ValueClass? = value.toKotlinType(ValueClass::class)

        /* Then */
        expect(result).toNotBeNull()
        expect(result).toBe(value)
    }

    @Test
    fun `boxAsValueClass should box non-value-class types`() {
        /* Given */
        val value = "test"

        /* When */
        val result: ValueClass = value.boxAsValueClass(ValueClass::class)

        /* Then */
        expect(result).toNotBeNull()
        expect((result).content).toBe(value)
    }

    @Test
    fun `boxAsValueClass should pass through null value`() {
        /* Given */
        val value: String? = null

        /* When */
        val result: ValueClass? = value.boxAsValueClass(ValueClass::class)

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun `boxAsValueClass should throw if target type is non-value-class`() {
        /* Given */
        val value: String? = null

        /* When, Then */
        val exception = assertThrows<IllegalArgumentException> { value.boxAsValueClass(Int::class) }
        expect(exception.message).toBe("kotlin.Int is not a value class.")
    }

    @Test
    fun `unboxValueClass should unbox a value class type`() {
        /* Given */
        val value = ValueClass("test")

        /* When */
        val result = value.unboxValueClass()

        /* Then */
        expect(result).toNotBeNull()
        expect(result).toBeInstanceOf<String>()
        expect(result).toBe(value.content)
    }

    @Test
    fun `unboxValueClass should throw if source type is non-value-class`() {
        /* Given */
        val value = "test"

        /* When, Then */
        val exception = assertThrows<IllegalArgumentException> { value.unboxValueClass() }
        expect(exception.message).toBe("kotlin.String is not a value class.")
    }

    @Test
    fun `valueClassInnerClass should yield the inner type of a value class type`() {
        /* Given */
        val clazz = ValueClass::class

        /* When */
        val result = clazz.valueClassInnerClass()

        /* Then */
        expect(result).toNotBeNull()
        expect(result).toBeInstanceOf<KClass<String>>()
    }

    @Test
    fun `valueClassInnerClass should throw if source type is non-value-class`() {
        /* Given */
        val clazz = String::class

        /* When, Then */
        val exception = assertThrows<IllegalArgumentException> { clazz.valueClassInnerClass() }
        expect(exception.message).toBe("kotlin.String is not a value class.")
    }
}
