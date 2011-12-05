/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spoutapi.chunkstore;

import gnu.trove.map.hash.TLongObjectHashMap;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.getspout.spoutapi.chunkdatamanager.ChunkDataManager;
import org.getspout.spoutapi.io.FlatFileStore;
import org.getspout.spoutapi.util.UniqueItemStringMap;

public class SimpleChunkDataManager implements ChunkDataManager {
	
	private ChunkStore chunkStore = new ChunkStore();
	
	private HashMap<UUID, TLongObjectHashMap<ChunkMetaData>> chunkMetaDataLoaded = new HashMap<UUID,TLongObjectHashMap<ChunkMetaData>>();
	
	private HashMap<UUID, WorldGlobalItemMapConverter> worldItemMapConverters = new HashMap<UUID, WorldGlobalItemMapConverter>();
	
	public void closeAllFiles() {
		chunkStore.closeAll();
	}

	public ChunkMetaData loadChunk(Chunk c) {
		return loadChunk(c.getWorld(), c.getX(), c.getZ());
	}
	
	public ChunkMetaData loadChunk(World world, int x, int z) {
		
		ChunkMetaData md = getMetaData(world, x, z, true, true);
		return md;

	}
	
	public boolean loadWorldChunks(World w) {
		
		Chunk[] chunks = w.getLoadedChunks();
		
		boolean loaded = false;
		
		for (Chunk c : chunks) {
			loaded |= loadChunk(c) != null;
		}
		
		return loaded;
		
	}
	
	public boolean loadAllChunks() {
		List<World> worlds = Bukkit.getServer().getWorlds();
		
		boolean loaded = false;
		
		for (World w : worlds) {
			loaded |= loadWorldChunks(w);
		}
		
		return loaded;	
	}
	
	public boolean saveChunk(Chunk c) {
		return saveChunk(c.getWorld(), c.getX(), c.getZ());
	}
		
	public boolean saveChunk(World w, int x, int z) {
		
		ChunkMetaData md = getMetaData(w, x, z, false, false);
		
		if (md != null) {
			chunkStore.writeChunkMetaData(w, x, z, md);
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean unloadWorldChunks(World world) {
		return saveWorldChunks(world, true);
	}
	
	public boolean saveWorldChunks(World world) {
		return saveWorldChunks(world, false);
	}
	
	public boolean saveWorldChunks(World world, boolean unload) {
		boolean unloaded = false;
		synchronized(chunkMetaDataLoaded) {
			TLongObjectHashMap<ChunkMetaData> worldChunks = chunkMetaDataLoaded.get(world.getUID());
		
			if (worldChunks == null) {
				return false;
			}
			
			Collection<ChunkMetaData> chunks = worldChunks.valueCollection();
			
			for (ChunkMetaData md : chunks) {
				unloaded |= saveChunk(world, md.getChunkX(), md.getChunkZ());
			}
			
			worldChunks.clear();
		}
		return unloaded;
	}
	
	public boolean unloadAllChunks() {
		List<World> worlds = Bukkit.getServer().getWorlds();
		
		boolean unloaded = false;

		for(World world : worlds) {
			unloaded |= unloadWorldChunks(world);
		}
		
		return unloaded;
	}

	@Override
	public int getStringId(String string) {
		return UniqueItemStringMap.getId(string);
	}

	@Override
	public Serializable setBlockData(String id, World world, int x, int y, int z, Serializable data) {
		
		ChunkMetaData md = getMetaData(world, x >> 4, z >> 4, true, true);
		
		return md.putBlockData(id, x, y, z, data);
		
	}

	@Override
	public Serializable getBlockData(String id, World world, int x, int y, int z) {
		
		ChunkMetaData md = getMetaData(world, x >> 4, z >> 4, true, false);
		
		if (md == null) {
			return null;
		}
		
		return md.getBlockData(id, x, y, z);
	}


	public Serializable removeBlockData(String id, World world, int x, int y, int z) {
		
		ChunkMetaData md = getMetaData(world, x >> 4, z >> 4, true, false);
		
		if (md == null) {
			return null;
		}
		
		return md.removeBlockData(id, x, y, z);
	}

	@Override
	public Serializable setChunkData(String id, World world, int x, int z, Serializable data) {

		ChunkMetaData md = getMetaData(world, x, z, true, true);
		
		return md.putChunkData(id, data);
	}
	
	@Override
	public Serializable getChunkData(String id, World world, int x, int z) {

		ChunkMetaData md = getMetaData(world, x, z,  true, false);
		
		if (md == null) {
			return null;
		}
		
		return md.getChunkData(id);
	}

	@Override
	public Serializable removeChunkData(String id, World world, int x, int z) {

		ChunkMetaData md = getMetaData(world, x, z, true, false);
		
		if (md == null) {
			return null;
		}
		
		return md.removeChunkData(id);
	}
	
	@Override
	public short[] getCustomBlockIds(World world, int x, int z) {
		ChunkMetaData md = getMetaData(world, x, z, true, false);
		
		if (md == null) {
			return null;
		}
		
		return md.getCustomBlockIds();
	}

	@Override
	public void setCustomBlockIds(World world, int x, int z, short[] ids) {
		ChunkMetaData md = getMetaData(world, x, z, true, true);
		
		md.setCustomBlockIds(ids);
	}
	
	private ChunkMetaData getMetaData(World world, int x, int z, boolean load, boolean loadOrCreate) {
		long key = (((long)x)<<32) | (((long)z) & 0xFFFFFFFFL);
		UUID uid = world.getUID();
		ChunkMetaData md = null;
		synchronized(chunkMetaDataLoaded) {
			TLongObjectHashMap<ChunkMetaData> worldChunks = chunkMetaDataLoaded.get(uid);
			if (worldChunks == null) {
				worldChunks = new TLongObjectHashMap<ChunkMetaData>();
				chunkMetaDataLoaded.put(uid, worldChunks);
			}
			
			md = worldChunks.get(key);
			
			if (md == null && (load || loadOrCreate)) {
				try {
					md = chunkStore.readChunkMetaData(world, x, z);
					if (md != null) {
						if (!md.getWorldUID().equals(world.getUID()) || md.getChunkX() != x || md.getChunkZ() != z) {
							System.out.println("Expected: " + world.getUID() + " " + x + " " + z);
							System.out.println("Actual: " + md.getWorldUID() + " " + md.getChunkX() + " " + md.getChunkZ());
							throw new RuntimeException("Chunk meta data stored in wrong location");
						}
						md.setWorldItemMapConverter(getWorldItemMapConverter(world));
					}
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				
				if (md == null && loadOrCreate) {
					md = new ChunkMetaData(world.getUID(), getWorldItemMapConverter(world), x, z);
				}
				
				if (md != null) {
					worldChunks.put(key, md);
				}
			}
		}
		
		return md;
	}
	
	private WorldGlobalItemMapConverter getWorldItemMapConverter(World world) {
		UUID uid = world.getUID();
		WorldGlobalItemMapConverter worldItemMap = worldItemMapConverters.get(uid);
		if (worldItemMap == null) {
			File dir = new File(world.getWorldFolder(), "spout_meta");
			dir.mkdirs();
			
			FlatFileStore fs = new FlatFileStore(new File(dir, "worldItemMap.txt"));
			fs.load();
			worldItemMap = new WorldGlobalItemMapConverter(fs);
		}
		worldItemMapConverters.put(uid, worldItemMap);
		return worldItemMap;
	}
}

