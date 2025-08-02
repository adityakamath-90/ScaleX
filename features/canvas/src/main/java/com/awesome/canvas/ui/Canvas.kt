//package com.awesome.canvas.ui
//
//import android.graphics.Canvas
//import android.graphics.Color
//import kotlinx.coroutines.flow.map
//import java.nio.file.Files.size
//
//@Composable
//fun StrokeTracker() {
//
//    var strokes by rememberSaveable(stateSaver = NormalizedStrokesSaver) {
//        mutableStateOf(emptyList<NormalizedStroke>())
//    }
//
//    var currentStroke by remember { mutableStateOf<NormalizedStroke?>(null) }
//    var canvasSize by remember { mutableStateOf(Size.Zero) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .pointerInput(Unit) {
//                detectDragGestures(
//                    onDragStart = { offset ->
//                        if (canvasSize.width == 0f || canvasSize.height == 0f) return@detectDragGestures
//                        val aspectRatio = canvasSize.width / canvasSize.height
//                        currentStroke = NormalizedStroke(
//                            points = mutableListOf(
//                                NormalizedOffset(
//                                    offset.x / canvasSize.width,
//                                    offset.y / canvasSize.height
//                                )
//                            ),
//                            aspectRatio = aspectRatio
//                        )
//                    },
//                    onDrag = { _, dragAmount ->
//                        val last = currentStroke?.points?.lastOrNull()
//                        if (last != null && canvasSize.width != 0f && canvasSize.height != 0f) {
//                            currentStroke?.points?.add(
//                                NormalizedOffset(
//                                    last.x + (dragAmount.x / canvasSize.width),
//                                    last.y + (dragAmount.y / canvasSize.height)
//                                )
//                            )
//                        }
//                    },
//                    onDragEnd = {
//                        currentStroke?.let {
//                            strokes = strokes + it
//                        }
//                        currentStroke = null
//                    }
//                )
//            }
//    ) {
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            if (size.width == 0f || size.height == 0f) return@Canvas
//            canvasSize = size
//
//            fun NormalizedOffset.toOffset(originalAspectRatio: Float): Offset {
//                val currentAspectRatio = size.width / size.height
//
//                val scale: Float
//                val offsetX: Float
//                val offsetY: Float
//
//                if (currentAspectRatio > originalAspectRatio) {
//                    // Canvas is wider than original
//                    scale = size.height
//                    val contentWidth = scale * originalAspectRatio
//                    offsetX = (size.width - contentWidth) / 2f
//                    offsetY = 0f
//                } else {
//                    // Canvas is taller than original
//                    scale = size.width
//                    val contentHeight = scale / originalAspectRatio
//                    offsetX = 0f
//                    offsetY = (size.height - contentHeight) / 2f
//                }
//
//                return Offset(x * scale + offsetX, y * scale + offsetY)
//            }
//
//            // Draw saved strokes
//            strokes.forEach { stroke ->
//                val path = Path().apply {
//                    stroke.points.firstOrNull()?.toOffset(stroke.aspectRatio)?.let { moveTo(it.x, it.y) }
//                    stroke.points.drop(1).forEach {
//                        val point = it.toOffset(stroke.aspectRatio)
//                        lineTo(point.x, point.y)
//                    }
//                }
//                drawPath(path, color = Color.Black, style = Stroke(width = 4f))
//            }
//
//            // Draw in-progress stroke
//            currentStroke?.let { stroke ->
//                val path = Path().apply {
//                    stroke.points.firstOrNull()?.toOffset(stroke.aspectRatio)?.let { moveTo(it.x, it.y) }
//                    stroke.points.drop(1).forEach {
//                        val point = it.toOffset(stroke.aspectRatio)
//                        lineTo(point.x, point.y)
//                    }
//                }
//                drawPath(path, color = Color.Gray, style = Stroke(width = 4f))
//            }
//        }
//    }
//}
//
//data class NormalizedStroke(
//    val points: MutableList<NormalizedOffset>,
//    val aspectRatio: Float
//)
//
//data class NormalizedOffset(
//    val x: Float,
//    val y: Float
//)
//
//val NormalizedStrokesSaver: Saver<List<NormalizedStroke>, Any> = listSaver(
//    save = { strokes ->
//        strokes.map { stroke ->
//            listOf(stroke.aspectRatio) + stroke.points.flatMap { listOf(it.x, it.y) }
//        }
//    },
//    restore = { list ->
//        list.map { flatList ->
//            val aspectRatio = flatList[0]
//            val points = flatList.drop(1).chunked(2).map { coords ->
//                NormalizedOffset(coords[0], coords[1])
//            }
//            NormalizedStroke(points.toMutableList(), aspectRatio)
//        }
//    }
//)
//
//
//@Composable
//@Preview
//fun StrokeDesignPreview() {
//    StrokeTracker()
//}