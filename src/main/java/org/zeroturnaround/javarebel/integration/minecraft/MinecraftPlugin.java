/**
 * Copyright (C) 2010 ZeroTurnaround OU
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v2 as published by
 * the Free Software Foundation, with the additional requirement that
 * ZeroTurnaround OU must be prominently attributed in the program.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You can find a copy of GNU General Public License v2 from
 *   http://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.zeroturnaround.javarebel.integration.minecraft;

import org.zeroturnaround.javarebel.ClassResourceSource;
import org.zeroturnaround.javarebel.Integration;
import org.zeroturnaround.javarebel.IntegrationFactory;
import org.zeroturnaround.javarebel.Plugin;
import org.zeroturnaround.javarebel.integration.minecraft.cpb.FileResourcePackCPB;
import org.zeroturnaround.javarebel.integration.minecraft.cpb.LauncherClassLoaderCBP;
import org.zeroturnaround.javarebel.integration.minecraft.cpb.SimpleReloadableResourceManagerCBP;
import org.zeroturnaround.javarebel.integration.minecraft.cpb.TextureMapCPB;

public class MinecraftPlugin implements Plugin {

  public void preinit() {

    Integration i = IntegrationFactory.getInstance();
    ClassLoader cl = MinecraftPlugin.class.getClassLoader();
    i.addIntegrationProcessor(cl, "net.minecraft.launchwrapper.LaunchClassLoader", new LauncherClassLoaderCBP());
    i.addIntegrationProcessor(cl, "net.minecraft.client.resources.SimpleReloadableResourceManager", new SimpleReloadableResourceManagerCBP());
    i.addIntegrationProcessor(cl,"net.minecraft.client.resources.FileResourcePack", new FileResourcePackCPB());
    i.addIntegrationProcessor(cl,"net.minecraft.client.renderer.texture.TextureMap", new TextureMapCPB());
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
    return null;
  }

  public String getWebsite() {
    return null;
  }

  public String getSupportedVersions() {
    return null;
  }

  public String getTestedVersions() {
    return null;
  }
}
