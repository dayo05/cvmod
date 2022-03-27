package me.ddayo.cvmod.client.gui

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.NativeImage
import net.minecraft.util.text.StringTextComponent
import org.apache.logging.log4j.LogManager

class TestGui: Screen(StringTextComponent.EMPTY) {
    val logger = LogManager.getLogger()

    init {
        CvUtil.loadImage("assets/sans.png")
        logger.info(CvUtil.getImageWidth())
        logger.info(CvUtil.getImageHeight())
        logger.info(CvUtil.getImageChannels())
    }

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {

    }
}