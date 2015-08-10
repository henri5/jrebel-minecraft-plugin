#JRebel Minecraft Plugin

Tested with Minecraft 1.8 & Forge 11.14.3.1450

###Features:
* fixes bug with using JRebel Legacy core (`java.lang.NoClassDefFoundError: com/zeroturnaround/javarebel/gen/RebelLocator$$1`)
* loads mod's resources from the project workspace as per [rebel.xml](http://manuals.zeroturnaround.com/jrebel/standalone/config.html)
* automatically triggers reload of textures & json during runtime
* **Experimental** - with JVM argument `-Drebel.minecraft.proxy=true` and as long the block has no-parameter constructor with all block initialization inside (so no chaining setters), the plugin is able to reflect some changes during runtime (e.g. block hardness, in which tab the block is located in, yadda-yadda). This is done by using some proxy magic. As usual, you can override methods defined in Block class with JRebel and see the changes without any plugin-related wizardry.
* disable Minecraft from automatically saving with `-Drebel.minecraft.disable_save=true` JVM argument


To use the plugin

1. get [JRebel](https://zeroturnaround.com/software/jrebel/) - as long you're using it for non-commercial purposes, you can use free [myJRebel](https://my.jrebel.com/) license
2. build the plugin as `mvn clean package` using [Maven](https://maven.apache.org/)
3. add JRebel and plugin to JVM arguments as `-javaagent:/path/to/jrebel.jar -Drebel.plugins=/path/to/jr-minecraft-plugin-6.2.2.jar`

If using Forge, place the arguments inside `build.gradle` `runClient` conf as
```
runClient {
    jvmArgs '-javaagent:/path/to/jrebel.jar', '-Drebel.plugins=/path/to/jr-minecraft-plugin-6.2.2.jar'
}
```
####NB!
When setting up `rebel.xml`, make sure that first element of classpath points to directory that contains the `assets` folder, where you update the textures.
Start up Minecraft, get into a world, change texture or json and save it - it automatically detects that a resource was changed and will invoke reload for the resourcepackage (takes couple of seconds)

#####Tips & Tweaks

* `-Drebel.minecraft.skip_reload_handlers` - accepts comma-separated names of classes (e.g. `com.Foo,com.Bar`). When resource reloading occurs, handlers of these classes are not notified to speed up the reload process. Can cut the reload time nearly in half by skipping `SoundHandler` as `-Drebel.minecraft.skip_reload_handlers=net.minecraft.client.audio.SoundHandler`. Add it to jvmArgs.
* Press P while holding down F3 to disable game entering pause menu whenever it loses focus.

![](https://raw.githubusercontent.com/henri5/jr-minecraft-plugin/master/plugin_in_action.gif)

