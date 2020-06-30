package graphics;

import math.Matrix4f;
import math.Vector3f;

public abstract class Camera
{
    public Vector3f position;
    public float pitch, yaw, roll;

    // if this were c++, I would just use the stack to make a new matrix every time I need it, but here I can't guarantee it won't allocate on heap,
    // so I do it only once when the camera is created
    private Matrix4f temp;

    public Camera(float x, float y, float z)
    {
        position = new Vector3f(x, y, z);
        temp = new Matrix4f();
    }

    public Camera(Vector3f position)
    {
        this(position.x, position.y, position.z);
    }

    public abstract Matrix4f getProjectionMatrix();

    public Matrix4f getViewMatrix()
    {
        Matrix4f result = new Matrix4f();
        temp.rotateX(-pitch);
        result.mul(temp);

        temp.rotateY(-yaw);
        result.mul(temp);

        temp.rotateZ(-roll);
        result.mul(temp);

        temp.translate(-position.x, -position.y, -position.z);
        result.mul(temp);

        return result;
    }

    public Vector3f getDirectionVector()
    {
        Vector3f direction = new Vector3f(0, 0, -1);

        temp.rotateX(pitch);
        direction.mul(temp);

        temp.rotateY(yaw);
        direction.mul(temp);

        return direction;
    }

    public Vector3f getLeftVector()
    {
        Vector3f result = getDirectionVector();
        float temp = result.x;

        result.x = result.z;
        result.y = 0;
        result.z = -temp;

        return result.normalize();
    }

    public Vector3f getRightVector()
    {
        Vector3f result = getDirectionVector();
        float temp = result.x;

        result.x = -result.z;
        result.y = 0;
        result.z = temp;

        return result.normalize();
    }
}
