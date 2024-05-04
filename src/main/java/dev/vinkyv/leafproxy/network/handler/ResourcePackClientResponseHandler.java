package dev.vinkyv.leafproxy.network.handler;

import dev.vinkyv.leafproxy.Leaf;
import dev.vinkyv.leafproxy.LeafServer;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;

import java.util.List;
import java.util.UUID;

public class ResourcePackClientResponseHandler implements BedrockPacketHandler {
	private final LeafServer proxy;
	private final BedrockServerSession session;

	public ResourcePackClientResponseHandler(LeafServer proxy, BedrockServerSession session) {
		this.proxy = proxy;
		this.session = session;
	}

	public PacketSignal handle(ResourcePackClientResponsePacket packet) {
		BedrockCodec codec = this.session.getCodec();
		if (packet.getStatus() == ResourcePackClientResponsePacket.Status.HAVE_ALL_PACKS) {
			ResourcePackStackPacket resourcePackStackPacket = new ResourcePackStackPacket();
			resourcePackStackPacket.setForcedToAccept(false);
			resourcePackStackPacket.setGameVersion("*");
			session.sendPacketImmediately(resourcePackStackPacket);
			return PacketSignal.HANDLED;
		}
		if (packet.getStatus() == ResourcePackClientResponsePacket.Status.COMPLETED) {
			StartGamePacket startGamePacket = new StartGamePacket();
			startGamePacket.setUniqueEntityId(1); // Change to real one
			startGamePacket.setRuntimeEntityId(1); // Change to real one
			startGamePacket.setPlayerGameType(GameType.SURVIVAL); // Change to real one
			startGamePacket.setPlayerPosition(Vector3f.ZERO); // Change to real one
			startGamePacket.setDefaultSpawn(Vector3i.ZERO); // Change to real one
			startGamePacket.setRotation(Vector2f.ZERO); // Change to real one
			startGamePacket.setSeed(-777); // Move to config
			startGamePacket.setSpawnBiomeType(SpawnBiomeType.DEFAULT);
			startGamePacket.setCustomBiomeName("");
			startGamePacket.setDimensionId(0);
			startGamePacket.setBlockRegistryChecksum(0);
			startGamePacket.setLevelGameType(GameType.SURVIVAL); // Change to real one
			startGamePacket.setAchievementsDisabled(false);
			startGamePacket.setDayCycleStopTime(0);
			startGamePacket.setRainLevel(0f);
			startGamePacket.setLightningLevel(0f);
			startGamePacket.setDifficulty(1);
			startGamePacket.setEduFeaturesEnabled(false);
			startGamePacket.setEducationProductionId("");
			startGamePacket.setPlatformLockedContentConfirmed(false);
			startGamePacket.setMultiplayerGame(true);
			startGamePacket.setBroadcastingToLan(true);
			startGamePacket.setXblBroadcastMode(GamePublishSetting.PUBLIC); // Remove to not get fucked by Mojang
			startGamePacket.setPlatformBroadcastMode(GamePublishSetting.PUBLIC); // Remove to not get fucked by Mojang
			startGamePacket.setCommandsEnabled(true);
			startGamePacket.setTexturePacksRequired(false);
			startGamePacket.setExperimentsPreviouslyToggled(false);
			startGamePacket.setBonusChestEnabled(false);
			startGamePacket.setStartingWithMap(false);
			startGamePacket.setDefaultPlayerPermission(PlayerPermission.MEMBER); // Change to real one
			startGamePacket.setServerChunkTickRange(4); // Change to real one
			startGamePacket.setBehaviorPackLocked(false);
			startGamePacket.setResourcePackLocked(false);
			startGamePacket.setFromWorldTemplate(false);
			startGamePacket.setFromLockedWorldTemplate(false);
			startGamePacket.setOnlySpawningV1Villagers(false);
			startGamePacket.setVanillaVersion("*");
			startGamePacket.setLimitedWorldWidth(16); // Change to real one
			startGamePacket.setLimitedWorldHeight(16); // Change to real one
			startGamePacket.setNetherType(false);
			startGamePacket.setForceExperimentalGameplay(OptionalBoolean.empty());
			startGamePacket.setChatRestrictionLevel(ChatRestrictionLevel.NONE);
			startGamePacket.setDisablingPlayerInteractions(false);
			startGamePacket.setDisablingPersonas(false);
			startGamePacket.setDisablingCustomSkins(false);
			startGamePacket.setLevelId("Leaf");
			startGamePacket.setLevelName("Leaf"); // Move to config
			startGamePacket.setTrial(false);
			startGamePacket.setAuthoritativeMovementMode(AuthoritativeMovementMode.CLIENT);
			startGamePacket.setRewindHistorySize(0);
			startGamePacket.setServerAuthoritativeBlockBreaking(false);
			startGamePacket.setCurrentTick(0);
			startGamePacket.setEnchantmentSeed(0);
			startGamePacket.setItemDefinitions(List.of(ItemDefinition.AIR)); // Change to real one
			startGamePacket.setMultiplayerCorrelationId("");
			startGamePacket.setInventoriesServerAuthoritative(true);
			startGamePacket.setServerEngine("LeafProxy");
			startGamePacket.setPlayerPropertyData(NbtMap.EMPTY);
			startGamePacket.setPremiumWorldTemplateId("00000000-0000-0000-0000-000000000000");
			startGamePacket.setWorldTemplateId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
			startGamePacket.setWorldEditor(false);
			startGamePacket.setClientSideGenerationEnabled(false);
			startGamePacket.setEmoteChatMuted(false);
			startGamePacket.setBlockNetworkIdsHashed(true); // Maybe change
			startGamePacket.setCreatedInEditor(false);
			startGamePacket.setExportedFromEditor(false);
			startGamePacket.setTrustingPlayers(true);
			startGamePacket.setGeneratorId(1); // Change to real one
			startGamePacket.getGamerules().add((new GameRuleData<>("showcoordinates", true))); // Change to real one
			session.sendPacket(startGamePacket);

			//AvailableEntityIdentifiersPacket availableEntityIdentifiersPacket = new AvailableEntityIdentifiersPacket();
			//availableEntityIdentifiersPacket.setIdentifiers(NbtMap.EMPTY); // Change to real one
			//session.sendPacket(availableEntityIdentifiersPacket);

			//BiomeDefinitionListPacket biomeDefinitionListPacket = new BiomeDefinitionListPacket();
			//biomeDefinitionListPacket.setDefinitions(NbtMap.EMPTY); // Change to real one
			//session.sendPacket(biomeDefinitionListPacket);

			CreativeContentPacket creativeContentPacket = new CreativeContentPacket();
			creativeContentPacket.setContents(new ItemData[0]); // Change to real one
			session.sendPacket(creativeContentPacket);

			// TODO: Implemen packet
			//CraftingDataPacket craftingDataPacket = new CraftingDataPacket();
			// Add creafting data
			//craftingDataPacket.setCleanRecipes(true);
			//session.sendPacket(craftingDataPacket);

			PlayStatusPacket playStatus = new PlayStatusPacket();
			playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
			session.sendPacket(playStatus);

			new Thread(() -> {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				TextPacket textPacket = new TextPacket();
				textPacket.setSourceName("[§aLeafProxy§r]");
				textPacket.setXuid("");
				textPacket.setMessage("Welcome to §aLeafProxy§r!");
				textPacket.setType(TextPacket.Type.ANNOUNCEMENT);
				session.sendPacket(textPacket);
			}).start();

			new Thread(() -> {
				try {
					Thread.sleep(12000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				TransferPacket transferPacket = new TransferPacket();
				transferPacket.setAddress("bandomas.org");
				transferPacket.setPort(19132);
				session.sendPacket(transferPacket);
			}).start();

			return PacketSignal.HANDLED;
		}
		Leaf.getLogger().info("Bruh he lost packs "+packet.getStatus().toString());
		session.disconnect("Where is my packs?");
		return PacketSignal.HANDLED;
	}
}
