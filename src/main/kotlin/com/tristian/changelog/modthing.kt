package com.tristian.changelog

import com.tristian.changelog.gui.Changelog
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

//i am wayyyy too lazy to actually add this to a client to test it.
//so this is a driver class.
@Mod(
    modid = modthing.MOD_ID,
    name = modthing.MOD_NAME,
    version = modthing.VERSION,
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter"
)
object modthing {
    const val MOD_ID = "minecraft-forge-kotlin-template"
    const val MOD_NAME = "Minecraft Forge Kotlin Template"
    const val VERSION = "2019.1-1.2.23"
    val CHANGELOG = Changelog()

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    fun preinit(event: FMLPreInitializationEvent) {






    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    fun postinit(event: FMLPostInitializationEvent) {
    }

    @SubscribeEvent
    fun onKey(event: TickEvent.ClientTickEvent) {
        if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
            CHANGELOG.render()
        }
    }


}
