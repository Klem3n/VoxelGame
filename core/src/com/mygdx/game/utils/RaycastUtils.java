package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.collision.Bounds;
import com.mygdx.game.collision.ray.CollisionRay;
import com.mygdx.game.collision.ray.RayHit;

public class RaycastUtils {
    /**
     * @param blockPosition The position
     * @param bounds        The bounds
     * @param ray           The ray
     * @return {@code True} if ray intersects the bounds
     */
    public static RayHit intersect(Vector3 blockPosition, final Bounds bounds, final CollisionRay ray) {
        Vector3 origin = ray.getOrigin();
        Vector3 direction = ray.getDirection();

        Vector3 min = blockPosition.cpy().add(bounds.getOffset());
        Vector3 max = min.cpy().add(bounds.getWidth(), bounds.getHeight(), bounds.getDepth());

        float tmin = (min.x - origin.x) / direction.x;
        float tmax = (max.x - origin.x) / direction.x;

        int axis = 0;

        if (tmin > tmax) {
            float temp = tmin;
            tmin = tmax;
            tmax = temp;
        }

        float tymin = (min.y - origin.y) / direction.y;
        float tymax = (max.y - origin.y) / direction.y;

        if (tymin > tymax) {
            float temp = tymin;
            tymin = tymax;
            tymax = temp;
        }

        if ((tmin > tymax) || (tymin > tmax)) {
            return new RayHit(ray, false);
        }

        if (tymin > tmin) {
            tmin = tymin;
            axis = 1;
        }

        if (tymax < tmax) {
            tmax = tymax;
        }

        float tzmin = (min.z - origin.z) / direction.z;
        float tzmax = (max.z - origin.z) / direction.z;

        if (tzmin > tzmax) {
            float temp = tzmin;
            tzmin = tzmax;
            tzmax = temp;
        }

        if ((tmin > tzmax) || (tzmin > tmax)) {
            return new RayHit(ray, false);
        }

        if (tzmin > tmin) {
            tmin = tzmin;
            axis = 2;
        }

        if (tzmax < tmax) {
            tmax = tzmax;
        }

        Vector3 hitDirection = new Vector3();

        switch (axis) {
            case 0:
                if (origin.x < blockPosition.x) {
                    hitDirection.x = -1;
                } else {
                    hitDirection.x = 1;
                }
                break;
            case 1:
                if (origin.y < blockPosition.y) {
                    hitDirection.y = -1;
                } else {
                    hitDirection.y = 1;
                }
                break;
            case 2:
                if (origin.z < blockPosition.z) {
                    hitDirection.z = -1;
                } else {
                    hitDirection.z = 1;
                }
                break;
        }

        if (tmin > ray.getDistance()) {
            return new RayHit(ray, false, tmin, tmax, hitDirection);
        }

        return new RayHit(ray, true, tmin, tmax, hitDirection);
    }
}
