package org.rsmod.routefinder.loc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LocLayerConstantsTest {
    @Test
    fun `ensure all standard shapes are mapped`() {
        val standardShapes = 0..22
        for (shape in standardShapes) {
            assertDoesNotThrow { LocLayerConstants.of(shape) }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [23, 24, 25, 31])
    fun `ensure fallback for unknown shapes`(shape: Int) {
        assertEquals(LocLayerConstants.GROUND, LocLayerConstants.of(shape))
    }

    @Test
    fun `verify specific mappings`() {
        assertEquals(LocLayerConstants.WALL, LocLayerConstants.of(LocShapeConstants.WALL_STRAIGHT))
        assertEquals(
            LocLayerConstants.WALL_DECOR,
            LocLayerConstants.of(LocShapeConstants.WALLDECOR_STRAIGHT_NOOFFSET),
        )
        assertEquals(
            LocLayerConstants.GROUND,
            LocLayerConstants.of(LocShapeConstants.CENTREPIECE_STRAIGHT),
        )
        assertEquals(
            LocLayerConstants.GROUND,
            LocLayerConstants.of(LocShapeConstants.WALL_DIAGONAL),
        )
        assertEquals(
            LocLayerConstants.GROUND_DECOR,
            LocLayerConstants.of(LocShapeConstants.GROUND_DECOR),
        )
    }
}
