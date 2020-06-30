package graphics;

import math.Matrix4f;
import math.Vector3f;

public class PerspectiveCamera extends Camera
{
    private float fov;

    public float aspectRatio, near, far;

    public PerspectiveCamera(Vector3f position, float aspect, float fov, float near, float far)
    {
        super(position);
        setFov(fov);

        this.aspectRatio = aspect;
        this.near = near;
        this.far = far;
    }

    public float getFov()
    {
        return fov;
    }

    public void setFov(float fov)
    {
        if (fov > 0 && fov < 180)
            this.fov = fov;
    }

    @Override
    public Matrix4f getProjectionMatrix()
    {
        return new Matrix4f().perspective(aspectRatio, fov, near, far);
    }
}
