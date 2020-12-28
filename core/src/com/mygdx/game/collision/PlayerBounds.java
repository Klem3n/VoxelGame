package com.mygdx.game.collision;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Player;
import com.mygdx.game.block.BlockType;
import com.mygdx.game.block.WorldBlock;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.world.World;

public class PlayerBounds extends Bounds {
    private final Player player;
    private final Vector3 position;

    private final Box playerBox;

    public PlayerBounds(Player player, Vector3 position){
        this.position = position;
        this.player = player;

        playerBox = new Box(this.position, 0.3f, 2f, 0.3f);
    }

    @Override
    public void checkCollision(Vector3 velocity) {
        World world = World.INSTANCE;

        float x = velocity.x;
        float y = velocity.y;
        float z = velocity.z;

        BlockType blockType;

        float xOffset = x <= 0 ? x : playerBox.getWidth() + x;
        float yOffset = y <= 0 ? y : playerBox.getHeight() + y;
        float zOffset = z <= 0 ? z : playerBox.getDepth() + z;

        Vector3 blockPosition = Constants.floor(position.cpy().add(xOffset, 0, 0));

        if((blockType = world.get(blockPosition)) != null && collides(blockType, blockPosition)){
            //x = 0;
        }

        blockPosition = Constants.floor(position.cpy().add(0, yOffset, 0));

        if((blockType = world.get(blockPosition)) != null && collides(blockType, blockPosition)){
            y = 0;
        }

        blockPosition = Constants.floor(position.cpy().add(0, 0, zOffset));

        if((blockType = world.get(blockPosition)) != null && collides(blockType, blockPosition)){
            //z = 0;
        }

        velocity.set(x, 0, z);

        /*float x = velocity.x;
        float y = velocity.y;
        float z = velocity.z;

        float xOffset = x <= 0 ? x : playerBox.getWidth() + x;
        float yOffset = y <= 0 ? y : playerBox.getHeight() + y;
        float zOffset = z <= 0 ? z : playerBox.getDepth() + z;

        Vector3 blockPosition = Constants.floor(player.getPosition().cpy().add(xOffset, yOffset, zOffset));



        velocity.set(x, y, z);*/
    }

    private boolean collides(BlockType blockType, Vector3 blockPosition) {
        if(blockType == null || !blockType.isSolid()){
            return false;
        }

        Box blockBox = new Box(Constants.floor(blockPosition).add(blockType.getOffsetX(), blockType.getOffsetY(), blockType.getOffsetZ()), blockType.getSizeX(), blockType.getSizeY(), blockType.getSizeZ());

        return playerBox.intersects(blockBox);
    }
}
