package me.ddayo.cvmod

import com.mojang.blaze3d.systems.RenderSystem
import me.ddayo.cvmod.client.gui.CvUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.*
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL21.*
import java.io.File
import java.io.FileInputStream

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Cvmod.MOD_ID)
class Cvmod {
    companion object {
        const val MOD_ID = "cvmod"
        private val logger = LogManager.getLogger()

        init {
            //System.loadLibrary("opencv_videoio_ffmpeg455_64")
            //System.loadLibrary("opencv_world455")
            //System.loadLibrary("cvmod")
            System.load(File(Minecraft.getInstance().gameDir, "opencv_videoio_ffmpeg455_64.dll").canonicalPath)
            System.load(File(Minecraft.getInstance().gameDir, "opencv_world455.dll").canonicalPath)
            System.load(File(Minecraft.getInstance().gameDir, "cvmod.dll").canonicalPath)
        }
    }
    init {
        FMLJavaModLoadingContext.get().modEventBus.register(object {
            @SubscribeEvent
            fun setup(event: FMLCommonSetupEvent) {
                logger.info(CvUtil.loadVideo("assets/asdf.mp4"))
                Minecraft.getInstance().gameSettings.framerateLimit = 60
            }

            @SubscribeEvent
            fun doClientStuff(event: FMLClientSetupEvent) {
                RenderSystem.recordRenderCall {

                }
            }
        })

        MinecraftForge.EVENT_BUS.register(object {
            var tex = -1
            lateinit var aid: SoundSource
            var current = System.currentTimeMillis()

            @SubscribeEvent
            fun asdf(event: RenderGameOverlayEvent.Post) {
                if(Minecraft.getInstance().isGamePaused) return
                if(event.type == RenderGameOverlayEvent.ElementType.ALL) {
                    val width = Minecraft.getInstance().mainWindow.scaledWidth
                    val height = Minecraft.getInstance().mainWindow.scaledHeight

                    Thread.sleep(0)
                    if(tex == -1) {
                        tex = glGenTextures()
                        logger.info(tex)
                        glBindTexture(GL_TEXTURE_2D, tex)
                        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
                        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
                        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
                        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, CvUtil.getImageWidth(), CvUtil.getImageHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, CvUtil.getMatrix())
                        glBindTexture(GL_TEXTURE_2D, 0)

                        val method = SoundSource::class.java.getDeclaredMethod("allocateNewSource")
                        method.isAccessible = true
                        val source = method.invoke(null) as SoundSource
                        val stream = OggAudioStream(FileInputStream(File(Minecraft.getInstance().gameDir, "asdf.ogg")))
                        source.bindBuffer(AudioStreamBuffer(stream.readOggSound(), stream.audioFormat))
                        source.setGain(1.0f)
                        source.play()

                        current = System.currentTimeMillis()
                    }
                    else {
                        glPushMatrix()
                        run {
                            glLoadIdentity()
                            glTranslated(0.0, 0.0, -2000.0)

                            glBindTexture(GL_TEXTURE_2D, tex)
                            val ptr = CvUtil.setMillisecond(System.currentTimeMillis() - current)
                            if(ptr == 0L) return@run
                            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, CvUtil.getImageWidth(), CvUtil.getImageHeight(), GL_RGBA, GL_UNSIGNED_BYTE, ptr)
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
                }
            }
        })
    }
}