package org.hnau.commons.app.projector.fractal.anchor

interface SAnchorsStateMediator {
    fun updateAlong(along: Along)
    fun updatePosition(position: Position)
    fun setIsDragging(isDragging: Boolean)
}