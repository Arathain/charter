package net.arathain.charter.components;

import net.arathain.charter.Charter;
import net.arathain.charter.block.CharterStoneBlock;
import net.arathain.charter.block.PactPressBlock;
import net.arathain.charter.block.WaystoneBlock;
import net.arathain.charter.block.entity.PactPressBlockEntity;
import net.arathain.charter.item.ContractItem;
import net.arathain.charter.util.CharterUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CharterComponent implements SendHelpComponent {
	private BlockPos charterStonePos;
	private UUID owner;
	private final World world;
	private Map<UUID, BlockPos> pactVessels = new HashMap<>();

	public CharterComponent(World newWorld) {
		world = newWorld;
	}

	public CharterComponent(BlockPos charterStone, PlayerEntity owner, World world) {
		this.charterStonePos = charterStone;
		this.owner = owner.getUuid();
		this.world = world;
		this.area.add(Box.of(Vec3d.of(charterStone), 65, 65, 65));
		this.members.add(this.owner);
	}
	public void killCharter() {
		getAreas().forEach(area -> {
			BlockState state = world.getBlockState(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z));
			if(state.getBlock() instanceof WaystoneBlock) {
				world.breakBlock(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z), false);
				world.setBlockState(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z), Charter.BROKEN_WAYSTONE.getDefaultState());
			}
			if(state.getBlock() instanceof CharterStoneBlock) {
				world.breakBlock(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z), false);
				world.setBlockState(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z), Charter.BROKEN_CHARTER_STONE.getDefaultState());
			}
				});
		getMembers().forEach(member -> {
			PlayerEntity player = world.getPlayerByUuid(member);
			if(player != null) {
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 5));
			}
		});
		CharterComponents.CHARTERS.get(world).getCharters().removeIf(charter -> charter.equals(this));
	}

	public void tick() {
		BlockState upState = world.getBlockState(charterStonePos.offset(Direction.UP));
		if (upState.getBlock() != Blocks.AIR && upState.getBlock() != Blocks.WATER) {
			world.breakBlock(charterStonePos.offset(Direction.UP), true);
		}
		List<BlockPos> list = new ArrayList<>();

		for(int i=1;i<=4;i++){
			list.add(charterStonePos.add(2, 0, 2).offset(Direction.NORTH, i-1));
			list.add(charterStonePos.add(-2, 0, 2).offset(Direction.EAST, i-1));
			list.add(charterStonePos.add(-2, 0, -2).offset(Direction.SOUTH, i-1));
			list.add(charterStonePos.add(2, 0, -2).offset(Direction.WEST, i-1));
		}
		list.forEach(potentialVesselPos -> {
			BlockState potentialVesselState = world.getBlockState(potentialVesselPos);
			if (potentialVesselState.getBlock() instanceof PactPressBlock && potentialVesselState.get(Properties.LIT)) {
				PactPressBlockEntity press = (PactPressBlockEntity) world.getBlockEntity(potentialVesselPos);
				assert press != null;
				if(!press.getItems().isEmpty() && ContractItem.isViable(press.getContract())) {
					UUID newPotentialUuid = ContractItem.getIndebtedUUID(press.getContract());
					List<UUID> membr = new ArrayList<>(members);
					Optional<UUID> uuid = membr.stream().filter(existingUuid -> existingUuid == newPotentialUuid).findFirst();
					if (uuid.isEmpty() && !uuid.equals(owner)) {
						members.add(newPotentialUuid);
						pactVessels.put(newPotentialUuid, potentialVesselPos);
					}


				}

			}
		});
		List<UUID> memberList3 = new ArrayList<>(members);
		for(UUID member : memberList3) {
			PlayerEntity player = world.getPlayerByUuid(member);
			if (player != null && Objects.equals(CharterUtil.getCharterAtPos(player.getPos(), player.world), this)) {
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1000));
			}
		}
		List<UUID> memberList = new ArrayList<>(members);
		for(UUID member : memberList) {
			if (!(member.equals(owner))) {
				Map<UUID, BlockPos> vessels = new HashMap<>(pactVessels);
				if (vessels.get(member) == null || !(world.getBlockState(vessels.get(member)).getBlock() instanceof PactPressBlock) || !(world.getBlockState(vessels.get(member)).get(Properties.LIT)) || memberList.stream().filter(existingUuid -> existingUuid == member).count() > 1) {
					members.remove(member);
					pactVessels.remove(member);
					System.out.println("removed UUID for invalid");
				} else {
					PactPressBlockEntity press = (PactPressBlockEntity) world.getBlockEntity(vessels.get(member));
					if (press == null || press.getItems().isEmpty() || !(ContractItem.isViable(press.getContract())) || !Objects.equals(ContractItem.getIndebtedUUID(press.getContract()), member)) {
						members.remove(member);
						pactVessels.remove(member);
						System.out.println("removed UUID for not being there");
					}

				}
			}
		}

		List<UUID> memberList2 = new ArrayList<>(members);
		for(UUID member : memberList2) {
			PlayerEntity player = world.getPlayerByUuid(member);
			if (player != null && Objects.equals(CharterUtil.getCharterAtPos(player.getPos(), player.world), this)) {
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1000));
			}
			assert player != null;
			System.out.println(player.getName().asString());
		}



	}

	public BlockPos getCharterStonePos() {
		return charterStonePos;
	}

	public UUID getCharterOwnerUuid() {
		return owner;
	}

	public void writeToNbt(@NotNull NbtCompound tag) {
		NbtCompound rootTag = new NbtCompound();
		NbtList areaListTag = new NbtList();
		NbtList memberListTag = new NbtList();
		NbtList pactVesselListTag = new NbtList();


		rootTag.putUuid("CharterOwner", owner);
		rootTag.put("CharterStonePos", NbtHelper.fromBlockPos(charterStonePos));

		List<Box> areas = new ArrayList<>(area);
		for(Box box : areas) {
			NbtCompound boxCompound = new NbtCompound();

			boxCompound.put("Center", NbtHelper.fromBlockPos(new BlockPos(box.getCenter().x, box.getCenter().y, box.getCenter().z)));
			boxCompound.putDouble("LengthX", box.getXLength());
			boxCompound.putDouble("LengthY", box.getYLength());
			boxCompound.putDouble("LengthZ", box.getZLength());
			areaListTag.add(boxCompound);
		}
		Map<UUID, BlockPos> vessels = new HashMap<>(pactVessels);

		List<UUID> membrs = new ArrayList<>(members);
		for(UUID member : membrs) {
			if (!member.equals(owner) && vessels.get(member) != null) {
				NbtCompound pactVessel = new NbtCompound();
				pactVessel.put("Pos", NbtHelper.fromBlockPos(vessels.get(member)));
				pactVessel.putUuid("Uuid", member);
				pactVesselListTag.add(pactVessel);
			}
			memberListTag.add(NbtHelper.fromUuid(member));
		}

		rootTag.put("CharterArea", areaListTag);
		rootTag.put("CharterVessels", pactVesselListTag);
		rootTag.put("CharterMembers", memberListTag);
		tag.put(Charter.MODID, rootTag);
	}

	public List<Box> getAreas() {
		return area;
	}

	public void addArea(Box newArea){
		area.add(newArea);
	}
	public void addWaystone(BlockPos pos) {
		Vec3d vecPos = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
		Box box = Box.of(vecPos, 33, 33, 33);
		addArea(box);
	}

	public List<UUID> getMembers() {
		return members;
	}

	public void readFromNbt(NbtCompound tag) {
		NbtCompound rootTag = tag.getCompound(Charter.MODID);
		NbtList areaListTag = rootTag.getList("CharterArea", NbtElement.COMPOUND_TYPE);
		NbtList memberListTag = rootTag.getList("CharterMembers", NbtElement.INT_ARRAY_TYPE);
		NbtList charterVesselListTag = rootTag.getList("CharterVessels", NbtElement.COMPOUND_TYPE);
		area.clear();
		members.clear();
		pactVessels.clear();

		owner = rootTag.getUuid("CharterOwner");
		charterStonePos = NbtHelper.toBlockPos(rootTag.getCompound("CharterStonePos"));

		for(NbtElement boxElement : areaListTag) {
			NbtCompound boxCompound = (NbtCompound) boxElement;
			area.add(Box.of(Vec3d.of(NbtHelper.toBlockPos(boxCompound.getCompound("Center"))), boxCompound.getDouble("LengthX"), boxCompound.getDouble("LengthY"), boxCompound.getDouble("LengthZ")));
		}
		for(NbtElement vesselElement : charterVesselListTag) {
			NbtCompound vessel = (NbtCompound) vesselElement;
			pactVessels.put(vessel.getUuid("Uuid"), NbtHelper.toBlockPos(vessel.getCompound("Pos")));
		}


		Map<UUID, BlockPos> vessels = new HashMap<>(pactVessels);
		for(NbtElement member : memberListTag) {
			members.add(NbtHelper.toUuid(member));
		}
	}
}
