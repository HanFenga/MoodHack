package dev.hanfeng.zhebushigudu.xufangggg;

import dev.hanfeng.zhebushigudu.xufangggg.manager.*;
import dev.hanfeng.zhebushigudu.xufangggg.notification.NotificationsManager;
import dev.hanfeng.zhebushigudu.xufangggg.util.IconUtil;
import dev.hanfeng.zhebushigudu.xufangggg.util.Title;
import net.minecraft.util.Util;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Mod(modid = "mood", name = "Mood", version = "0.1")
public class OyVey {
    public static final String MODID = "mood";
    public static final String MODNAME = "Mood";
    public static final String MODVER = "0.1";
    public static final String ID = "0.1";
    public static final Logger LOGGER = LogManager.getLogger("Mood");
    public static CommandManager commandManager;
    public static NotificationsManager notificationsManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static PacketManager packetManager;
    public static ColorManager colorManager;
    public static HoleManager holeManager;
    public static InventoryManager inventoryManager;

    public static TargetManager targetManager;
    public static PotionManager potionManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static SpeedManager speedManager;
    public static ReloadManager reloadManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    @Mod.Instance
    public static OyVey INSTANCE;
    private static boolean unloaded;

    static {
        unloaded = false;
    }

    public SystemTray tray = SystemTray.getSystemTray();

    public static void load() {
        LOGGER.info("\n\nLoading OyVey by Alpha432");
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
        notificationsManager=new NotificationsManager();
        targetManager=new TargetManager();
        textManager = new TextManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        rotationManager = new RotationManager();
        packetManager = new PacketManager();
        eventManager = new EventManager();
        speedManager = new SpeedManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        serverManager = new ServerManager();
        fileManager = new FileManager();
        colorManager = new ColorManager();
        positionManager = new PositionManager();
        configManager = new ConfigManager();
        holeManager = new HoleManager();
        LOGGER.info("Managers loaded.");
        moduleManager.init();
        LOGGER.info("Modules loaded.");
        configManager.init();
        eventManager.init();
        LOGGER.info("EventManager loaded.");
        textManager.init(true);
        moduleManager.onLoad();
        LOGGER.info("OyVey successfully loaded!\n");
    }

    public static void unload(boolean unload) {
        LOGGER.info("\n\nUnloading OyVey by Alpha432");
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        OyVey.onUnload();
        notificationsManager=null;
        targetManager=null;
        eventManager = null;
        friendManager = null;
        speedManager = null;
        holeManager = null;
        positionManager = null;
        rotationManager = null;
        configManager = null;
        commandManager = null;
        colorManager = null;
        serverManager = null;
        fileManager = null;
        potionManager = null;
        inventoryManager = null;
        moduleManager = null;
        textManager = null;
        LOGGER.info("OyVey unloaded!\n");
    }
    public static Image image = Toolkit.getDefaultToolkit().createImage(OyVey.class.getResource("/assets/minecraft/textures/icon-32x.png"));
    public static TrayIcon trayIcon = new TrayIcon(image, "Auto Queue");
    public static void reload() {
        OyVey.unload(false);
        OyVey.load();
    }

    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(OyVey.configManager.config.replaceFirst("Mood/", ""));
            moduleManager.onUnloadPost();
            unloaded = true;
        }
    }
    

    @Mod.EventHandler
    public static void preInit() {
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Welcome Thanks for using KKGod" + OyVey.ID + "!!!");
        try {
            INSTANCE.tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        trayIcon.displayMessage("KKHack Thank you for your use", "Welcome Thanks for using KKGod" + OyVey.ID + "!", TrayIcon.MessageType.NONE);
        LOGGER.info("slol lives in vancouver canada and his name is jacob ward");;
    }

    public static void postInit() {
        MinecraftForge.EVENT_BUS.register(new Title());

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (Util.getOSType() != Util.EnumOS.OSX) {
            try (InputStream inputStream16x = OyVey.class.getResourceAsStream("/assets/minecraft/textures/icon-16x.png");
                 InputStream inputStream32x = OyVey.class.getResourceAsStream("/assets/minecraft/textures/icon-32x.png")) {
                ByteBuffer[] icons = new ByteBuffer[]{IconUtil.INSTANCE.readImageToBuffer(inputStream16x), IconUtil.INSTANCE.readImageToBuffer(inputStream32x)};
                Display.setIcon(icons);
            } catch (Exception e) {
                OyVey.LOGGER.error("Couldn't set Windows Icon", e);
            }
        }

        OyVey.load();
    }
}

