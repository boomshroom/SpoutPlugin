/*
 * This file is part of SpoutPlugin.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutPlugin is licensed under the GNU Lesser General Public License.
 *
 * SpoutPlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spoutapi.inventory;

import org.bukkit.inventory.ItemStack;

import org.getspout.spoutapi.material.item.GenericCustomTool;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.material.Tool;

public class SpoutItemStack extends ItemStack {
	public SpoutItemStack(int typeId, int amount, short data) {
		super(typeId, amount, data);
		Material m = getMaterial();
		if(m instanceof GenericCustomTool) {
			if(!getEnchantments().containsKey(SpoutEnchantment.MAX_DURABILITY)) addUnsafeEnchantment(SpoutEnchantment.MAX_DURABILITY, ((Tool) m).getMaxDurability());
			if(!getEnchantments().containsKey(SpoutEnchantment.DURABILITY)) addUnsafeEnchantment(SpoutEnchantment.DURABILITY, 0);
		}
	}
	
	public SpoutItemStack(ItemStack item) {
		this(item.getTypeId(), item.getAmount(), (short) item.getDurability());
		addUnsafeEnchantments(item.getEnchantments());
	}

	public SpoutItemStack(int typeId) {
		this(typeId, 1, (short) 0);
	}

	public SpoutItemStack(int typeId, short data) {
		this(typeId, 1, data);
	}

	public SpoutItemStack(CustomItem item) {
		this(item.getRawId(), 1, (short) item.getRawData());
	}

	public SpoutItemStack(CustomItem item, int amount) {
		this(item.getRawId(), amount, (short) item.getRawData());
	}

	public SpoutItemStack(CustomBlock block) {
		this(block.getBlockItem());
	}

	public SpoutItemStack(CustomBlock block, int amount) {
		this(block.getBlockItem(), amount);
	}

	public SpoutItemStack(Material material) {
		this(material.getRawId(), 1, (short) material.getRawData());
	}

	public SpoutItemStack(Material material, int amount) {
		this(material.getRawId(), amount, (short) material.getRawData());
	}

	public Material getMaterial() {
		return MaterialData.getMaterial(this.getTypeId(), this.getDurability());
	}

	public boolean isCustomItem() {
		return getMaterial() instanceof CustomItem;
	}
}
