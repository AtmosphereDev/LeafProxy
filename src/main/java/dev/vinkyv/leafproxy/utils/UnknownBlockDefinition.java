package dev.vinkyv.leafproxy.utils;

import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;
import org.cloudburstmc.protocol.common.DefinitionRegistry;

public class UnknownBlockDefinition implements DefinitionRegistry<BlockDefinition> {

	@Override
	public BlockDefinition getDefinition(int runtimeId) {
		return new UnknownDefinition(runtimeId);
	}

	@Override
	public boolean isRegistered(BlockDefinition blockDefinition) {
		return true;
	}

	record UnknownDefinition(int runtimeId) implements BlockDefinition {

		@Override
		public int getRuntimeId() {
			return runtimeId;
		}
	}
}
