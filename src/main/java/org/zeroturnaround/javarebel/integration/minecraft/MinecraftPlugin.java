package org.zeroturnaround.javarebel.integration.minecraft;

import org.zeroturnaround.javarebel.*;
import org.zeroturnaround.javarebel.integration.minecraft.cpb.*;
import org.zeroturnaround.javarebel.integration.minecraft.cpb.forge.GameDataCPB;
import org.zeroturnaround.javarebel.integration.minecraft.cpb.forge.ModelLoaderCPB;

public class MinecraftPlugin implements Plugin {
  private static final boolean DISABLE_SAVE = ConfigurationFactory.getInstance().getBoolean("rebel.minecraft.disable_save");
  private static final boolean PROXY_STUFF = ConfigurationFactory.getInstance().getBoolean("rebel.minecraft.proxy");

  public void preinit() {
    Integration i = IntegrationFactory.getInstance();
    ClassLoader cl = MinecraftPlugin.class.getClassLoader();
    i.addIntegrationProcessor(cl, "net.minecraft.launchwrapper.LaunchClassLoader",
        new LauncherClassLoaderCBP());
    i.addIntegrationProcessor(cl, "net.minecraft.client.resources.SimpleReloadableResourceManager",
        new SimpleReloadableResourceManagerCBP());
    i.addIntegrationProcessor(cl,"net.minecraft.client.resources.FileResourcePack",
        new FileResourcePackCPB());
    i.addIntegrationProcessor(cl,"net.minecraft.client.Minecraft",
        new MinecraftCPB());

    if (PROXY_STUFF) {
      i.addIntegrationProcessor(cl,"net.minecraftforge.fml.common.registry.GameData",
          new GameDataCPB());
      i.addIntegrationProcessor(cl,"net.minecraft.block.state.BlockState",
          new BlockStateCPB());
      i.addIntegrationProcessor(cl,"net.minecraft.block.Block",
          new BlockCPB());
      i.addIntegrationProcessor(cl,"net.minecraft.item.ItemBlock",
          new ItemBlockCPB());
      i.addIntegrationProcessor(cl,"net.minecraftforge.client.model.ModelLoader",
          new ModelLoaderCPB());
      i.addIntegrationProcessor(cl,"net.minecraft.util.RegistryNamespaced",
          new RegistryNamespacedCPB());
      ReloaderFactory.getInstance().addClassReloadListener(new BlockClassEventListener());
    }

    if (DISABLE_SAVE) {
      i.addIntegrationProcessor(cl,"net.minecraft.world.WorldServer",
          new WorldServerCPB());
    }
  }

  public boolean checkDependencies(ClassLoader classLoader, ClassResourceSource classResourceSource) {
    return classResourceSource.getClassResource("net.minecraft.launchwrapper.LaunchClassLoader") != null;
  }

  public String getId() {
    return "minecraft_plugin";
  }

  public String getName() {
    return "JRebel Minecraft Plugin";
  }

  public String getDescription() {
    return "Reload stuff.";
  }

  public String getAuthor() {
    return "Henri Viik";
  }

  public String getWebsite() {
    return null;
  }

  public String getSupportedVersions() {
    return null;
  }

  public String getTestedVersions() {
    return "1.8-11.14.3.1450";
  }
}
