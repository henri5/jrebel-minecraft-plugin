#JRebel Minecraft Plugin


**If having any issues with this plugin, don't contact JRebel nor Minecraft support - fix it yourself.**

Tested with Minecraft 1.8 & Forge 11.14.3.1450

###Features:
* fixes bug with using JRebel Legacy core
* loads mod's resources from the project workspace
* reload textures during runtime
* detect new referenced resources during runtime


To use the plugin

1. get [JRebel](https://zeroturnaround.com/software/jrebel/)
2. build the plugin as `mvn clean package`
3. add JRebel and plugin to JVM arguments as `-javaagent:/path/to/jrebel.jar -Drebel.plugins=/path/to/jr-minecraft-plugin-x.y.z.jar`

If using Forge, place the arguments inside `build.gradle` `runClient` conf as
```
runClient {
    jvmArgs '-javaagent:/path/to/jrebel.jar', '-Drebel.plugins=/path/tojr-minecraft-plugin-x.y.z.jar'
}
```

When setting up `rebel.xml`, make sure that first element of classpath points to directory that contains the `assets` folder, where you update the textures.
Start up Minecraft, get into a world, change texture or json and save it - it automatically detects that a resource was changed and will invoke reload for the resourcepackage (takes couple of seconds)

Some reload handlers in MC take bit unnecessary amount of time, so you can skip them with `rebel.minecraft.ignore_reload_handlers` JVM argument. E.g `SoundHandler` takes nearly 40% of reload time, now you can skip it as `-Drebel.minecraft.ignore_reload_handlers=net.minecraft.client.audio.SoundHandler`, and you can add multiple classes there, separated by a comma as `-Drebel.minecraft.ignore_reload_handlers=foo.Bar,foo.Baz`.
