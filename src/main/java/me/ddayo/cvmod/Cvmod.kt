package me.ddayo.cvmod

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import io.netty.buffer.ByteBufInputStream
import me.ddayo.cvmod.client.gui.CvUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.NativeImage
import net.minecraft.resources.*
import net.minecraft.resources.data.IMetadataSectionSerializer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent.PlayerTickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil
import org.opencv.core.Mat
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.nio.ByteOrder
import java.util.function.Predicate
import org.lwjgl.opengl.GL21.*

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Cvmod.MOD_ID)
class Cvmod {
    companion object {
        const val MOD_ID = "cvmod"
        private val logger = LogManager.getLogger()

        init {
            System.loadLibrary("opencv_videoio_ffmpeg455_64")
            System.loadLibrary("opencv_world455")
            System.loadLibrary("cvmod")
        }
    }
    init {
        FMLJavaModLoadingContext.get().modEventBus.register(object {
            @SubscribeEvent
            fun setup(event: FMLCommonSetupEvent) {
                //logger.info(CvUtil.loadImage("assets/sans.png"))
                logger.info(CvUtil.loadVideo("assets/sans.mp4"))
            }

            @SubscribeEvent
            fun doClientStuff(event: FMLClientSetupEvent) {
                RenderSystem.recordRenderCall {

                }
            }
        })

        MinecraftForge.EVENT_BUS.register(object {
            var tex = -1

            @SubscribeEvent
            fun asdf(event: GuiScreenEvent.DrawScreenEvent.Post) {
                val width = event.gui.width
                val height = event.gui.height

                if(tex == -1) {
                    tex = glGenTextures()
                    logger.info(tex)
                    glBindTexture(GL_TEXTURE_2D, tex)
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, CvUtil.getImageWidth(), CvUtil.getImageHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, CvUtil.getBmpImage())
                    glBindTexture(GL_TEXTURE_2D, 0)
                }
                else {
                    glPushMatrix()
                    run {
                        glLoadIdentity()
                        glTranslated(0.0, 0.0, -2000.0)

                        glBindTexture(GL_TEXTURE_2D, tex)
                        //glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, CvUtil.getImageWidth(), CvUtil.getImageHeight(), GL_RGBA, GL_UNSIGNED_BYTE, CvUtil.getBmpImage())
                        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, CvUtil.getImageWidth(), CvUtil.getImageHeight(), GL_RGBA, GL_UNSIGNED_BYTE, CvUtil.nextFrame())
                        glBegin(GL_QUADS)

                        glTexCoord2d(1.0, 0.0)
                        glVertex2i(width, 0)
                        glTexCoord2d(0.0, 0.0)
                        glVertex2i(0, 0)
                        glTexCoord2d(0.0, 1.0)
                        glVertex2i(0, height)
                        glTexCoord2d(1.0, 1.0)
                        glVertex2i(width, height)
                        glEnd()
                    }
                    glBindTexture(GL_TEXTURE_2D, 0)
                    glEnable(GL_DEPTH_TEST)
                    glPopMatrix()
                }
                //logger.info(Minecraft.getInstance().textureManager.getTexture(ResourceLocation("assets", "sans.png"))!!.glTextureId)
                //Minecraft.getInstance().textureManager.bindTexture(ResourceLocation("asdf", "asdf"))
                //logger.info(Minecraft.getInstance().textureManager.getTexture(ResourceLocation("asdf", "asdf"))!!.glTextureId)
                //Minecraft.getInstance().textureManager.bindTexture(ResourceLocation("assets", "sans.png"))
            }

            @SubscribeEvent
            fun onServerStarting(event: FMLServerStartingEvent) {

            }
        })
    }
}