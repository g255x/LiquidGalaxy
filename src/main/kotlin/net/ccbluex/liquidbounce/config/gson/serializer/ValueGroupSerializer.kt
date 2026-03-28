/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2026 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */

package net.ccbluex.liquidbounce.config.gson.serializer

import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.ccbluex.liquidbounce.config.types.Value
import net.ccbluex.liquidbounce.config.types.group.ValueGroup
import net.ccbluex.liquidbounce.features.module.ClientModule
import net.ccbluex.liquidbounce.features.module.ModuleCategories
import net.ccbluex.liquidbounce.utils.client.toLowerCamelCase
import net.ccbluex.liquidbounce.utils.render.Alignment
import java.lang.reflect.Type

class ValueGroupSerializer(
    private val withValueType: Boolean, private val includePrivate: Boolean, private val includeNotAnOption: Boolean
) : JsonSerializer<ValueGroup> {

    companion object {

        /**
         * This serializer is used to serialize [ValueGroup]s to JSON
         */
        @JvmField
        val FILE_SERIALIZER = ValueGroupSerializer(
            withValueType = false, includePrivate = true, includeNotAnOption = true
        )

        /**
         * This serializer is used to serialize [ValueGroup]s to JSON for interop communication
         */
        @JvmField
        val INTEROP_SERIALIZER = ValueGroupSerializer(
            withValueType = true, includePrivate = true, includeNotAnOption = false
        )

        /**
         * This serializer is used to serialize [ValueGroup]s to JSON for public config
         */
        @JvmField
        val PUBLIC_SERIALIZER = ValueGroupSerializer(
            withValueType = false, includePrivate = false, includeNotAnOption = true
        )

        /**
         * Serialize a [ValueGroup] to a read-only [JsonObject]
         *
         * Used for interop communication by [ReadOnlyComponentSerializer]
         * and [ReadOnlyThemeSerializer].
         */
        @JvmStatic
        fun serializeReadOnly(
            valueGroup: ValueGroup,
            context: JsonSerializationContext
        ): JsonObject = JsonObject().apply {
            for (v in valueGroup.inner) {
                add(v.name.toLowerCamelCase(), when (v) {
                    is Alignment -> context.serialize(v, Alignment::class.java)
                    is ValueGroup -> serializeReadOnly(v, context)
                    else -> context.serialize(v.inner)
                })
            }
        }

    }

    override fun serialize(
        src: ValueGroup, typeOfSrc: Type, context: JsonSerializationContext
    ) = JsonObject().apply {
        addProperty("name", src.name)
        try {
            val values = filterValues(src)

            if (withValueType) {
                add("value", context.serialize(values.map { value ->
                    serializeValueWithTranslation(value, context)
                }))
            } else {
                add("value", context.serialize(values))
            }
        } catch (e: Exception) {
            println("failed to serialize config for ${src.name}")
            throw e
        }
        if (withValueType) {
            add("valueType", context.serialize(src.valueType))
        }
    }

    /**
     * Filters values based on the serializer configuration
     */
    private fun filterValues(src: ValueGroup): List<Value<*>> {
        return src.inner
            .filter { includeNotAnOption || !it.notAnOption }
            .filter { includePrivate || checkIfInclude(it) }
    }

    /**
     * Serializes a value with translation for interop communication
     */
    private fun serializeValueWithTranslation(
        value: Value<*>,
        context: JsonSerializationContext
    ): JsonObject {
        val jsonObject = context.serialize(value).asJsonObject

        if (value is ValueGroup) {
            return jsonObject
        }

        val translationKey = buildTranslationKey(value)
        if (translationKey != null) {
            val translation = net.ccbluex.liquidbounce.lang.translation(translationKey)
            if (translation.string != translationKey) {
                jsonObject.addProperty("translatedName", translation.string)
            }
        }

        return jsonObject
    }

    /**
     * Builds the translation key for a value
     * Translation key format: liquidbounce.module.{module}.value.{value}.name
     */
    private fun buildTranslationKey(value: Value<*>): String? {
        return value.key
            ?.let { key -> key.replaceFirst(Regex("(.module.)"), "$1value.") }
            ?.let { "$it.name" }
    }

    /**
     * Checks if value should be included in public config
     */
    private fun checkIfInclude(value: Value<*>): Boolean {
        /**
         * Do not include values that are not supposed to be shared
         * with other users
         */
        if (value.doNotInclude()) {
            return false
        }

        // Might check if value is module
        if (value is ClientModule) {
            /**
             * Do not include modules that are heavily user-personalised
             */
            if (value.category == ModuleCategories.RENDER || value.category == ModuleCategories.FUN) {
                return false
            }
        }

        // Otherwise include value
        return true
    }

}
