

# L-Clearlag

The plugin completely resolves the entity lag of versions 1.14/1.15/1.16/1.17/1.18!

Has no method been able to solve entity lag so far?
Use this plugin!

이 플러그인은 버전 1.14/1.15/1.16/1.17/1.18/1.19의 엔티티 랙을 완전히 해결합니다!

### How does it works?
Minecraft tracks a lot of entities, even if they are outside the tracking range of the player, that's a normal behavior but is a tps killer for 1.14.4 to 1.19 servers with more than 30 players. So what this plugin do is untrack those entities every configured ticks and track them again if the player is near.

### 어떻게 작동합니까?
마인크래프트는 플레이어의 추적 범위를 벗어나더라도 많은 실체를 추적하는데, 이는 정상적인 행동이지만 30명 이상의 플레이어를 보유한 1.14.4~1.19 서버의 경우 tps를 매우 잡아먹습니다. 따라서 이 플러그인은 설정된 모든 틱을 추적 해제하고 플레이어가 근처에 있을 경우 다시 추적합니다.

```yaml
####################################################################################################
#                                                                                                  #
#                                       __          ___    _     __                                #
#                          |      ───  /  `  |     |__    /_\   |__)                               #
#                          |___        \__,  |___  |___  /   \  |  \                               #
#                                                                                                  #
#                                                         _    __                                  #
#                                                  |     /_\  / _                                  #
#                                                  |___ /   \ \__/                                 #
#                                                                                                  #
#                                                                                                  #
#                                                                                                  #
#                                                                                                  #
#                                       MiniLuca#7822                                              #
#                            Complete lag resolution in servers!                                   #
#                                  [ 1.14.4    -    1.19 ]                                         #
#                                                                                                  #
#  SPIGOT:      https://www.spigotmc.org/resources/l-clearlag.98464/                               #
# └ https://www.spigotmc.org/resources/entitytrackerfixer-fix-1-14-4-1-16-2-entitytick-lag.70902/  #
# └ https://www.spigotmc.org/resources/villager-optimiser-1-14-2-1-16-5.68517/                     #
#                                                                                                  #
####################################################################################################

# Should we notify the console when we interact with entities?
# - Default value(s): false
log-to-console: false


# Should we disable tick operations of un-tracked entities?
# Note 1: this option only works for +1.16.1 servers
# - Default value(s): true
disable-tick-for-untracked-entities: true


# How low should the server's TPS be before we do anything?
# - Note: Setting this value above 20 will skip this check, allowing the tasks to run 24/7.
# - Default value(s): 20.0
tps-limit: 20.0


# What entities should we ignore?
# - Default value(s):
#   - creeper
#   - villager
#   - ender_dragon
#   - armor_stand
ignore-entity-list:
  - creeper
  - villager
  - ender_dragon
  - armor_stand

# Should we ignore an entity with a name?
# - Default value(s): true
ignore-entity-name: true


# How far (in blocks) should we look for players near un-tracked entities?
# - Default value(s): 50
tracking-range: 50


# How often (in ticks) should we check for "lingering" entities?
# - Default value(s): 1000
untrack-ticks: 1000


# Should the plugin perform tasks on all worlds?
# Note: if this is set to true, the option "worlds" will be ignored
# - Default value(s): true
enable-on-all-worlds: true


# What worlds should we perform our tasks on?
# - Default value(s):
#   - world
#   - world_nether
#   - world_the_end
worlds:
  - world
  - world_nether
  - world_the_end

# Do you use a system to limit the number of entities per chunk?
# - Default value(s): false
chunk-entity-limit-enable: false

# Limits the number of entities per chunk.
# - Precaution: If set to pig:10, only 10 animals will be spawn per bundle.
# - Note : Set to all:10 and you can have up to 10 entities per chunk.
# Default value(s):
#  - all:70
entity-limit:
  - all:70



# Beta version
# https://www.spigotmc.org/resources/villager-optimiser-1-14-2-1-16-5.68517/
villager-enable: false
# - Default value(s): 600
ticks-per-allow-search: 600



version: 1.7.1
```

MiniLuca#7822

Plugin statistics (플러그인 통계)
https://bstats.org/plugin/bukkit/L-Clearlag/13638

So far, 100,000,000 entities have been removed!
지금까지 100,000,000 마리의 엔티티를 제거했습니다!

#minecraft entity lag fix

Report any bug in the discussion section.
