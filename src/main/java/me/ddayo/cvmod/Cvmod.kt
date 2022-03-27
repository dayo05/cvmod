package me.ddayo.cvmod

import com.mojang.blaze3d.matrix.MatrixStack
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
import java.util.function.Consumer
import java.util.function.Predicate

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Cvmod.MOD_ID)
class Cvmod {
    companion object {
        const val MOD_ID = "cvmod"
        private val logger = LogManager.getLogger()

        init {
            System.loadLibrary("opencv_world455")
            System.loadLibrary("cvmod")
        }
    }

    init {
        val manager = Minecraft.getInstance().resourceManager as SimpleReloadableResourceManager
        manager.addResourcePack(object : IResourcePack {
            override fun close() {}

            override fun getRootResourceStream(fileName: String): InputStream {
                logger.info("RRS")
                val arr = ByteArray(CvUtil.getImageChannels() * CvUtil.getImageHeight() * CvUtil.getImageWidth())
                CvUtil.getImageByteBuffer().get(arr)
                return ByteArrayInputStream(arr)
            }

            override fun getResourceStream(type: ResourcePackType, location: ResourceLocation): InputStream {
                logger.info("RS")
                val arr = ByteArray(CvUtil.getImageChannels() * CvUtil.getImageHeight() * CvUtil.getImageWidth())
                CvUtil.getImageByteBuffer().order(ByteOrder.nativeOrder()).get(arr)
                return ByteArrayInputStream(arr)
            }

            override fun getAllResourceLocations(type: ResourcePackType, namespaceIn: String, pathIn: String, maxDepthIn: Int, filterIn: Predicate<String>): MutableCollection<ResourceLocation> {
                logger.info("all location")
                return listOf(ResourceLocation("asdf", "asdf")).toMutableList()
            }

            override fun resourceExists(type: ResourcePackType, location: ResourceLocation): Boolean {
                logger.info("exists")
                return true
            }

            override fun getResourceNamespaces(type: ResourcePackType): MutableSet<String> {
                logger.info("namespace")
                return setOf("asdf").toMutableSet()
            }

            override fun <T : Any?> getMetadata(deserializer: IMetadataSectionSerializer<T>): T? {
                logger.info("metadata")
                return null
            }

            override fun getName(): String {
                return "asdf"
            }
        })
        manager.addResourcePack(object: FolderPack(File(Minecraft.getInstance().gameDir, "assets")) {

            override fun getAllResourceLocations(type: ResourcePackType, namespaceIn: String, pathIn: String, maxDepthIn: Int, filterIn: Predicate<String>): MutableCollection<ResourceLocation> {
                val x =  super.getAllResourceLocations(type, namespaceIn, pathIn, maxDepthIn, filterIn)
                for(k in x)
                    logger.info("${k.namespace} ${k.path}")
                return x
            }
        })

        FMLJavaModLoadingContext.get().modEventBus.register(object {
            @SubscribeEvent
            fun setup(event: FMLCommonSetupEvent) {
                logger.info(CvUtil.loadImage("assets/sans.png"))
            }

            @SubscribeEvent
            fun doClientStuff(event: FMLClientSetupEvent) {
                RenderSystem.recordRenderCall {

                }
            }
        })

        MinecraftForge.EVENT_BUS.register(object {
            val nctor = NativeImage::class.java.getDeclaredConstructor(NativeImage.PixelFormat::class.java, Int::class.java, Int::class.java, Boolean::class.java, Long::class.java)
            init {
                nctor.isAccessible = true
            }

            @SubscribeEvent
            fun asdf(event: GuiScreenEvent.DrawScreenEvent.Post) {
                val width = event.gui.width
                val height = event.gui.height

                //logger.info(Minecraft.getInstance().textureManager.getTexture(ResourceLocation("assets", "sans.png"))!!.glTextureId)
                Minecraft.getInstance().textureManager.bindTexture(ResourceLocation("asdf", "asdf"))
                logger.info(Minecraft.getInstance().textureManager.getTexture(ResourceLocation("asdf", "asdf"))!!.glTextureId)
                //Minecraft.getInstance().textureManager.bindTexture(ResourceLocation("assets", "sans.png"))
                GL11.glPushMatrix()
                run {
                    GL11.glEnable(GL11.GL_TEXTURE_2D)
                    GL11.glEnable(GL11.GL_BLEND)
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                    GL11.glTexCoord2d(1.0, 0.0)
                    GL11.glVertex2i(width, 0)
                    GL11.glTexCoord2d(0.0, 0.0)
                    GL11.glVertex2i(0, 0)
                    GL11.glTexCoord2d(0.0, 1.0)
                    GL11.glVertex2i(0, height)
                    GL11.glTexCoord2d(1.0, 1.0)
                    GL11.glVertex2i(width, height)
                }
                GL11.glPopMatrix()
            }

            @SubscribeEvent
            fun onServerStarting(event: FMLServerStartingEvent) {

            }
        })
    }
}