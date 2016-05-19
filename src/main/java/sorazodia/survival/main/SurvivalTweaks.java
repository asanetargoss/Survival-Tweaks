package sorazodia.survival.main;

import static sorazodia.survival.main.SurvivalTweaks.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.logging.log4j.Logger;

import sorazodia.survival.config.ConfigHandler;
import sorazodia.survival.mechanics.BlockBreakEvent;
import sorazodia.survival.mechanics.EnderEvent;
import sorazodia.survival.mechanics.EntityTickEvent;
import sorazodia.survival.mechanics.PlayerActionEvent;
import sorazodia.survival.mechanics.PlayerSleepEvent;
import sorazodia.survival.server.command.CommandDimensionTeleport;
import sorazodia.survival.server.command.DimensionChecker;

@Mod(name = NAME, version = VERSION, modid = MODID, guiFactory = GUI_FACTORY)
public class SurvivalTweaks
{
	public static final String MODID = "survivalTweaks";
	public static final String VERSION = "2.2.0";
	public static final String NAME = "Survival Tweaks";
	public static final String GUI_FACTORY = "sorazodia.survival.config.ConfigGUIFactory";

	private static ConfigHandler configHandler;
	private static Logger log;
	private static float soundLevel = 0;

	@EventHandler
	public void serverStart(FMLServerStartingEvent preServerEvent)
	{
		preServerEvent.registerServerCommand(new CommandDimensionTeleport());
		DimensionChecker.clear();
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent serverInitEvent)
	{
		MinecraftServer server = MinecraftServer.getServer();

		if (Loader.isModLoaded("Mystcraft") || Loader.isModLoaded("rftools"))
			if (server.getCommandManager().getCommands().containsKey(CommandDimensionTeleport.getName()))
				server.getCommandManager().getCommands().remove(CommandDimensionTeleport.getName());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent preEvent)
	{
		log = preEvent.getModLog();
		log.info("Syncing config and registering events");
		configHandler = new ConfigHandler(preEvent);
		
		MinecraftForge.EVENT_BUS.register(new PlayerActionEvent());
		MinecraftForge.EVENT_BUS.register(new EnderEvent());
		MinecraftForge.EVENT_BUS.register(new EntityTickEvent());
		MinecraftForge.EVENT_BUS.register(new BlockBreakEvent());
		MinecraftForge.EVENT_BUS.register(new DimensionChecker());
		MinecraftForge.EVENT_BUS.register(new PlayerSleepEvent());
		MinecraftForge.EVENT_BUS.register(configHandler);

		log.info("Mod Loaded");
	}

	public static Logger getLogger()
	{
		return log;
	}
	
	//From http://stackoverflow.com/questions/237159/whats-the-best-way-to-check-to-see-if-a-string-represents-an-integer-in-java
	public static boolean isInteger(String arg)
	{
		if (arg == null)
			return false;

		int length = arg.length();

		if (length == 0)
			return false;

		int x = 0;

		if (arg.charAt(0) == '-')
		{
			if (length == 1)
				return false;
			x = 1;
		}

		for (; x < length; x++)
		{
			char c = arg.charAt(x);
			if (c <= '/' || c >= ':')
				return false;
		}

		return true;
	}

	public static void playSound(String name, World world, EntityPlayer player)
	{
		if (world.isRemote)
			soundLevel = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS);
		
		player.playSound(name, soundLevel, soundLevel);
	}

}
