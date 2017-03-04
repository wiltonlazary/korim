package com.soywiz.korim.vector

import com.soywiz.korim.color.Colors
import com.soywiz.korim.geom.Matrix2d
import com.soywiz.korim.geom.Rectangle
import com.soywiz.korim.geom.Vector2
import java.util.*

class Context2d(val renderer: Renderer) {
	enum class LineCap { BUTT, ROUND, SQUARE }
	enum class LineJoin { BEVEL, MITER, ROUND }

	open class Renderer {
		open fun render(state: State, fill: Boolean): Unit {
		}

		open fun renderText(state: State, font: Font, text: String, x: Double, y: Double, fill: Boolean): Unit {
		}

		open fun getBounds(font: Font, text: String, out: TextMetrics): Unit {
			out.bounds.setTo(0.0, 0.0, 0.0, 0.0)
		}
	}

	enum class VerticalAlign(val ratio: Double) {
		TOP(0.0), MIDLE(0.5), BASELINE(1.0), BOTTOM(1.0);

		fun getOffsetY(height: Double, baseline: Double): Double = when (this) {
			BASELINE -> baseline
			else -> height * ratio
		}

	}

	enum class HorizontalAlign(val ratio: Double) {
		LEFT(0.0), CENTER(0.5), RIGHT(1.0);

		fun getOffsetX(width: Double): Double = width * ratio
	}

	class State(
		var transform: Matrix2d = Matrix2d(),
		var clip: GraphicsPath? = null,
		var path: GraphicsPath = GraphicsPath(),
		var lineWidth: Double = 1.0,
		var lineCap: LineCap = LineCap.BUTT,
		var lineJoin: LineJoin = LineJoin.MITER,
		var miterLimit: Double = 10.0,
		var strokeStyle: Paint = Color(Colors.BLACK),
		var fillStyle: Paint = Color(Colors.BLACK),
		var font: Font = Font("sans-serif", 10.0),
		var verticalAlign: VerticalAlign = VerticalAlign.BASELINE,
		var horizontalAlign: HorizontalAlign = HorizontalAlign.LEFT,
		var globalAlpha: Double = 1.0
	) {
		fun clone(): State = State(
			transform = transform.clone(),
			clip = clip?.clone(),
			path = path.clone(),
			lineWidth = lineWidth,
			lineCap = lineCap,
			lineJoin = lineJoin,
			miterLimit = miterLimit,
			strokeStyle = strokeStyle,
			fillStyle = fillStyle,
			font = font,
			verticalAlign = verticalAlign,
			horizontalAlign = horizontalAlign,
			globalAlpha = globalAlpha
		)
	}

	internal var state = State()
	private val stack = LinkedList<State>()

	var lineWidth: Double; get() = state.lineWidth; set(value) = run { state.lineWidth = value }
	var lineCap: LineCap; get() = state.lineCap; set(value) = run { state.lineCap = value }
	var strokeStyle: Paint; get() = state.strokeStyle; set(value) = run { state.strokeStyle = value }
	var fillStyle: Paint; get() = state.fillStyle; set(value) = run { state.fillStyle = value }
	var font: Font; get() = state.font; set(value) = run { state.font = value }
	var verticalAlign: VerticalAlign; get() = state.verticalAlign; set(value) = run { state.verticalAlign = value }
	var horizontalAlign: HorizontalAlign; get() = state.horizontalAlign; set(value) = run { state.horizontalAlign = value }
	var globalAlpha: Double; get() = state.globalAlpha; set(value) = run { state.globalAlpha = value }
	inline fun keepApply(callback: Context2d.() -> Unit) = this.apply { keep { callback() } }

	inline fun keep(callback: () -> Unit) {
		save()
		try {
			callback()
		} finally {
			restore()
		}
	}

	fun save() {
		stack.add(state.clone())
	}

	fun restore() {
		state = stack.removeLast()
	}

	fun scale(sx: Double, sy: Double = sx) {
		state.transform.prescale(sx, sy)
	}

	fun rotate(angle: Double) {
		state.transform.prerotate(angle)
	}

	fun translate(tx: Double, ty: Double) {
		state.transform.pretranslate(tx, ty)
	}

	fun transform(a: Double, b: Double, c: Double, d: Double, tx: Double, ty: Double) {
		state.transform.premulitply(a, b, c, d, tx, ty)
	}

	fun setTransform(a: Double, b: Double, c: Double, d: Double, tx: Double, ty: Double) {
		state.transform.setTo(a, b, c, d, tx, ty)
	}

	fun shear(sx: Double, sy: Double) = transform(1.0, sy, sx, 1.0, 0.0, 0.0)

	fun moveTo(x: Int, y: Int) = moveTo(x.toDouble(), y.toDouble())
	fun lineTo(x: Int, y: Int) = lineTo(x.toDouble(), y.toDouble())
	fun quadraticCurveTo(cx: Int, cy: Int, ax: Int, ay: Int) = quadraticCurveTo(cx.toDouble(), cy.toDouble(), ax.toDouble(), ay.toDouble())
	fun bezierCurveTo(cx1: Int, cy1: Int, cx2: Int, cy2: Int, ax: Int, ay: Int) = bezierCurveTo(cx1.toDouble(), cy1.toDouble(), cx2.toDouble(), cy2.toDouble(), ax.toDouble(), ay.toDouble())
	fun arcTo(x1: Int, y1: Int, x2: Int, y2: Int, radius: Int) = arcTo(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble(), radius.toDouble())

	fun moveTo(p: Vector2) = moveTo(p.x, p.y)
	fun lineTo(p: Vector2) = lineTo(p.x, p.y)
	fun quadraticCurveTo(c: Vector2, a: Vector2) = quadraticCurveTo(c.x, c.y, a.x, a.y)
	fun bezierCurveTo(c1: Vector2, c2: Vector2, a: Vector2) = bezierCurveTo(c1.x, c1.y, c2.x, c2.y, a.x, a.y)
	fun arcTo(p1: Vector2, p2: Vector2, radius: Double) = arcTo(p1.x, p1.y, p2.x, p2.y, radius)

	fun rect(x: Int, y: Int, width: Int, height: Int) = rect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
	fun strokeRect(x: Int, y: Int, width: Int, height: Int) = strokeRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
	fun fillRect(x: Int, y: Int, width: Int, height: Int) = fillRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

	fun arc(x: Double, y: Double, r: Double, start: Double, end: Double) {
		state.path.arc(x, y, r, start, end)
	}

	fun strokeDot(x: Double, y: Double) {
		beginPath()
		moveTo(x, y)
		lineTo(x, y)
		stroke()
	}

	fun arcTo(x1: Double, y1: Double, x2: Double, y2: Double, r: Double) {
		state.path.arcTo(x1, y1, x2, y2, r)
	}

	fun circle(x: Double, y: Double, radius: Double) = arc(x, y, radius, 0.0, Math.PI * 2.0)

	fun moveTo(x: Double, y: Double) {
		state.path.moveTo(x, y)
	}

	fun lineTo(x: Double, y: Double) {
		state.path.lineTo(x, y)
	}

	fun quadraticCurveTo(cx: Double, cy: Double, ax: Double, ay: Double) {
		state.path.quadTo(cx, cy, ax, ay)
	}

	fun bezierCurveTo(cx1: Double, cy1: Double, cx2: Double, cy2: Double, x: Double, y: Double) {
		state.path.cubicTo(cx1, cy1, cx2, cy2, x, y)
	}

	fun rect(x: Double, y: Double, width: Double, height: Double) {
		state.path.rect(x, y, width, height)
	}

	fun roundRect(x: Double, y: Double, w: Double, h: Double, rx: Double, ry: Double = rx) {
		if (rx == 0.0 && ry == 0.0) {
			rect(x, y, w, h)
		} else {
			// @TODO: radiusX
			val r = if (w < 2 * rx) w / 2.0 else if (h < 2 * rx) h / 2.0 else rx
			this.beginPath();
			this.moveTo(x + r, y);
			this.arcTo(x + w, y, x + w, y + h, r);
			this.arcTo(x + w, y + h, x, y + h, r);
			this.arcTo(x, y + h, x, y, r);
			this.arcTo(x, y, x + w, y, r);
			this.closePath();
		}
	}

	fun path(path: GraphicsPath) {
		this.state.path.write(path)
	}

	fun draw(d: Drawable) {
		d.draw(this)
	}

	fun strokeRect(x: Double, y: Double, width: Double, height: Double) {
		beginPath()
		rect(x, y, width, height)
		stroke()
	}

	fun fillRect(x: Double, y: Double, width: Double, height: Double) {
		beginPath()
		rect(x, y, width, height)
		fill()
	}

	fun beginPath() {
		state.path = GraphicsPath()
	}

	fun closePath() {
		state.path.close()
	}

	fun stroke() {
		if (state.strokeStyle != None) renderer.render(state, fill = false)
	}

	fun fill() {
		if (state.fillStyle != None) renderer.render(state, fill = true)
	}

	fun fillStroke() {
		fill()
		stroke()
	}

	fun clip() {
		state.clip = state.path
	}

	fun createLinearGradient(x0: Double, y0: Double, x1: Double, y1: Double) = LinearGradient(x0, y0, x1, y1)
	fun createRadialGradient(x0: Double, y0: Double, r0: Double, x1: Double, y1: Double, r1: Double) = RadialGradient(x0, y0, r0, x1, y1, r1)
	fun createColor(color: Int) = Color(color)
	val none = None

	data class Font(val name: String, val size: Double)

	data class TextMetrics(val bounds: Rectangle = Rectangle()) {
	}

	fun getTextBounds(text: String, out: TextMetrics = TextMetrics()): TextMetrics = out.apply {
		renderer.getBounds(font, text, out)
	}

	fun fillText(text: String, x: Double, y: Double): Unit = renderText(text, x, y, fill = true)
	fun strokeText(text: String, x: Double, y: Double): Unit = renderText(text, x, y, fill = false)

	fun renderText(text: String, x: Double, y: Double, fill: Boolean): Unit {
		renderer.renderText(state, font, text, x, y, fill)
	}

	interface Paint

	object None : Paint

	data class Color(val color: Int) : Paint

	abstract class Gradient(
		val stops: ArrayList<Double> = arrayListOf<Double>(),
		val colors: ArrayList<Int> = arrayListOf<Int>()
	) : Paint {
		fun addColorStop(stop: Double, color: Int): Gradient {
			stops += stop
			colors += color
			return this
		}

		abstract fun applyMatrix(m: Matrix2d): Gradient
	}

	class LinearGradient(val x0: Double, val y0: Double, val x1: Double, val y1: Double, stops: ArrayList<Double> = arrayListOf(), colors: ArrayList<Int> = arrayListOf()) : Gradient(stops, colors) {
		override fun applyMatrix(m: Matrix2d): Gradient = LinearGradient(
			m.transformX(x0, y0),
			m.transformY(x0, y0),
			m.transformX(x1, y1),
			m.transformY(x1, y1),
			ArrayList(stops),
			ArrayList(colors)
		)

		override fun toString(): String = "LinearGradient($x0, $y0, $x1, $y1, $stops, $colors)"
	}

	class RadialGradient(val x0: Double, val y0: Double, val r0: Double, val x1: Double, val y1: Double, val r1: Double, stops: ArrayList<Double> = arrayListOf(), colors: ArrayList<Int> = arrayListOf()) : Gradient(stops, colors) {
		override fun applyMatrix(m: Matrix2d): Gradient = RadialGradient(
			m.transformX(x0, y0),
			m.transformY(x0, y0),
			r0,
			m.transformX(x1, y1),
			m.transformY(x1, y1),
			r1,
			ArrayList(stops),
			ArrayList(colors)
		)

		override fun toString(): String = "RadialGradient($x0, $y0, $r0, $x1, $y1, $r1, $stops, $colors)"
	}

	interface Drawable {
		fun draw(c: Context2d)
	}

	interface SizedDrawable : Drawable {
		val width: Int
		val height: Int
	}

	class FuncDrawable(val action: Context2d.() -> Unit) : Context2d.Drawable {
		override fun draw(c: Context2d) {
			c.keep {
				action(c)
			}
		}
	}
}

