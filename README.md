The plugin completely resolves 
the entity lag of versions 1.14/1.15/1.16/1.17/1.18!

Has no method been able to solve entity lag so far?
Use this plugin!

이 플러그인은 버전 1.14/1.15/1.16/1.17/1.18의 엔티티 랙을 완전히 해결합니다!

#### How does it works?
Minecraft tracks a lot of entities, 
even if they are outside the tracking range of the player,
 that's a normal behavior but is a tps killer for 1.14.4 to 1.18 
servers with more than 30 players. So what this plugin do is 
untrack those entities every configured ticks and track them 
again if the player is near.

#### 어떻게 작동합니까?
마인크래프트는 플레이어의 추적 범위를 벗어나더라도
 많은 실체를 추적하는데, 이는 정상적인 행동이지만 
30명 이상의 플레이어를 보유한 1.14.4~1.18 서버의 경우 
tps를 매우 잡아먹습니다. 따라서 이 플러그인은 설정된 
모든 틱을 추적 해제하고 플레이어가 근처에 있을 경우 다시 추적합니다.

```
#Clearlag Plugin
#Last Reload Date : 2022-01-04 22:59:19



# Should we notify the console when we interact with entities?
# - Default value(s): false
log-to-console: false


# How low should the server's TPS be before we do anything?
# - Note: Setting this value above 20 will skip this check, allowing the tasks to run 24/7.
# - Default value(s): 19.5
tps-limit: 19.5


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



version: 1.3.1
```
