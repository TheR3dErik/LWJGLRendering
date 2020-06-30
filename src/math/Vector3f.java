package math;

public class Vector3f
{
    public float x, y, z;

    public Vector3f()
    {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3f(Vector3f other)
    {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Vector3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float length2()
    {
        return x*x + y*y + z*z;
    }

    public float length()
    {
        return (float)Math.sqrt(x*x + y*y + z*z);
    }

    public Vector3f normalize()
    {
        float len = length();
        this.x /= len;
        this.y /= len;
        this.z /= len;
        return this;
    }

    public Vector3f add(Vector3f other)
    {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;

        return this;
    }

    public static Vector3f add(Vector3f a, Vector3f b)
    {
        return new Vector3f(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public Vector3f sub(Vector3f other)
    {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;

        return this;
    }

    public static Vector3f sub(Vector3f a, Vector3f b)
    {
        return new Vector3f(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public Vector3f set(Vector3f other)
    {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;

        return this;
    }

    public Vector3f set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public Vector3f mul(float scalar)
    {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;

        return this;
    }

    public static Vector3f mul(Vector3f a, float scalar)
    {
        return new Vector3f(a.x * scalar, a.y * scalar, a.z * scalar);
    }

    public Vector3f mul(Matrix4f mat)
    {
        float x = this.x * mat.data[0*4 + 0] + this.y * mat.data[0*4 + 1] + this.z * mat.data[0*4 + 2] + mat.data[0*4 + 3];
        float y = this.x * mat.data[1*4 + 0] + this.y * mat.data[1*4 + 1] + this.z * mat.data[1*4 + 2] + mat.data[1*4 + 3];
        float z = this.x * mat.data[2*4 + 0] + this.y * mat.data[2*4 + 1] + this.z * mat.data[2*4 + 2] + mat.data[2*4 + 3];

        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public static float dot(Vector3f a, Vector3f b)
    {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    @Override
    public String toString()
    {
        return this.x + ", " + this.y + ", " + this.z;
    }
}
