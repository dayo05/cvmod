package me.ddayo.cvmod.client.gui

import java.nio.ByteBuffer

object CvUtil {
    external fun loadImage(name: String): Long
    external fun getImageWidth(): Int
    external fun getImageHeight(): Int
    external fun getImageChannels(): Int
    external fun getBmpImage(): Long
    external fun loadVideo(name: String): Long
    external fun nextFrame(): Long
}