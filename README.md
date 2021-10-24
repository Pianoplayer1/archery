# Archery plugin (Halloweeny Challenge #4)
Archery plugin based on [this](https://docs.google.com/document/d/1BvM50rv8V-TnLTUsF9yHfovdmLkfES13rjgWyu5vxtM/edit) document.

Please read through the text below for all the additional features I have implemented!
 
## General features
* 100% multiplayer compatible (scores, bossbar, settings etc. are stored for each player individually)
* Easy to navigate with full chat autocompletion and clickable chat links to switch between modes
* Extensive config file, all major chat messages can be customised
* Maintainable and expandable with structured & commented code
 
## Archery practice mode
* Functions just as stated in the document, `/archery` to begin or exit
* You can toggle where to display scores (actionBar, bossBar, scoreboard) in the config file
* The scoreboard additionally provides the durations it took the player to hit each target
* Personal highscore system based on the total time to finish

## Archery fun mode
* Enable or disable and configure with `/archery fun`
* All arrows shot from any bow will be replaced by custom configurable projectile(s)
* If the player has no bow / arrow, they will be given a standard bow / arrow which they can keep
* Infinity "enchantment" for all arrows shot in archery mode

  ### Configure possibilities
  Syntax: `/archery fun <property> <value>`
  
  Property | Meaning | Possible values
  -------- | ------- | ---------------
  amount | The number of projectiles to fire simultaneously | Integer from 1 to 100
  fire | Whether the projectiles should burn | true \| false
  projectile | The type of projectile to fire | arrow \| egg \| enderpearl \| fireball \| snowball
  riding | Whether the player should ride a fired projectile | true \| false
