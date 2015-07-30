package org.zeroturnaround.javarebel.integration.minecraft;

import org.zeroturnaround.javarebel.*;
import org.zeroturnaround.javarebel.integration.minecraft.cpb.*;
import org.zeroturnaround.javarebel.integration.minecraft.cpb.forge.GameDataCPB;

public class MinecraftPlugin implements Plugin {
  private static final boolean REROUTE_BLOCKS = ConfigurationFactory.getInstance().getBoolean("rebel.minecraft.reroute_blocks");
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

    //Proof-of-concept
    if (REROUTE_BLOCKS) {
      i.addIntegrationProcessor(cl,"net.minecraft.block.Block",
          new BlockCPB());
      i.addIntegrationProcessor(cl,"net.minecraftforge.fml.common.registry.GameData",
          new GameDataCPB());
      ReloaderFactory.getInstance().addClassReloadListener(new BlockClassEventListener());
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
