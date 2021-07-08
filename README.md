# Discord-Link 2
Handles gamechats on the DirtCraft Discord.

Requires:
- ~~LuckPerms~~
- Sponge-Discord-Lib
- UltimateChat

Todo:
 - Integrate some parts into  Sponge-Discord-Lib, or merge the two (Like how we do it with spigot/bungee?)
 - Create an API and separate from implementation, so plugins can abstractly use it, and random class changes won't break shit.
 - promote enable/disable/whitelist (indv. servers)
 
Changelog
  - 2.0.0
    - TODO: command schema for editing config values? (Bungee-Style). JDA Status, etc.
    - new API for other plugins, Rather then directly accessing the implementation plugins such as dirt-listener etc. can now use an API.
    - Big ass refactor for the entire API to function correctly. Lot less static methods, More managers. Roles are no longer hard coded.
    - Overhaul of how the plugin loads. Now it will load using a callback. JDA will try to connect every 2.5 minutes until a connection is gained. 
     This means when the server loses connection during boot up discord link should be able to initialize. Previously if JDA took longer then 5
     minutes the entire plugin would crash.
  - 1.5.1, 1.5.2
    - added mute handling (ignores messages / deletes them)
    - added mute commands !mute add, !mute remove, !mute info
    - fixed kits and ranks only accepting uuid.
  - 1.5.0
    - Moved chat handlers to use scheduler
    - Allowed bot commands in DMs
    - !kits
    - !prefix 
    - !logs command
    - in game /prefix as well.
    - General refactor
    - Made the repo a lot more easier to merge cross-versions.
    - Made Sponge-Discord-Lib lazy-loadable before init. DL now lazy loads it, allowing DL to load on PRE, before AFTER_PRE.
    - Overhaul of the sender system, and wrapped console senders. 
    - Overhaul of how discord commands send their responses.
    - made !ranks in LP API 4.0 identical to API 5.0
    - made discord link set the ranks on join
    - added a packet handler for re-configuring ranks on discord.
    - !list now shows prefix and highlights staff.
    - track promotion handler support for bungee module
    - !version command
    - bot can now verify users via DM  or message in gamechat (will delete/ignore message and DM them)
    - ability to grab server log channel from discord link.

  - 1.4.0
    - Updated to new JDA 4.2.0 release
    - General Codebase Refactors
    - Made command output nicer (Especially !help)
    - Added DM command parsing (Currently only does console commands ATM, logged.)
    - Added game construction event to tracked boot stages (boot embeds)
    - No longer initializes if JDA cannot be loaded, or any of the other deps.
    - Made only notify, help, shutdown commands can run pre-boot.
    - Made main class extend boot handler to stop bogus accusations of poor programming
    - Made message delete calls exceptionless to reduce log noise.
    - Stopped storing Role, Channels, Guild etc. Store long id's instead.
    - Made bot DM me on exception.
    - All commands delete the input message now
    - Added a changelog.
  
  - 1.3.0
    - !halt command - nukes the server
    - !unlink command - removes user from verified database
    - !sync command - /lp sync for admins
    - !ranks command - shows ranks
    - Load plugin at the earliest possible stage to allow for boot cancellation in the event of a thread-lock.
    - Add server watchdog, DMs listed people if a game-stage exceeds the time threshold. Fully customizable.
    - Add boot stage embeds so people can see the server boot in realtime.
    - Support for upcoming proxy module
    - Hide vanished players from leave/join messages, !list
    - New command feedback system, Stacks messages and sends all at once, eliminating embed spam and instead fills each embed before sending.
    
  - Prior
    - check git history pleb. I can't be bothered going back further, changelog starts from here.