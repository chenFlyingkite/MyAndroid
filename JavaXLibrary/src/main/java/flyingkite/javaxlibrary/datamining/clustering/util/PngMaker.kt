package flyingkite.javaxlibrary.datamining.clustering.util

import flyingkite.files.FileUtil
import flyingkite.tool.TicTac2
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

object PngMaker {
    private val mClock = TicTac2()
    val colors = arrayOf(
            Color(255,   0,   0)
            , Color(  0, 255,   0)
            , Color(  0,   0, 255)
            , Color(255, 255,   0)
            , Color(255,   0, 255)
            , Color(  0, 255, 255)
            , Color(128,   0,   0)
            , Color(  0, 128,   0)
            , Color(  0,   0, 128)
            , Color(128, 128,   0)
    )

    fun getColorModed(i: Int): Color {
        return colors[i % colors.size]
    }

    fun loadImage(f: File): BufferedImage? {
        // Step : Load image by ImageIO
        var img: BufferedImage? = null
        try {
            img = ImageIO.read(f)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return img
    }

    fun _png(name: String): String {
        if (!name.endsWith(".png")) {
            return "$name.png"
        }
        return name
    }

    fun into(img: BufferedImage?, file: File) {
        FileUtil.ensureDelete(file)
        FileUtil.createFile(file)

        //mClock.tic()
        // Step : Write to output
        try {

            ImageIO.write(img, "png", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //mClock.tac("ImageIO write >> " + file.absolutePath)
    }
}