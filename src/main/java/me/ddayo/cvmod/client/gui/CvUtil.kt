package me.ddayo.cvmod.client.gui

object CvUtil {
    external fun loadImage(name: String): Long
    external fun loadVideo(name: String): Long
    external fun nextFrame(): Long
    external fun getImageWidth(): Int
    external fun getImageHeight(): Int
    external fun getImageChannels(): Int
    external fun isVideoFinished(): Boolean
    external fun setFrame(pos: Long): Long
    external fun setMillisecond(pos: Long): Long
    external fun getMatrix(): Long
}