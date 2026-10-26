# TFC Perfect Blacksmith

## About
Project is a mod for modern TerraFirmaCraft versions(1.20.1+), which aims to reduce friction caused by forging items with an anvil.

Anvil is an integral mechanic of TerraFirmaCraft, especially  at early stages, but I've found the anvil tedious work to find out the right sequence of actions and then write it down somewhere. So to address it, I've created a mod, that displays hints in the game chat, which inform the player what action next he needs to be taken in order to get closer to finish forging an item in the least amount of steps required, effectively making perfectly forged items most of the time. Additionally It's user friendly: If player makes a mistake, the solver immediately finds a new most optimal sequence and shows it to player.

The implementation utilizes Breadth First Search to find locally optimal step, which leads to the global optimum. The solver takes into account the need for a sequence to end with specified sub-sequence of steps.

## Building & installation
To build the project use gradle:
```shell
./gradlew build
```

To install the mod, first you need Minecraft, TerraFirmaCraft, MinecraftForge and It's dependencies. Currently only MC version 1.20.1 is supported. Newer versions might not work.
