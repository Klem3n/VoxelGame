package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.world.World;
import com.mygdx.game.world.chunk.ChunkData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveUtils {
    private static final String SAVE_DIR = "./saves/";

    public static void saveWorld(World world) throws IOException {
        File directory = new File(SAVE_DIR);

        if (!directory.exists()){
            directory.mkdirs();
        }

        DataOutputStream os = new DataOutputStream(new FileOutputStream(SAVE_DIR + "save.dat"));
        os.writeInt(world.getWorldSeed());
        os.writeFloat(world.getPlayer().getPosition().x);
        os.writeFloat(world.getPlayer().getPosition().y);
        os.writeFloat(world.getPlayer().getPosition().z);

        saveChunkData(world, os);

        os.close();
    }

    private static void saveChunkData(World world, DataOutputStream os) throws IOException {
        List<ChunkData> chunksToSave = new ArrayList<>();

        ChunkData.CHUNK_CACHE.forEach(entry -> {
            ChunkData chunk = entry.value;

            if(chunk.getChangedVoxelIndexes().isEmpty()){
               return;
            }

            chunksToSave.add(chunk);
        });

        os.writeInt(chunksToSave.size());

        for (ChunkData chunkData : chunksToSave) {
            os.writeFloat(chunkData.getChunkPosition().x);
            os.writeFloat(chunkData.getChunkPosition().y);
            os.writeFloat(chunkData.getChunkPosition().z);

            os.writeInt(chunkData.getChangedVoxelIndexes().size);
            for (Integer changedVoxel : chunkData.getChangedVoxelIndexes()) {
                os.writeInt(changedVoxel);
                os.writeByte(chunkData.getVoxels()[changedVoxel]);
            }
        }
    }

    public static int WORLD_SEED;
    public static Vector3 PLAYER_POSITION = null;

    public static boolean loadWorld() throws IOException {
        File saveFile = new File(SAVE_DIR + "save.dat");

        if(!saveFile.exists()){
            return false;
        }

        DataInputStream is = new DataInputStream(new FileInputStream(saveFile));

        WORLD_SEED = is.readInt();

        PLAYER_POSITION = new Vector3(is.readFloat(), is.readFloat(), is.readFloat());

        int chunkSize = is.readInt();

        for (int i = 0; i < chunkSize; i++) {
            Vector3 chunkPosition = new Vector3(is.readFloat(), is.readFloat(), is.readFloat());

            ChunkData chunkData = new ChunkData(chunkPosition);

            int changedVoxels = is.readInt();

            for (int j = 0; j < changedVoxels; j++) {
                int voxelHash = is.readInt();
                byte voxel = is.readByte();

                chunkData.getChangedVoxelIndexes().add(voxelHash);
                chunkData.getChangedVoxels().add(voxel);
            }

            ChunkData.CHUNK_CACHE.put(chunkPosition, chunkData);
        }
        
        is.close();

        return true;
    }
}
