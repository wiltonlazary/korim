package com.soywiz.korim.bitmap

import com.soywiz.korim.color.RGBA
import com.soywiz.korio.util.UByteArray

class Bitmap8(
	width: Int,
	height: Int,
	data: ByteArray = ByteArray(width * height),
	palette: IntArray = IntArray(255)
) : BitmapIndexed(8, width, height, data, palette) {
	operator override fun set(x: Int, y: Int, color: Int) = Unit.apply { datau[index(x, y)] = color }
	operator override fun get(x: Int, y: Int): Int = datau[index(x, y)]
	override fun get32(x: Int, y: Int): Int = palette[get(x, y)]

	override fun toString(): String = "Bitmap8($width, $height, palette=${palette.size})"
}
